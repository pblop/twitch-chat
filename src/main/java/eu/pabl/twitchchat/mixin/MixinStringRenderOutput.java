package eu.pabl.twitchchat.mixin;

import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.channelFont.Badge;
import eu.pabl.twitchchat.channelFont.ChannelFont;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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


    @Inject(method="reload", at=@At("RETURN"))
    public CompletableFuture<Void> afterReload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> ci) {
        return ci.getReturnValue().thenRun(() -> {
            Badge.loadBadges();
            FontStorage channelIconFontStorage = new FontStorage(this.textureManager, ChannelFont.CHANNEL_ICON_FONT_STORAGE);
            channelIconFontStorage.setFonts(ChannelFont.CHANNEL_ICON_FONT_FILTER, null);
            fontStorages.put(ChannelFont.CHANNEL_ICON_FONT_STORAGE, channelIconFontStorage);
            TwitchChatMod.LOGGER.info("Added custom channel icon font: " + ChannelFont.CHANNEL_ICON_FONT_STORAGE);
        });
    }
}
