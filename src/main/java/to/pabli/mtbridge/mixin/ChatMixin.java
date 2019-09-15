package to.pabli.mtbridge.mixin;

import java.util.Date;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to.pabli.mtbridge.MTBridge;
import to.pabli.mtbridge.config.ModConfig;
import to.pabli.mtbridge.twitch_integration.CalculateMinecraftColor;

@Mixin(Screen.class)
public class ChatMixin {
	@Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
	private void sendMessage(String text, boolean showInHistory, CallbackInfo info) {
    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    // If the message is a twitch message
    if (text.startsWith(config.prefix)) {
      if (MTBridge.bot != null && MTBridge.bot.isConnected()) {
        String textWithoutPrefix = text.replaceFirst(config.prefix, "");
        MTBridge.bot.sendMessage(textWithoutPrefix); // Send the message to the Twitch IRC Chat

        Date currentTime = new Date();
        String formattedTime = MTBridge.formatDateTwitch(currentTime);

        String username = MTBridge.bot.getUsername();
        Formatting userColor = CalculateMinecraftColor.getDefaultUserColor(username);

        // Add the message to the Minecraft Chat
        MTBridge.addTwitchMessage(formattedTime, username, textWithoutPrefix, userColor);
        MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(text);
        info.cancel();
      } else {
        MTBridge.addNotification("Twitch integration is not enabled, to enable it do /twitch enable.");
      }
    }
	}
}
