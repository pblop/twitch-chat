package eu.pabl.twitchchat.badge;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;

public class BadgeSet {
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
  public Badge getChannelOnly(String channelID, String name) {
    Badge.ChannelOverride override = get(name).getChannelOverride(channelID);
    if (override == null) throw new IllegalArgumentException("badge named '" + name + "' does not exist for channel '" + channelID + "'");
    return override.toBadge();
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

  /**
   * Adds a new global badge. Use {@link BadgeSet#add(String channelID, int codepoint, Badge badge)} to add a channel
   * specific badge instead.
   * @param codePoint The code point to access the badge later.
   * @param badge The badge to add as a global badge.
   */
  public void add(int codePoint, @NotNull Badge badge) {
    badges.put(codePoint, badge);
  }

  /**
   * Adds a new channel specific badge. Use {@link BadgeSet#add(int, Badge)} to add a global badge instead.
   * @param channelID The channel ID to add this badge to.
   * @param codePoint The code point to access the badge later.
   * @param badge The badge to add as a global badge.
   */
  public void add(String channelID, int codePoint, @NotNull Badge badge) {
    Badge parentBadge;
    try {
      parentBadge = get(badge.getName());
    } catch (IllegalArgumentException ignored) {
      parentBadge = new Badge(badge);
      add(codePoint, badge);
    }
    parentBadge.setChannelOverride(channelID, codePoint, badge.image());
  }
}
