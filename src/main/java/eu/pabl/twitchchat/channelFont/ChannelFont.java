package eu.pabl.twitchchat.channelFont;

import eu.pabl.twitchchat.TwitchChatMod;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ChannelFont implements Font {
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
