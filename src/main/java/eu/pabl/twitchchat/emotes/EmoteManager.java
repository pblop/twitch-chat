package eu.pabl.twitchchat.emotes;

import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class EmoteManager {
  public static final Identifier EMOTE_FONT_IDENTIFIER = Identifier.of(TwitchChatMod.MOD_ID, "emote_font");

  public final EmoteFont emoteFont;
  public final EmoteFontStorage emoteFontStorage;
  private static final EmoteManager instance = new EmoteManager();

  private final HashMap<String, Integer> emoteNameToCodepointHashMap;
  private int currentCodepoint;

  private EmoteManager() {
    this.emoteNameToCodepointHashMap = new HashMap<>();
    this.currentCodepoint = 40;

    /// The order is important here. Emote font storage depends on the emote font.
    this.emoteFont = new EmoteFont();
    this.emoteFontStorage = new EmoteFontStorage(this.getEmoteFont());
  }
  public static EmoteManager getInstance() {
    return instance;
  }

  public void downloadEmote() {
    Thread t = new Thread(() -> {
      try {
        URL url = new URL("https://static-cdn.jtvnw.net/emoticons/v2/47/static/light/1.0");
        NativeImage image = NativeImage.read(url.openStream());
        String emoteName = "Kappa";
        int codepoint = getAndAdvanceCurrentCodepoint();
        this.getEmoteFont().addGlyph(codepoint, new EmoteFont.EmoteGlyph(0.5f, image, 0, 0, image.getWidth(), image.getHeight()*2, image.getWidth()/2, image.getWidth()/2, emoteName));
        this.emoteNameToCodepointHashMap.put(emoteName, codepoint);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    t.setDaemon(true);
    t.start();
  }

  private int getAndAdvanceCurrentCodepoint() {
    int prevCodepoint = currentCodepoint;
    currentCodepoint++;
    // Skip the space (' ') codepoint, because the TextRenderer does weird stuff with the space character
    // (like it doesn't get obfuscated and stuff).
    if (currentCodepoint == 32) currentCodepoint++;
    return prevCodepoint;
  }
  public Integer getCodepoint(String emote) {
    return this.emoteNameToCodepointHashMap.get(emote);
  }
  public EmoteFont getEmoteFont() {
    return this.emoteFont;
  }
  public EmoteFontStorage getEmoteFontStorage() {
    return this.emoteFontStorage;
  }

}