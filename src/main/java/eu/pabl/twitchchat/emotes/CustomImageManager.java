package eu.pabl.twitchchat.emotes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.config.ModConfig;
import eu.pabl.twitchchat.emotes.minecraft.CustomImageFont;
import eu.pabl.twitchchat.emotes.minecraft.CustomImageFontStorage;
import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIBadge;
import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIBadgeSet;
import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIEmote;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CustomImageManager {
  public static final Identifier CUSTOM_IMAGE_FONT_IDENTIFIER = Identifier.of(TwitchChatMod.MOD_ID, "emote_font");

  // I've found this is a pretty good scale factor for 24x24px Twitch emotes.
  public static final float CUSTOM_IMAGE_SCALE_FACTOR = 0.3f;
  public static final String TMI_CLIENT_ID = "q6batx0epp608isickayubi39itsckt";
  public static final String EMOTE_ID_PREFIX = "emotes/";
  public static final String BADGE_ID_PREFIX = "badges/";

  private final CustomImageFont customImageFont;
  private final CustomImageFontStorage customImageFontStorage;
  private static final CustomImageManager instance = new CustomImageManager();

  // A map of the badge set name and the badges it contains.
  private final ConcurrentHashMap<String, Integer> idToCodepointHashMap;
  private final ConcurrentHashMap<String, String> emoteNameToIdHashMap;

  private final ExecutorService downloadExecutor;
  private final ScheduledExecutorService scheduledExecutor;
  private final HttpClient downloadHttpClient;

  private int currentCodepoint;
  private final int loadingImageCodepoint;

  private CustomImageManager() {
    this.idToCodepointHashMap = new ConcurrentHashMap<>();
    this.emoteNameToIdHashMap = new ConcurrentHashMap<>();
    this.currentCodepoint = 1;

    /// The order is important here. Emote font storage depends on the emote font.
    this.customImageFont = new CustomImageFont();
    this.customImageFontStorage = new CustomImageFontStorage(this.getCustomImageFont());

    this.downloadHttpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(20))
      .build();

    this.downloadExecutor = Executors.newCachedThreadPool();
    this.scheduledExecutor = Executors.newScheduledThreadPool(1);

    this.loadingImageCodepoint = this.addLoadingIcon();
    this.loadCache();
  }
  public static CustomImageManager getInstance() {
    return instance;
  }

  // Returns the loading icon loading character codepoint.
  private int addLoadingIcon() {
    try {
      InputStream is = TwitchChatMod.class.getResourceAsStream("/data/twitchchat/textures/loading.png");
      if (is == null) {
        throw new IOException("loading.png InputStream is null");
      }
      NativeImage image = NativeImage.read(is);
      int codepoint = getAndAdvanceCurrentCodepoint();
      int advance = (int) (image.getWidth() * CUSTOM_IMAGE_SCALE_FACTOR) + 1; // the +1 is to account for the shadow, which is a pixel in length
      int ascent = (int) (image.getHeight() * CUSTOM_IMAGE_SCALE_FACTOR);
      this.getCustomImageFont().addGlyph(codepoint,
        new CustomImageFont.CustomImageGlyph(CUSTOM_IMAGE_SCALE_FACTOR, image, 0, 0, image.getWidth(),
          image.getHeight(), advance, ascent, "loading"));
      return codepoint;
    } catch (IOException e) {
      throw new RuntimeException("Error loading 'loading.png' texture for font: " + e.getMessage());
    }
  }

  public void loadCache() {
    try {
      CustomImageCache.CacheEntry[] allCachedFiles = CustomImageCache.getInstance().getAllCachedFiles();
      for (var entry : allCachedFiles) {
        InputStream is = new FileInputStream(entry.path().toFile());
        addImage(is, entry.id(), false);
      }
    } catch (IOException e) {
      TwitchChatMod.LOGGER.error("Couldn't load cache due to error {}", e);
      throw new RuntimeException(e);
    }
  }

  /* These handle emoji downloading, they accept any of the possible urls' return formats.
     Possible urls: - /chat/emotes?broadcaster_id=
                    - /chat/emotes/global
                    - /char/emotes/set?emote_set_id
     And add the emotes to the CustomImageFont and the HashMap emoteName -> codepoint.
   */
  public void downloadImagePack(String urlStr, ImageTypes type) {
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create(urlStr))
      .timeout(Duration.ofMinutes(2))
      .header("Authorization", "Bearer " + ModConfig.getConfig().getOauthKey().replace("oauth:", ""))
      .header("Client-Id", TMI_CLIENT_ID)
      .build();

    executeRunnable(() -> {
      try {
        HttpResponse<String> res = this.downloadHttpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
          TwitchChatMod.LOGGER.warn("Couldn't load image from url {}: status code {}", urlStr, res.statusCode());
          return;
        }

        JsonObject jsonObject = (JsonObject) JsonParser.parseString(res.body());
        Gson gson = new Gson();
        Stream<JsonElement> data = jsonObject.getAsJsonArray("data")
          .asList()
          .stream();
        if (type == ImageTypes.EMOTE) {
          data.map(emote -> gson.fromJson(emote, TwitchAPIEmote.class))
            .forEach(twitchEmote -> this.executeRunnable(() -> downloadEmote(twitchEmote)));
        } else if (type == ImageTypes.BADGE) {
          data.parallel()
            .map(badgeSet -> gson.fromJson(badgeSet, TwitchAPIBadgeSet.class))
            .forEach(this::downloadBadgeSet);
        }
      } catch (SocketException e) {
        TwitchChatMod.LOGGER.warn("Couldn't load images from url {}: error {}", urlStr, e);
        this.scheduleRunnable(() -> downloadImagePack(urlStr, type), 30, TimeUnit.SECONDS);
      }
    });
  }

  private void downloadEmote(TwitchAPIEmote twitchEmote) throws IOException {
    String emoteId = EMOTE_ID_PREFIX + twitchEmote.id();
    // I've we've already downloaded the emote, do not download it again.
    if (this.idToCodepointHashMap.containsKey(emoteId)) {
      return;
    }

    // Do this earlier, so that if the emotes take long to load, at least we can show the user that the
    // emote hasn't loaded.
    this.emoteNameToIdHashMap.put(twitchEmote.name(), emoteId);

    String url1x = twitchEmote.images().get("url_1x");
    URL url = new URL(url1x);

    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      if (connection.getResponseCode() != 200) {
        TwitchChatMod.LOGGER.warn("Couldn't load emote 1x {} from string {}: status code {}",
          twitchEmote.name(), url1x, connection.getResponseCode());
        return;
      }

      addImage(url.openStream(), emoteId, true);

      TwitchChatMod.LOGGER.info("Loaded emote {}", twitchEmote.name());
    } catch (SocketException e) {
      TwitchChatMod.LOGGER.warn("Couldn't load emote 1x {} from string {}: error {}",
        twitchEmote.name(), url1x, e);
      this.scheduleRunnable(() -> downloadEmote(twitchEmote), 30, TimeUnit.SECONDS);
    }
  }

  private void downloadBadgeSet(TwitchAPIBadgeSet badgeSet) {
    for (var badge : badgeSet.versions()) {
      executeRunnable(() -> downloadBadge(badgeSet.set_id(), badge));
    }
  }
  private void downloadBadge(String badgeSetId, TwitchAPIBadge badge) throws IOException {
    // I've we've already downloaded the emote, do not download it again.
    String badgeId = BADGE_ID_PREFIX + badgeSetId + "/" + badge.id();
    if (this.idToCodepointHashMap.containsKey(badgeId))
      return;

    String url1x = badge.image_url_1x();
    URL url = new URL(url1x);
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      if (connection.getResponseCode() != 200) {
        TwitchChatMod.LOGGER.warn("Couldn't load badge 1x {} from string {}: status code {}",
          badge.id(), url1x, connection.getResponseCode());
        return;
      }

      addImage(url.openStream(), badgeId, true);

      TwitchChatMod.LOGGER.debug("Loaded badge {}", badgeId);
    } catch (SocketException e) {
      TwitchChatMod.LOGGER.warn("Couldn't load badge 1x {} from string {}: error {}",
        badgeId, url1x, e);
      this.scheduleRunnable(() -> downloadBadge(badgeSetId, badge), 30, TimeUnit.SECONDS);
    }
  }

  private void addImage(InputStream stream, String id, boolean writeToDisk) throws IOException {
    NativeImage image = NativeImage.read(stream);
    if (writeToDisk)
      image.writeTo(CustomImageCache.getInstance().getPngFile(id));

    int codepoint = getAndAdvanceCurrentCodepoint();
    // advance is the amount the text is moved forward after the character
    int advance = (int) (image.getWidth() * CUSTOM_IMAGE_SCALE_FACTOR) + 1; // the +1 is to account for the shadow, which is a pixel in length
    // TODO: It would be really cool to be able to add or remove the +1 depending on if we're rendering a shadow or
    //       not. This could be done through a mixin in TextRenderer.Drawer#accept.
    // ascent is the height of the glyph relative to something
    int ascent = (int) (image.getHeight() * CUSTOM_IMAGE_SCALE_FACTOR);
    // both advance and ascent seem to correlate pretty well with its scale factor
    this.getCustomImageFont().addGlyph(codepoint,
      new CustomImageFont.CustomImageGlyph(CUSTOM_IMAGE_SCALE_FACTOR, image, 0, 0, image.getWidth(),
        image.getHeight(), advance, ascent, id));

    this.idToCodepointHashMap.put(id, codepoint);
    TwitchChatMod.LOGGER.info("Added image with id {}", id);
  }

  public void downloadEmoteSet(String emoteSetId) {
    this.downloadImagePack("https://api.twitch.tv/helix/chat/emotes/set?emote_set_id=" + emoteSetId, ImageTypes.EMOTE);
  }
  public void downloadChannelEmotes(String channelId) {
    this.downloadImagePack("https://api.twitch.tv/helix/chat/emotes?broadcaster_id=" + channelId, ImageTypes.EMOTE);
  }
  public void downloadChannelBadges(String channelId) {
    this.downloadImagePack("https://api.twitch.tv/helix/chat/badges?broadcaster_id=" + channelId, ImageTypes.BADGE);
  }

  private void executeRunnable(FailingRunnable r) {
    this.downloadExecutor.execute(r.toRunnable());
  }
  private void scheduleRunnable(FailingRunnable r, long delay, TimeUnit timeunit)  {
    this.scheduledExecutor.schedule(r.toRunnable(), delay, timeunit);
  }

  private synchronized int getAndAdvanceCurrentCodepoint() {
    int prevCodepoint = currentCodepoint;
    currentCodepoint++;
    // Skip the space (' ') codepoint, because the TextRenderer does weird stuff with the space character
    // (like it doesn't get obfuscated and stuff).
    if (currentCodepoint == 32) currentCodepoint++;
    return prevCodepoint;
  }

  public String getEmoteIdFromName(String emoteName) {
    return this.emoteNameToIdHashMap.get(emoteName);
  }
  public Integer getCodepoint(String id) {
    return this.idToCodepointHashMap.getOrDefault(id, this.loadingImageCodepoint);
  }
  public CustomImageFont getCustomImageFont() {
    return this.customImageFont;
  }
  public CustomImageFontStorage getCustomImageFontStorage() {
    return this.customImageFontStorage;
  }
  public Integer getEmoteCodepointFromId(String emoteSubId) {
    return this.getCodepoint("emotes/" + emoteSubId);
  }
  public Integer getBadgeCodepointFromId(String badgeSubId) {
    return this.getCodepoint("badges/" + badgeSubId);
  }

  public enum ImageTypes {
    BADGE,
    EMOTE
  }
}
