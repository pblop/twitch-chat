package to.pabli.twitchchat.mixin;

import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.emotes.Emote;
import to.pabli.twitchchat.emotes.EmoteManager;
import to.pabli.twitchchat.emotes.EmoteRenderableGlyph;

import java.util.Optional;


@Mixin(FontStorage.class)
public abstract class CustomGlyphFontStorage {
    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Inject(at = @At("HEAD"), method = "getGlyph", cancellable = true)
    public void getGlyph(int charCode, CallbackInfoReturnable<Glyph> info) {
        Emote emote = EmoteManager.getInstance().getHashMap().get(charCode);

        if (emote != null) {
            EmoteRenderableGlyph renderableGlyph = emote.getRenderableGlyph();
            if (renderableGlyph != null) {
                // != null means the renderableGlyph exists (has been downloaded)
                info.setReturnValue(renderableGlyph.getGlyph());
            } else {
                // TODO: Ask to download our emote
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getGlyphRenderer(I)Lnet/minecraft/client/font/GlyphRenderer;", cancellable = true)
    public void getGlyphRenderer(int charCode, CallbackInfoReturnable<GlyphRenderer> info) {
        Emote emote = EmoteManager.getInstance().getHashMap().get(charCode);

        if (emote != null) {
            EmoteRenderableGlyph renderableGlyph = emote.getRenderableGlyph();
            if (renderableGlyph != null) {
                // != null means the renderableGlyph exists (has been downloaded)
                info.setReturnValue(getGlyphRenderer(renderableGlyph));
            } else {
                // TODO: Ask to download our emote
            }
        }
    }
}