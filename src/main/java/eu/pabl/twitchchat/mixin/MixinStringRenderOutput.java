package eu.pabl.twitchchat.mixin;

import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.channelFont.ChannelFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Mixin(FontManager.class)
public class MixinStringRenderOutput {

    @Shadow
    private Map<Identifier, FontStorage> fontStorages;
    @Shadow
    private TextureManager textureManager;

    @Unique
    private static final Identifier channelIconFontID = Identifier.of("twitchchat", "channel_icon2");
    @Unique
    private static final List<Font.FontFilterPair> channelIconFontFilter = List.of(new Font.FontFilterPair(new ChannelFont(), FontFilterType.FilterMap.NO_FILTER));

    @Inject(method="reload", at=@At("RETURN"))
    public CompletableFuture<Void> afterReload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> ci) {
        return ci.getReturnValue().thenRun(() -> {
            FontStorage channelIconFontStorage = new FontStorage(this.textureManager, channelIconFontID);
            channelIconFontStorage.setFonts(channelIconFontFilter, null);
            fontStorages.put(channelIconFontID, channelIconFontStorage);
            TwitchChatMod.LOGGER.info("Added custom channel icon font: " + channelIconFontID);
        });
    }
}
