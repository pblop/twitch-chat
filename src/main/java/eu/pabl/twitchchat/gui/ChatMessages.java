package eu.pabl.twitchchat.gui;

import eu.pabl.twitchchat.TwitchChatMod;
import eu.pabl.twitchchat.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ChatMessages {
  public static void addTwitchMessage(String time, String username, String message, TextColor textColor, boolean isMeMessage) {
    MutableComponent timestampText = Component.literal(time);
    MutableComponent usernameText = Component.literal(username).withStyle(style -> style.withColor(textColor));
    MutableComponent messageBodyText;

    if (!isMeMessage) {
      messageBodyText = Component.literal(": " + message);
    } else {
      // '/me' messages have the same color as the username in the Twitch website.
      // And thus I set the color of the message to be the same as the username.
      // They also don't have a colon after the username.
      messageBodyText = Component.literal(" " + message).withStyle(style -> style.withColor(textColor));

      // In Minecraft, a '/me' message is marked with a star before the name, like so:
      //
      // <Player> This is a normal message
      // * Player this is a '/me' message
      //
      // The star is always white (that's why I don't format it).
      usernameText = Component.literal("* ").append(usernameText);
    }

    if (ModConfig.getConfig().isBroadcastEnabled()) {
      try {
        String plainTextMessage = ModConfig.getConfig().getBroadcastPrefix() + username + ": " + message;
        plainTextMessage = sanitiseMessage(plainTextMessage);
        if (Minecraft.getInstance().player != null) {
          minecraftSendChatMessage(Component.literal(plainTextMessage), false);
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
  private static void minecraftChatAddMessage(MutableComponent message, GuiMessageTag indicator) {
    Minecraft instance = Minecraft.getInstance();
    instance.execute(
        () -> instance.gui.getChat().addMessage(message, null, indicator)
    );
  }

  private static void minecraftSendChatMessage(MutableComponent message, boolean overlay) {
    Minecraft instance = Minecraft.getInstance();
    instance.execute(
      () -> instance.player.displayClientMessage(message, overlay)
    );
  }

  private static String sanitiseMessage(String message) {
    return message.replaceAll("§", "");
  }

  public static void addNotification(MutableComponent message) {
    minecraftChatAddMessage(message.withStyle(ChatFormatting.DARK_AQUA), MessageIndicators.TWITCH_SYSTEM);
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
