package eu.pabl.twitchchat.emotes;

import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIBadge;
import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIEmote;

import javax.annotation.Nullable;

public final class DownloadableImage {
  public static final String EMOTE_ID_PREFIX = "emotes/";
  public static final String BADGE_ID_PREFIX = "badges/";

  private final String id; // The internal (mod) id of this element,
  private final String url; // The url from which to download this element
  private final ImageTypes imageType;
  private final String emoteName; // If it's an emote, its name

  private DownloadableImage(String id, String url, ImageTypes imageType, String emoteName) {
    this.id = id;
    this.url = url;
    this.imageType = imageType;
    this.emoteName = emoteName;
  }

  // Factories to generate from the Twitch API bases.
  public static DownloadableImage fromTwitchAPIElement(String badgeSetId, TwitchAPIBadge badge) {
    return new DownloadableImage(
      BADGE_ID_PREFIX + badgeSetId + "/" + badge.id(),
      badge.image_url_1x(),
      ImageTypes.BADGE,
      null
    );
  }
  public static DownloadableImage fromTwitchAPIElement(TwitchAPIEmote emote) {
    return new DownloadableImage(
      EMOTE_ID_PREFIX + emote.id(),
      emote.images().get("url_1x"),
      ImageTypes.EMOTE,
      emote.name()
    );
  }

  public String getId() {
    return id;
  }
  public String getUrl() {
    return url;
  }
  public ImageTypes getImageType() {
    return imageType;
  }
  // Will be null if the image type is not an emote.
  @Nullable
  public String getEmoteName() {
    return emoteName;
  }

  public enum ImageTypes {
    BADGE,
    EMOTE
  }
}
