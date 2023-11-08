package eu.pabl.twitchchat.emotes.minecraft;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.function.Function;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;

public class CustomImageFont implements Font {
  private final GlyphContainer<CustomImageGlyph> glyphs;

  public CustomImageFont() {
    this.glyphs = new GlyphContainer<>(CustomImageGlyph[]::new, rows -> new CustomImageGlyph[rows][]);
  }

  @Override
  public void close() {
//    this.image.close();
  }

  public synchronized void addGlyph(int codepoint, CustomImageGlyph glyph) {
    this.glyphs.put(codepoint, glyph);
  }

  @Override
  @Nullable
  public synchronized Glyph getGlyph(int codePoint) {
    return this.glyphs.get(codePoint);
  }

  @Override
  public synchronized IntSet getProvidedGlyphs() {
    return IntSets.unmodifiable(this.glyphs.getProvidedGlyphs());
  }

  public record CustomImageGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent, String id) implements Glyph
  {
    @Override
    public float getAdvance() {
      return this.advance;
    }

    @Override
    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
      return function.apply(new CustomImageRenderableGlyph(){
        @Override
        public float getOversample() {
          return 1.0f / scaleFactor;
        }

        @Override
        public int getWidth() {
          return width;
        }

        @Override
        public int getHeight() {
          return height;
        }

        @Override
        public float getAscent() {
          return CustomImageRenderableGlyph.super.getAscent() + 7.0f - (float)ascent;
        }

        @Override
        public void upload(int x, int y) {
          image.upload(0, x, y, 0, 0, width, height, false, false);
        }

        @Override
        public boolean hasColor() {
          return image.getFormat().getChannelCount() > 1;
        }

        @Override
        public String getId() {
          return id;
        }
      });
    }
  }
}

