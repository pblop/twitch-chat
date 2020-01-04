package to.pabli.twitchchat.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public class ChatMixin {
//	@Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
//	private void sendMessage(String text, boolean showInHistory, CallbackInfo info) {
//    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
//
//    // If the message is a twitch message
//    if (text.startsWith(config.prefix)) {
//      if (TwitchChatMod.bot != null && TwitchChatMod.bot.isConnected()) {
//        String textWithoutPrefix = text.replaceFirst(config.prefix, "");
//        TwitchChatMod.bot.sendMessage(textWithoutPrefix); // Send the message to the Twitch IRC Chat
//
//        Date currentTime = new Date();
//        String formattedTime = TwitchChatMod.formatDateTwitch(currentTime);
//
//        String username = TwitchChatMod.bot.getUsername();
//        Formatting userColor = CalculateMinecraftColor.getDefaultUserColor(username);
//
//        // Add the message to the Minecraft Chat
//        TwitchChatMod.addTwitchMessage(formattedTime, username, textWithoutPrefix, userColor);
//        MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(text);
//        info.cancel();
//      } else {
//        TwitchChatMod.addNotification("Twitch integration is not enabled, to enable it do /twitch enable.");
//      }
//    }
//	}
}
