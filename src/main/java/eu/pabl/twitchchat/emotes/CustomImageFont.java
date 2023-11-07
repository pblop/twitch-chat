package eu.pabl.twitchchat.emotes;

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
//  private final NativeImage image;
  private final GlyphContainer<CustomImageGlyph> glyphs;

  public CustomImageFont() {
    this.glyphs = new GlyphContainer<>(CustomImageGlyph[]::new, rows -> new CustomImageGlyph[rows][]);
  }

  @Override
  public void close() {
//    this.image.close();
  }

  public void addGlyph(CustomImageGlyph glyph) {
    this.glyphs.put(glyph.codepoint(), glyph);
  }

  @Override
  @Nullable
  public Glyph getGlyph(int codePoint) {
    return this.glyphs.get(codePoint);
  }

  @Override
  public IntSet getProvidedGlyphs() {
    return IntSets.unmodifiable(this.glyphs.getProvidedGlyphs());
  }

  public record CustomImageGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent, String emoteString, int codepoint) implements Glyph
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
        public int getCodepoint() {
          return codepoint;
        }
      });
    }
  }

//  public record Loader(Identifier file, int height, int ascent, int[][] codepointGrid) implements FontLoader
//  {
//    private static final Codec<int[][]> CODE_POINT_GRID_CODEC = Codecs.validate(Codec.STRING.listOf().xmap(strings -> {
//      int i = strings.size();
//      int[][] is = new int[i][];
//      for (int j = 0; j < i; ++j) {
//        is[j] = ((String)strings.get(j)).codePoints().toArray();
//      }
//      return is;
//    }, codePointGrid -> {
//      ArrayList<String> list = new ArrayList<String>(((int[][])codePointGrid).length);
//      for (int[] is : codePointGrid) {
//        list.add(new String(is, 0, is.length));
//      }
//      return list;
//    }), Loader::validateCodePointGrid);
//    public static final MapCodec<Loader> CODEC = Codecs.validate(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("file")).forGetter(Loader::file), Codec.INT.optionalFieldOf("height", 8).forGetter(Loader::height), ((MapCodec)Codec.INT.fieldOf("ascent")).forGetter(Loader::ascent), ((MapCodec)CODE_POINT_GRID_CODEC.fieldOf("chars")).forGetter(Loader::codepointGrid)).apply((Applicative<Loader, ?>)instance, Loader::new)), Loader::validate);
//
//    private static DataResult<int[][]> validateCodePointGrid(int[][] codePointGrid) {
//      int i = codePointGrid.length;
//      if (i == 0) {
//        return DataResult.error(() -> "Expected to find data in codepoint grid");
//      }
//      int[] is = codePointGrid[0];
//      int j = is.length;
//      if (j == 0) {
//        return DataResult.error(() -> "Expected to find data in codepoint grid");
//      }
//      for (int k = 1; k < i; ++k) {
//        int[] js = codePointGrid[k];
//        if (js.length == j) continue;
//        return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + js.length + " codepoints, expected: " + j + "), pad with \\u0000");
//      }
//      return DataResult.success(codePointGrid);
//    }
//
//    private static DataResult<Loader> validate(Loader fontLoader) {
//      if (fontLoader.ascent > fontLoader.height) {
//        return DataResult.error(() -> "Ascent " + fontLoader.ascent + " higher than height " + fontLoader .height);
//      }
//      return DataResult.success(fontLoader);
//    }
//
//    @Override
//    public FontType getType() {
//      return FontType.BITMAP;
//    }
//
//    @Override
//    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
//      return Either.left(this::load);
//    }
//
//    private Font load(ResourceManager resourceManager) throws IOException {
//      Identifier identifier = this.file.withPrefixedPath("textures/");
//      try (InputStream inputStream = resourceManager.open(identifier);){
//        NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
//        int i2 = nativeImage.getWidth();
//        int j = nativeImage.getHeight();
//        int k = i2 / this.codepointGrid[0].length;
//        int l = j / this.codepointGrid.length;
//        float f = (float)this.height / (float)l;
//        GlyphContainer<EmoteFontGlyph> glyphContainer = new GlyphContainer<EmoteFontGlyph>(EmoteFontGlyph[]::new, i -> new EmoteFontGlyph[i][]);
//        for (int m = 0; m < this.codepointGrid.length; ++m) {
//          int n = 0;
//          for (int o : this.codepointGrid[m]) {
//            int q;
//            EmoteFontGlyph emoteFontGlyph;
//            int p = n++;
//            if (o == 0 || (emoteFontGlyph = glyphContainer.put(o, new EmoteFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(nativeImage, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
//            LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)identifier);
//          }
//        }
//        EmoteFont bitmapFont = new EmoteFont(nativeImage, glyphContainer);
//        return bitmapFont;
//      }
//    }
//
//    private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
//      int i;
//      for (i = characterWidth - 1; i >= 0; --i) {
//        int j = charPosX * characterWidth + i;
//        for (int k = 0; k < characterHeight; ++k) {
//          int l = charPosY * characterHeight + k;
//          if (image.getOpacity(j, l) == 0) continue;
//          return i + 1;
//        }
//      }
//      return i + 1;
//    }
//  }
}

