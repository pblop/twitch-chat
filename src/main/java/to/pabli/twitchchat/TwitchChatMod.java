package to.pabli.twitchchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.twitch_integration.Bot;

public class TwitchChatMod implements ModInitializer {
  public static Bot bot;

	@Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    // InGameHud.addChatMessage
    AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
  }

  public static void addTwitchMessage(String time, String username, String message, Formatting textColor) {
    Text timestampText = new LiteralText("[" + time + "] ");
    Text usernameText = new LiteralText(username).formatted(textColor);
    Text messageBodyText = new LiteralText(": " + message);

    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT,
        timestampText
        .append(usernameText)
        .append(messageBodyText));
  }
  public static void addNotification(String message) {
    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, new LiteralText(message).formatted(Formatting.DARK_GRAY));
  }
//  public static void addMessage(Text text) {
//    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, text);
//  }

  public static String formatTMISentTimestamp(String tmiSentTS) {
    Date date = new Date(Long.parseLong(tmiSentTS));
    return formatDateTwitch(date);
  }
  public static String formatDateTwitch(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat("H:mm");
    return sf.format(date);
  }
}
