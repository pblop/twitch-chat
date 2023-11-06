package eu.pabl.twitchchat.mixin;

import eu.pabl.twitchchat.emotes.EmoteManager;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.class)
public abstract class AddEmoteFontMixin implements ResourceReloader, AutoCloseable {
  @Shadow @Final private List<Font> fonts;

  @Shadow @Final private Map<Identifier, FontStorage> fontStorages;

  @Inject(
    method = "reload(Lnet/minecraft/client/font/FontManager$ProviderIndex;Lnet/minecraft/util/profiler/Profiler;)V",
    at=@At("TAIL")
  )
  private void reload(FontManager.ProviderIndex index, Profiler profiler, CallbackInfo ci) {
    System.out.println("Added font");
    this.fonts.add(EmoteManager.getInstance().getEmoteFont());
    this.fontStorages.put(EmoteManager.EMOTE_FONT_IDENTIFIER, EmoteManager.getInstance().emoteFontStorage);
  }
}
