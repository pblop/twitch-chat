package eu.pabl.twitchchat.badge;

import eu.pabl.twitchchat.TwitchChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Badge {
    private final String name;
    private MutableText displayName;
    private Text description;
    Map<String, ChannelOverride> channelOverrides = new HashMap<>();
    int codepoint;
    NativeImage image;
    NativeImage resourcePackOverrideImage;

    /**
     * An empty badge with a name set to "" and null image.
     */
    public static final Badge EMPTY = new Badge("", null);

    Badge(String name, NativeImage image) {
        this.name = name;
        this.image = image;
    }

    /**
     * A clone constructor to clone a badge.
     * @param badge the badge to clone
     */
    Badge(Badge badge) {
        this.name = badge.name;
        this.displayName = badge.displayName;
        this.description = badge.description;
        this.channelOverrides = badge.channelOverrides;
        this.codepoint = badge.codepoint;
        this.image = badge.image;
        this.resourcePackOverrideImage = badge.resourcePackOverrideImage;
    }

    /**
     * @return The name of the badge
     */
    public String getName() {
        return name;
    }

    /**
     * Adds or replaces an override for the given channel.
     * @param channelID The channel ID to add the override for.
     * @param codepoint The codepoint to use this override in a text.
     * @param image The image to use for that channel.
     * @throws IllegalStateException when this badge is not allowed to have channel overrides e.g. it is already a
     * channel override.
     */
    void setChannelOverride(@NotNull String channelID, int codepoint, NativeImage image) throws IllegalStateException {
        if (channelOverrides == null) throw new IllegalStateException("This badge cant have overrides (is it an channel override?");
        this.channelOverrides.put(channelID, new ChannelOverride(channelID, codepoint, image));
    }

    /**
     * Gets the {@link ChannelOverride} for a channel.
     * @param channelID The channelID to get the override from
     * @return The override for the given channel, or null if this badge contains no override for the channel.
     */
    public @Nullable ChannelOverride getChannelOverride(@NotNull String channelID) {
        if (this.channelOverrides == null) return null;
        return this.channelOverrides.get(channelID);
    }

    /**
     * Gets the {@link ChannelOverride} for a channel.
     * @param codePoint The code point to search the override for.
     * @return The override for the given channel, or null if this badge contains no override for the channel.
     */
    public @Nullable ChannelOverride getChannelOverride(int codePoint) {
        if (this.channelOverrides == null) return null;
        for (ChannelOverride override : this.channelOverrides.values()) {
            if (override.getCodepoint() == codePoint) return override;
        }
        return null;
    }

    /**
     * Clears all channel overrides of this badge, if any.
     */
    public void clearChannelOverrides() {
        this.channelOverrides.clear();
    }

    /**
     * @return The display text of the badge.
     */
    public MutableText getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The updated display text.
     */
    public void setDisplayName(MutableText displayName) {
        this.displayName = displayName;
    }

    /**
     * @param displayName The updated display text.
     */
    public void setDisplayName(String displayName) {
        setDisplayName(Text.literal(displayName));
    }

    /**
     * @return Whether the badge has a display name.
     */
    public boolean hasDisplayName() {
        return this.getDisplayName() != null && !Objects.equals(this.getDisplayName().getLiteralString(), "");
    }

    /**
     * @return The description of the badge.
     */
    public Text getDescription() {
        return description;
    }

    /**
     * @param description The updated description text.
     */
    public void setDescription(String description) {
        this.description = Text.literal(description).styled(style -> style.withColor(Formatting.GRAY).withItalic(true));
    }

    /**
     * @return Whether the badge has a description.
     */
    public boolean hasDescription() {
        return this.getDescription() != null && !Objects.equals(this.getDescription().getLiteralString(), "");
    }

    public HoverEvent getHoverEvent() {
        MutableText hoverText = Text.literal(this.name);
        if (this.hasDisplayName()) {
            hoverText.append(getDisplayName());
        }
        if (this.hasDescription()) {
            hoverText.append("\n").append(this.getDescription());
        }
        hoverText.append(Text.literal("\n" + this.name).styled(style -> style
            .withColor(Formatting.DARK_GRAY)
        ));
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
    }

    /**
     * @return The code point to use this badge in a text.
     */
    int getCodepoint() {
        return this.codepoint;
    }

    /**
     * @return The code point to use this badge in a text.
     */
    public String getChar() {
        return Character.toString((char) this.codepoint);
    }

    /**
     * @param codepoint The new code point.
     */
    void setCodepoint(int codepoint) {
        this.codepoint = codepoint;
    }

    /**
     * @return The ready to use text component of the badge.
     */
    public Text toText() {
        return Text.literal(this.getChar()).styled(style -> style
            .withFont(BadgeFont.IDENTIFIER)
            .withHoverEvent(this.getHoverEvent())
        );
    }

    /**
     * @return The image of the badge
     */
    public NativeImage image() {
        return hasResourcePackOverride() ? this.resourcePackOverrideImage : this.image;
    }

    public boolean hasResourcePackOverride() {
        return this.resourcePackOverrideImage != null;
    }

    public void unsetResourcePackOverride() {
        this.resourcePackOverrideImage = null;
    }

    /**
     * Loads the badges from the applied resource packs
     */
    public static void loadBadges() {
        String startingPath = "textures/badge";
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        ResourceFinder finder = new ResourceFinder(startingPath, ".png");

        Map<Identifier, Resource> resources = finder.findResources(resourceManager);

        if (resources.isEmpty()) {
            return;
        }

        final String regex = startingPath + "/(?:global|channel/(?<channelName>[a-z0-1_]+))/(?<badgeName>[a-z0-1_]+)\\.png";
        final Pattern pattern = Pattern.compile(regex);
        resources.forEach((identifier, resource) -> {
            if (!identifier.getNamespace().equals(BadgeFont.IDENTIFIER.getNamespace())) return;
            Matcher matcher = pattern.matcher(identifier.getPath());
            if (!matcher.matches()) return;

            String channelID = null;
            try {
                channelID = matcher.group("channelName");
            } catch (IllegalArgumentException ignored) {}

            NativeImage image;
            try {
                image = NativeImage.read(resource.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String name = matcher.group("badgeName");
            Badge badge = new Badge(name, null);
            badge.resourcePackOverrideImage = image;
            if (channelID == null) {
                TwitchChatMod.BADGES.add(badge);
            } else {
                TwitchChatMod.BADGES.add(channelID, badge);
            }
        });
    }

    public class ChannelOverride {
        final String channelID;
        private final int codepoint;
        final NativeImage image;
        NativeImage resourcePackOverrideImage;

        private ChannelOverride(String channelID, int codepoint, NativeImage image) {
            this.channelID = channelID;
            this.codepoint = codepoint;
            this.image = image;
        }

        /**
         * @return The name of the badge
         */
        public String getChannelID() {
            return channelID;
        }

        /**
         * @return The code point to use this badge in a text.
         */
        int getCodepoint() {
            return this.codepoint;
        }

        /**
         * @return The image of the channel specific override
         */
        public NativeImage image() {
            if (this.resourcePackOverrideImage != null) {
                return this.resourcePackOverrideImage;
            } else if (Badge.this.resourcePackOverrideImage != null) {
                return Badge.this.resourcePackOverrideImage;
            } else {
                return this.image;
            }
        }

        public boolean hasResourcePackOverride() {
            return this.resourcePackOverrideImage != null || Badge.this.hasResourcePackOverride();
        }

        public void unsetResourcePackOverride() {
            this.resourcePackOverrideImage = null;
        }

        /**
         * Turn this override into an actual badge to use.
         * <p> The returned badge cant have channel overrides. Trying to set a channel override on this badge will throw
         * an {@link IllegalStateException}.
         * @return the badge representation of this override.
         */
        Badge toBadge() {
            Badge badge = new Badge(Badge.this);
            badge.channelOverrides = null;
            badge.codepoint = this.codepoint;
            badge.image = this.image;
            badge.resourcePackOverrideImage = this.resourcePackOverrideImage;
            return badge;
        }
    }
}
