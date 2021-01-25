package to.pabli.twitchchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
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
	  addTwitchMessage(time, username, message, textColor, false);
  }
  public static void addTwitchMessage(String time, String username, String message, Formatting textColor, boolean isMeMessage) {
    MutableText timestampText = new LiteralText(time);
    MutableText usernameText = new LiteralText(username).formatted(textColor);
    MutableText messageBodyText;
    String plainTextMessage = "[twitch] " + username + ": " + message;

    if (!isMeMessage) {
      messageBodyText = new LiteralText(": " + message);
    } else {
      // '/me' messages have the same color as the username in the Twitch website.
      // And thus I set the color of the message to be the same as the username.
      // They also don't have a colon after the username.
      messageBodyText = new LiteralText(" " + message).formatted(textColor);

      // In Minecraft, a '/me' message is marked with a star before the name, like so:
      //
      // <Player> This is a normal message
      // * Player this is a '/me' message
      //
      // The star is always white (that's why I don't format it).
      usernameText = new LiteralText("* ").append(usernameText);
    }

    if (ModConfig.getConfig().isBroadcastEnabled()) {
      try {
        if (MinecraftClient.getInstance().player != null) {
          MinecraftClient.getInstance().player.sendChatMessage(plainTextMessage);
        }
      } catch (NullPointerException e) {
        System.err.println("TWITCH BOT FAILED TO BROADCAST MESSAGE: " + e.getMessage());
      }
    } else {
      MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT,
          timestampText
          .append(usernameText)
          .append(messageBodyText), UUID.randomUUID());
    }
  }
  public static void addNotification(MutableText message) {
    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message.formatted(Formatting.DARK_GRAY), UUID.randomUUID());
  }

  public static String formatTMISentTimestamp(String tmiSentTS) {
    return formatTMISentTimestamp(Long.parseLong(tmiSentTS));
  }
  public static String formatTMISentTimestamp(long tmiSentTS) {
    Date date = new Date(tmiSentTS);
    return formatDateTwitch(date);
  }
  public static String formatDateTwitch(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat(ModConfig.getConfig().getDateFormat());
    return sf.format(date);
  }
}
