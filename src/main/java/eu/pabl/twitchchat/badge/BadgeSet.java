package eu.pabl.twitchchat.badge;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;

public class BadgeSet {
  private static final char MIN_CHAR = ' ' + 1;
  private char allCodePoint;
  private final Int2ObjectMap<Badge> badges = new Int2ObjectOpenHashMap<>();

  public IntSet codePoints() {
    return badges.keySet();
  }

  /**
   * Access the badge for the given code point.
   * @param codePoint The code point to search the badge for.
   * @return The badge for the code point or empty badge.
   */
  public Badge get(int codePoint) {
    return badges.getOrDefault(codePoint, Badge.EMPTY);
  }

  /**
   * Access the global badge for the given name. You may want to use
   * {@link BadgeSet#get(String channelID, String name)} instead to also include the channel specific badges.
   * @param name The name to search the badge for.
   * @return The badge for the name.
   * @throws IllegalArgumentException If the given name is not a global badge.
   */
  public Badge get(String name) throws IllegalArgumentException {
    return badges.values().stream()
        .filter(badge -> badge.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("badge named '" + name + "' does not exist"));
  }

  /**
   * Access the badge for the given name. First search if the given channel ID has this badge. If not, search for the
   * global badge.
   * @param channelID The channel ID to include in the search.
   * @param name The name to search the badge for.
   * @return The badge for the name.
   * @throws IllegalArgumentException If the given name is neither a channel badge nor a global badges.
   */
  public Badge get(String channelID, String name) throws IllegalArgumentException {
    Badge badge = get(name);
    Badge.ChannelOverride override = badge.getChannelOverride(channelID);
    if (override == null) return badge;
    return override.toBadge();
  }

  /**
   * Access the badge for the given name. Unlike {@link BadgeSet#get(String channelID, String name)} this method only
   * searches for a channel badge but not a global badge.
   * @param channelID The channel ID of the badge.
   * @param name The name to search the badge for.
   * @return The badge for the name.
   * @throws IllegalArgumentException If the given name is neither a channel badge nor a global badges.
   */
  public Badge.ChannelOverride getChannelOnly(String channelID, String name) {
    Badge.ChannelOverride override = get(name).getChannelOverride(channelID);
    if (override == null) throw new IllegalArgumentException("badge named '" + name + "' does not exist for channel '" + channelID + "'");
    return override;
  }

  /**
   * Get the code point string for the given global badge name. You may want to use
   * {@link BadgeSet#getChar(String channelID, String name)} instead to also include the channel specific badges.
   * @param name The name to search the badge for.
   * @return The code point string to use this badge in a text.
   * @throws IllegalArgumentException If the given name is not a global badge.
   */
  public String getChar(String name) throws IllegalArgumentException {
    return get(name).getChar();
  }

  /**
   * Get the code point string for the given badge name. First search if the given channel ID has this badge. If not,
   * search for the global badge.
   * @param channelID The channel ID to include in the search.
   * @param name The name to search the badge for.
   * @return The code point string to use this badge in a text.
   * @throws IllegalArgumentException If the given name is neither a channel badge nor a global badge.
   */
  public String getChar(String channelID, String name) throws IllegalArgumentException {
    return get(channelID, name).getChar();
  }

  public void clearResourcePackOverrides() {
    badges.values().stream()
        .filter(Badge::hasResourcePackOverride)
        .forEach(Badge::unsetResourcePackOverride);
  }

  /**
   * Adds a new global badge. Use {@link BadgeSet#add(String channelID, Badge badge)} to add a channel specific badge
   * instead.
   * @param badge The badge to add as a global badge.
   */
  public void add(@NotNull Badge badge) {
    Badge badgeBefore;
    try {
      badgeBefore = get(badge.getName());
    } catch (IllegalArgumentException ignored){
      put(badge);
      return;
    }
    put(badge, badgeBefore);
  }

  /**
   * Adds a new channel specific badge. Use {@link BadgeSet#add(Badge)} to add a global badge instead.
   * @param channelID The channel ID to add this badge to.
   * @param badge The badge to add as a global badge.
   */
  public void add(String channelID, @NotNull Badge badge) {
    if (channelID == null) {
      add(badge);
      return;
    }
    Badge parentBadge;
    try {
      parentBadge = get(badge.getName());
    } catch (IllegalArgumentException ignored) {
      parentBadge = new Badge(badge);
      add(badge);
    }

    int codePoint;
    try {
      codePoint = getChannelOnly(channelID, badge.getName()).getCodepoint();
    } catch (IllegalArgumentException ignored) {
      codePoint = (allCodePoint++) + MIN_CHAR;
    }
    parentBadge.setChannelOverride(channelID, codePoint, badge.image());
  }

  /**
   * @param badge The badge to add.
   */
  private void put(@NotNull Badge badge) {
    int codePoint = (allCodePoint++) + MIN_CHAR;
    badge.codepoint = codePoint;
    badges.put(codePoint, badge);
  }

  /**
   * @param badge The badge to add.
   * @param badgeBefore The badge to replace (same name, same channel)
   */
  private void put(@NotNull Badge badge, Badge badgeBefore) {
    if (badgeBefore == null) {
      put(badge);
      return;
    }

    if (badge.image == null) {
      badge.image = badgeBefore.image;
    }
    if (!badge.hasResourcePackOverride()) {
      badge.resourcePackOverrideImage = badgeBefore.resourcePackOverrideImage;
    }
    if (!badge.hasDisplayName()) {
      badge.setDisplayName(badgeBefore.getDisplayName());
    }
    badge.codepoint = badgeBefore.codepoint;
    badges.put(badge.codepoint, badge);
  }
}
