package to.pabli.twitchchat.mixin;

import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.emotes.Emote;
import to.pabli.twitchchat.emotes.EmoteRenderableGlyph;

import java.io.IOException;
import java.util.Optional;


@Mixin(FontStorage.class)
public abstract class CustomGlyphFontStorage {
    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Inject(at = @At("HEAD"), method = "getGlyph", cancellable = true)
    public void getGlyph(int charCode, CallbackInfoReturnable<Glyph> info) {
        Optional<Emote> glyphEmote = Emote.SET.stream().filter(emote -> {
            int emoteCharIdentifierCharCode = emote.getCharIdentifier();
            return emoteCharIdentifierCharCode == charCode;
        }).findFirst();

        if (glyphEmote.isPresent()) {
            try {
                info.setReturnValue(new EmoteRenderableGlyph(glyphEmote.get()).getGlyph());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getGlyphRenderer(I)Lnet/minecraft/client/font/GlyphRenderer;", cancellable = true)
    public void getGlyphRenderer(int charCode, CallbackInfoReturnable<GlyphRenderer> info) {
        Optional<Emote> glyphEmote = Emote.SET.stream().filter(emote -> {
            int emoteCharIdentifierCharCode = emote.getCharIdentifier();
            return emoteCharIdentifierCharCode == charCode;
        }).findFirst();

        if (glyphEmote.isPresent()) {
            try {
                info.setReturnValue(getGlyphRenderer(new EmoteRenderableGlyph(glyphEmote.get())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}