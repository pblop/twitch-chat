package to.pabli.twitchchat.mixin;


import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextRendererTextModifierMixin {
    @Shadow
    @Final
    private static Vector3f FORWARD_SHIFT;
    @Shadow
    private static int tweakTransparency(int color) { return 0; }
    @Shadow
    public String mirror(String text) { return null; }
    @Shadow
    private float drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) { return 0; }
    @Shadow
    private float drawLayer(StringRenderable text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) { return 0; }

    @Inject(at = @At("HEAD"), method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", cancellable = true)
    private void drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, boolean mirror, CallbackInfoReturnable<Integer> info) {
        text = text.replace("LOL", "wow");

        if (mirror) {
            text = this.mirror(text);
        }

        color = tweakTransparency(color);
        Matrix4f matrix4f = matrix.copy();
        if (shadow) {
            this.drawLayer(text, x, y, color, true, matrix, vertexConsumers, seeThrough, backgroundColor, light);
            matrix4f.addToLastColumn(FORWARD_SHIFT);
        }

        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumers, seeThrough, backgroundColor, light);

        info.setReturnValue((int)x + (shadow ? 1 : 0));
    }

    @Inject(at = @At("HEAD"), method = "drawInternal(Lnet/minecraft/text/StringRenderable;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", cancellable = true)
    private void drawInternal(StringRenderable text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light, CallbackInfoReturnable<Integer> info) {
        if (text.getString().contains("LOL")) {
            text = StringRenderable.plain("WOW");
        }
        color = tweakTransparency(color);
        Matrix4f matrix4f = matrix.copy();
        if (shadow) {
            this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
            matrix4f.addToLastColumn(FORWARD_SHIFT);
        }

        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, seeThrough, backgroundColor, light);
        info.setReturnValue((int)x + (shadow ? 1 : 0));
    }
}
