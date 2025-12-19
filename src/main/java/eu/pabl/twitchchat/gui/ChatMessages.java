package eu.pabl.twitchchat.gui;

import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ChatMessages {
  public static void addTwitchMessage(String time, String username, String message, TextColor textColor, boolean isMeMessage) {
    MutableText timestampText = Text.literal(time);
    MutableText usernameText = Text.literal(username).styled(style -> style.withColor(textColor));
    MutableText messageBodyText;

    if (!isMeMessage) {
      messageBodyText = Text.literal(": " + message);
    } else {
      // '/me' messages have the same color as the username in the Twitch website.
      // And thus I set the color of the message to be the same as the username.
      // They also don't have a colon after the username.
      messageBodyText = Text.literal(" " + message).styled(style -> style.withColor(textColor));

      // In Minecraft, a '/me' message is marked with a star before the name, like so:
      //
      // <Player> This is a normal message
      // * Player this is a '/me' message
      //
      // The star is always white (that's why I don't format it).
      usernameText = Text.literal("* ").append(usernameText);
    }

    if (ModConfig.getConfig().isBroadcastEnabled()) {
      try {
        String plainTextMessage = ModConfig.getConfig().getBroadcastPrefix() + username + ": " + message;
        plainTextMessage = sanitiseMessage(plainTextMessage);
        if (MinecraftClient.getInstance().player != null) {
          minecraftSendChatMessage(Text.literal(plainTextMessage), false);
        }
      } catch (NullPointerException e) {
        TwitchChatMod.LOGGER.error("Failed to broadcast Twitch message to Minecraft chat", e);
      }
    } else {
      minecraftChatAddMessage(
          timestampText
          .append(usernameText)
          .append(messageBodyText),
          MessageIndicators.TWITCH_CHAT
      );
    }
  }

  // Wrapper methods to ensure sending/rendering chat messages happens on the main Minecraft thread.
  private static void minecraftChatAddMessage(MutableText message, MessageIndicator indicator) {
    MinecraftClient instance = MinecraftClient.getInstance();
    instance.execute(
        () -> instance.inGameHud.getChatHud().addMessage(message, null, indicator)
    );
  }

  private static void minecraftSendChatMessage(MutableText message, boolean overlay) {
    MinecraftClient instance = MinecraftClient.getInstance();
    instance.execute(
      () -> instance.player.sendMessage(message, overlay)
    );
  }

  private static String sanitiseMessage(String message) {
    return message.replaceAll("§", "");
  }

  public static void addNotification(MutableText message) {
    minecraftChatAddMessage(message.formatted(Formatting.DARK_AQUA), MessageIndicators.TWITCH_SYSTEM);
  }

  public static String formatTMISentTimestamp(Instant tmiSentTS) {
    Date date = Date.from(tmiSentTS);
    return formatDateTwitch(date);
  }

  public static String formatDateTwitch(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat(ModConfig.getConfig().getDateFormat());
    return sf.format(date);
  }
}
