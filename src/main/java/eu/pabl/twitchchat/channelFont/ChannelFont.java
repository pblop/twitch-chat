package eu.pabl.twitchchat.channelFont;

import eu.pabl.twitchchat.TwitchChatMod;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelFont implements Font {
    public static final Identifier CHANNEL_ICON_FONT_STORAGE = Identifier.of("twitchchat", "channel_icon2");
    public static final List<Font.FontFilterPair> CHANNEL_ICON_FONT_FILTER = List.of(new Font.FontFilterPair(new ChannelFont(), FontFilterType.FilterMap.NO_FILTER));

    @Override
    public void close() {
        Font.super.close();
    }

    @Nullable
    @Override
    public Glyph getGlyph(int codePoint) {
        return Font.super.getGlyph(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        IntSet set = IntSet.of(20);
        return set;
    }
}
