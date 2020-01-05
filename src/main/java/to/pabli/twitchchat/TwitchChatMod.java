package to.pabli.twitchchat;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    ModConfig.getConfig().load();
  }

  public static void addTwitchMessage(String time, String username, String message, Formatting textColor) {
    Text timestampText = new LiteralText(time);
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

  public static String formatTMISentTimestamp(String tmiSentTS) {
    Date date = new Date(Long.parseLong(tmiSentTS));
    return formatDateTwitch(date);
  }
  public static String formatDateTwitch(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat(ModConfig.getConfig().getDateFormat());
    return sf.format(date);
  }
}
