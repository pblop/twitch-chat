package to.pabli.twitchchat.mixin;

import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.emotes.EmoteRenderableGlyph;


@Mixin(FontStorage.class)
public abstract class CustomGlyphFontStorage {
    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Inject(at = @At("HEAD"), method = "getGlyph", cancellable = true)
    public void getGlyph(int charCode, CallbackInfoReturnable<Glyph> info) {
        if (charCode == 57344) {
            info.setReturnValue(EmoteRenderableGlyph.INSTANCE.getGlyph());
        }
    }

    @Inject(at = @At("HEAD"), method = "getGlyphRenderer(I)Lnet/minecraft/client/font/GlyphRenderer;", cancellable = true)
    public void getGlyphRenderer(int charCode, CallbackInfoReturnable<GlyphRenderer> info) {
        if (charCode == 57344) {
            info.setReturnValue(this.getGlyphRenderer(EmoteRenderableGlyph.INSTANCE));
        }
    }
}