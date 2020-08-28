package to.pabli.twitchchat.mixin;

import net.minecraft.client.font.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.emotes.EmoteGlyph;


@Mixin(FontStorage.class)
public abstract class CustomGlyphFontStorage {
    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    private static final Glyph SPACE = () -> {
        return 4.0F;
    };

    @Inject(at = @At("HEAD"), method = "getGlyph", cancellable = true)
    public void getGlyph(int charCode, CallbackInfoReturnable<Glyph> info) {
        if (charCode == 57344) {
            info.setReturnValue(SPACE);
        }
    }

    @Inject(at = @At("HEAD"), method = "getGlyphRenderer(I)Lnet/minecraft/client/font/GlyphRenderer;", cancellable = true)
    public void getGlyphRenderer(int charCode, CallbackInfoReturnable<GlyphRenderer> info) {
        if (charCode == 57344) {
            info.setReturnValue(this.getGlyphRenderer(EmoteGlyph.INSTANCE));
        }
    }
}