package eu.pabl.twitchchat.emotes.minecraft;

import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.emotes.CustomImageManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class CustomImageFontStorage extends FontStorage implements AutoCloseable {
  private static final Random RANDOM = Random.create();
  private static final float MAX_ADVANCE = 32.0f;
  private final Identifier id;
  private final Font font;
  private final GlyphContainer<GlyphRenderer> glyphRendererCache = new GlyphContainer(GlyphRenderer[]::new, rowCount -> new GlyphRenderer[rowCount][]);
  private final GlyphContainer<GlyphPair> glyphCache = new GlyphContainer(GlyphPair[]::new, rowCount -> new GlyphPair[rowCount][]);
  private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<IntList>();
  private final List<GlyphAtlasTexture> glyphAtlases = new ArrayList<>();

  public CustomImageFontStorage(CustomImageFont customImageFontInstance) {
    super(null, null);
    this.id = CustomImageManager.CUSTOM_IMAGE_FONT_IDENTIFIER;
    this.font = customImageFontInstance;
  }

  @Override
  public void setFonts(List<Font> fonts) { }

  @Override
  public void close() {
    // this.closeFont();
    // this.closeGlyphAtlases();
  }

  private void closeFont() {
    font.close();
//    this.fonts.clear();
  }

  private void closeGlyphAtlases() {
    for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
      glyphAtlasTexture.close();
    }
    this.glyphAtlases.clear();
  }

  private static boolean isAdvanceInvalid(Glyph glyph) {
    float f = glyph.getAdvance(false);
    if (f < 0.0f || f > MAX_ADVANCE) {
      return true;
    }
    float g = glyph.getAdvance(true);
    return g < 0.0f || g > MAX_ADVANCE;
  }

  /**
   * {@return the glyph of {@code codePoint}}
   *
   * @apiNote Call {@link #getGlyph} instead, as that method provides caching.
   */
  private GlyphPair findGlyph(int codePoint) {
    Glyph glyph = font.getGlyph(codePoint);
    if (glyph == null)
      return GlyphPair.MISSING;
    if (CustomImageFontStorage.isAdvanceInvalid(glyph))
      return new GlyphPair(glyph, BuiltinEmptyGlyph.MISSING);

    return new GlyphPair(glyph, glyph);
  }

  /**
   * {@return the glyph of {@code codePoint}}
   *
   * @implNote {@link BuiltinEmptyGlyph#MISSING} is returned for missing code points.
   */
  @Override
  public Glyph getGlyph(int codePoint, boolean validateAdvance) {
    return this.glyphCache.computeIfAbsent(codePoint, this::findGlyph).getGlyph(validateAdvance);
  }

  private GlyphRenderer findGlyphRenderer(int codePoint) {
    Glyph glyph = font.getGlyph(codePoint);
    if (glyph != null)
      return glyph.bake(this::getGlyphRenderer);
    return super.blankGlyphRenderer;
  }

  @Override
  public GlyphRenderer getGlyphRenderer(int codePoint) {
    return this.glyphRendererCache.computeIfAbsent(codePoint, this::findGlyphRenderer);
  }

  private GlyphRenderer getGlyphRenderer(RenderableGlyph _c) {
    if (!(_c instanceof CustomImageRenderableGlyph)) {
      // Our font only returns CustomImageRenderableGlyphs, so this should never trigger. If it does, there's a big problem.
      TwitchChatMod.LOGGER.error("RenderableGlyph for emotes is not an CustomImageRenderableGlyph.");
      return super.blankGlyphRenderer;
    }
    CustomImageRenderableGlyph c = (CustomImageRenderableGlyph) _c;

    for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
      GlyphRenderer glyphRenderer = glyphAtlasTexture.getGlyphRenderer(c);
      if (glyphRenderer == null) continue;
      return glyphRenderer;
    }
    Identifier identifier = this.id.withSuffixedPath("/" + c.getId());
    boolean bl = c.hasColor();
    TextRenderLayerSet textRenderLayerSet = bl ? TextRenderLayerSet.of(identifier) : TextRenderLayerSet.ofIntensity(identifier);
    GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(textRenderLayerSet, bl);
    this.glyphAtlases.add(glyphAtlasTexture2);
    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, glyphAtlasTexture2);
    GlyphRenderer glyphRenderer2 = glyphAtlasTexture2.getGlyphRenderer(c);
    return glyphRenderer2 == null ? super.blankGlyphRenderer : glyphRenderer2;
  }

  @Override
  public GlyphRenderer getObfuscatedGlyphRenderer(Glyph glyph) {
    IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
    if (intList != null && !intList.isEmpty()) {
      return this.getGlyphRenderer(intList.getInt(RANDOM.nextInt(intList.size())));
    }
    return super.blankGlyphRenderer;
  }

  @Override
  public GlyphRenderer getRectangleRenderer() {
    return super.whiteRectangleGlyphRenderer;
  }

  record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
    static final GlyphPair MISSING = new GlyphPair(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);

    Glyph getGlyph(boolean validateAdvance) {
      return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
    }
  }
}
