package eu.pabl.twitchchat.channelFont;

import eu.pabl.twitchchat.TwitchChatMod;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class ChannelFont implements Font {
    public static final Identifier CHANNEL_ICON_FONT_STORAGE = Identifier.of("twitchchat", "channel_icon2");
    public static final List<Font.FontFilterPair> CHANNEL_ICON_FONT_FILTER = List.of(new Font.FontFilterPair(new ChannelFont(), FontFilterType.FilterMap.NO_FILTER));
    private static final int BADGE_SIZE = 8;

    @Override
    public void close() {
        Font.super.close();
    }

    @Nullable
    @Override
    public Glyph getGlyph(int codePoint) {

        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        NativeImage image;

        // reload font storage
        //FontStorage fontStorage = new FontStorage(MinecraftClient.getInstance().getTextureManager(), CHANNEL_ICON_FONT_STORAGE);
        //fontStorage.setFonts(CHANNEL_ICON_FONT_FILTER, null);
        //TwitchChatMod.LOGGER.info("triggered set fonts");

        String badgeName = switch (codePoint) {
            case 33 -> "broadcaster";
            case 34 -> "moderator";
            case 35 -> "partner";
            case 36 -> "vip";
            default ->  null;
        };
        if (badgeName == null) {
            TwitchChatMod.LOGGER.error("unknown code point '" + codePoint + "' in badges font!");
            return Font.super.getGlyph(codePoint);
        }

        try {
            image = TextureContents.load(resourceManager, Identifier.of("twitchchat", "textures/badge/" + badgeName + ".png")).image();
        } catch (IOException e) {
            TwitchChatMod.LOGGER.error("couldn't load texture contents for '" + badgeName + "' badge: " + e);
            return Font.super.getGlyph(codePoint);
        }

        float scaleFactor = (float) BADGE_SIZE / image.getWidth();
        BitmapFont.BitmapFontGlyph glyph = new BitmapFont.BitmapFontGlyph(scaleFactor, image, 0, 0, image.getWidth(), image.getHeight(), BADGE_SIZE+1, BADGE_SIZE);
        return glyph;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        IntSet set = IntSet.of(33, 34, 35, 36);
        return set;
    }
}
