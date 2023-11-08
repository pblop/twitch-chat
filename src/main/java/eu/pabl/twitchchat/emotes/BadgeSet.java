package eu.pabl.twitchchat.emotes;

import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIBadgeSet;

import java.util.HashMap;
import java.util.Objects;

public class BadgeSet {
  private final String id;
  private final HashMap<String, Badge> versions;

  private BadgeSet(String id, HashMap<String, Badge> versions) {
    this.id = id;
    this.versions = versions;
  }

  public static BadgeSet fromTwitchAPIBadgeSet(TwitchAPIBadgeSet apiBS) {
    HashMap<String, Badge> versionsMap = new HashMap<>();
    for (var version : apiBS.versions()) {
      versionsMap.put(version.id(), new Badge(version.id(), version.image_url_1x()));
    }

    return new BadgeSet(apiBS.set_id(), versionsMap);
  }

  public String getId() {
    return id;
  }
  public HashMap<String, Badge> getVersions() {
    return versions;
  }

  // A badge where we only store what we need for displaying badges.
  public static final class Badge {
    private final String id;
    private final String imageUrl1x;
    private boolean isDownloaded;

    // TODO: Maybe add hover stuff, like a title, description, and the click url.
    public Badge(String id, String imageUrl1x) {
      this.id = id;
      this.imageUrl1x = imageUrl1x;
      this.isDownloaded = false;
    }

    public void markAsDownloaded() {
      this.isDownloaded = true;
    }

    public String getId() {
      return id;
    }
    public String getImageUrl1x() {
      return imageUrl1x;
    }
    public boolean isDownloaded() {
      return this.isDownloaded;
    }
  }
}

