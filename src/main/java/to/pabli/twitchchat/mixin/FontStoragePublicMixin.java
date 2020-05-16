package to.pabli.twitchchat.mixin;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import to.pabli.twitchchat.emotes.FontStoragePublicInterface;

@Mixin(FontStorage.class)
public abstract class FontStoragePublicMixin implements AutoCloseable, FontStoragePublicInterface {
  @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

  @Shadow protected abstract RenderableGlyph getRenderableGlyph(char character);

  @Override
  public GlyphRenderer getGlyphRendererPublic(RenderableGlyph c) {
    return getGlyphRenderer(c);
  }

  @Override
  public RenderableGlyph getRenderableGlyphPublic(char c) {
    return getRenderableGlyph(c);
  }
}
