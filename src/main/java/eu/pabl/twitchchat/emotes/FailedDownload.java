package eu.pabl.twitchchat.emotes;

import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIObject;

public record FailedDownload(
  TwitchAPIObject object,
  String string,
  FailedDownloadType failedDownloadType
) {
  public enum FailedDownloadType {
    BADGE_PACK, // object (-), string (url)
    EMOTE_PACK, // object (-), string (url)
    BADGE,      // object (TwitchAPIBadge), string (setId)
    EMOTE       // object (TwitchAPIEmote), string (-)
  }
}
