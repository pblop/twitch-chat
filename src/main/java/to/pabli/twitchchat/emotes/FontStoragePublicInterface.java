package to.pabli.twitchchat.emotes;

import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

public interface FontStoragePublicInterface {
  public GlyphRenderer getGlyphRendererPublic(RenderableGlyph c);
  public RenderableGlyph getRenderableGlyphPublic(char c);
}
