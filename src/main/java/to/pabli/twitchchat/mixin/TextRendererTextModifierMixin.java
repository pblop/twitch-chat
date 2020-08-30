package to.pabli.twitchchat.mixin;


import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.render.VertexConsumer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.TwitchChatMod;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.emotes.Emote;
import to.pabli.twitchchat.emotes.EmoteManager;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

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
        String finalText = text;
        // Only swap emote codes and their respective identification characters when writing a Twitch message.
        Map<String, String> targetReplacementMap = EmoteManager.getInstance().getSetClone().stream().filter(emote -> finalText.contains(emote.getCode())).collect(Collectors.toMap(Emote::getCode, Emote::getCharIdentifierAsString));
        text = TwitchChatMod.replaceString(text, targetReplacementMap);

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

//    @Inject(at = @At("HEAD"), method = "drawInternal(Lnet/minecraft/text/StringRenderable;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", cancellable = true)
//    private void drawInternal(StringRenderable text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light, CallbackInfoReturnable<Integer> info) {
//        String finalText = text.getString();
//        Map<String, String> targetReplacementMap = Emote.SET.stream().filter(emote -> finalText.contains(emote.getCode())).collect(Collectors.toMap(Emote::getCode, Emote::getCharIdentifierAsString));
//        text = replaceStringRenderable(text, targetReplacementMap);
//
//        color = tweakTransparency(color);
//        Matrix4f matrix4f = matrix.copy();
//        if (shadow) {
//            this.drawLayer(text, x, y, color, true, matrix, vertexConsumerProvider, seeThrough, backgroundColor, light);
//            matrix4f.addToLastColumn(FORWARD_SHIFT);
//        }
//
//        x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, seeThrough, backgroundColor, light);
//        info.setReturnValue((int)x + (shadow ? 1 : 0));
//    }

    private StringRenderable replaceStringRenderable(StringRenderable renderable, Map<String, String> targetReplacementMap) {
        Set<Map.Entry<String, String>> targetReplacementMapEntrySet = targetReplacementMap.entrySet();

        ArrayList<StringRenderable> stringRenderableList = new ArrayList<>();
        renderable.visit((style, string) -> {
            for (Map.Entry<String, String> entry : targetReplacementMapEntrySet) {
                string = string.replace(entry.getKey(), entry.getValue());
            }
            stringRenderableList.add(StringRenderable.styled(string, style));
            return Optional.empty();
        }, Style.EMPTY);

        return StringRenderable.concat(stringRenderableList);
    }
}
