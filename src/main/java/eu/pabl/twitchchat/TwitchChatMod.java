package eu.pabl.twitchchat;

import eu.pabl.twitchchat.commands.TwitchBaseCommand;
import eu.pabl.twitchchat.config.ModConfig;
import eu.pabl.twitchchat.emotes.CustomImageManager;
import eu.pabl.twitchchat.twitch_integration.Bot;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchChatMod implements ModInitializer {
  public static final String MOD_ID = "twitchchat";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static Bot bot;

  @Override
  public void onInitialize() {
    ModConfig.getConfig().load();

    // Register commands
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
        new TwitchBaseCommand().registerCommands(dispatcher));

//    EmoteManager.getInstance().downloadEmote();
    CustomImageManager.getInstance().downloadEmotePack("https://api.twitch.tv/helix/chat/emotes/global");
//    MinecraftClient.getInstance().textRenderer
  }

  public static MutableText getEmotedMessage(String plainMessage) {
    MutableText emotedMessage = MutableText.of(TextContent.EMPTY);

    // Split words and check if any of those is an emote.
    String[] wordArray = plainMessage.split(" ");
    for (int i = 0; i < wordArray.length; i++) {
      String word = wordArray[i];
      Integer codepoint = CustomImageManager.getInstance().getEmoteCodepoint(word);
      if (codepoint != null) {
        emotedMessage.append(
          // Add the space character after this emote
          Text.literal(Character.toString(codepoint))
            .setStyle(Style.EMPTY.withFont(CustomImageManager.CUSTOM_IMAGE_FONT_IDENTIFIER)));
      } else {
        emotedMessage.append(Text.of(word));
      }

      // Add a space after words because split removes it.
      if (i < (wordArray.length - 1)) {
        emotedMessage.append(Text.of(" "));
      }
    }
    return emotedMessage;
  }

  public static void addTwitchMessage(String time, String username, String message, TextColor textColor, boolean isMeMessage) {
    MutableText timestampText = Text.literal(time);
    MutableText usernameText = Text.literal(username).styled(style -> style.withColor(textColor));
    MutableText emotedMessage = getEmotedMessage(message);
    MutableText messageBodyText;

    if (!isMeMessage) {
      messageBodyText = Text.literal(": ").append(emotedMessage);
    } else {
      // '/me' messages have the same color as the username in the Twitch website.
      // And thus I set the color of the message to be the same as the username.
      // They also don't have a colon after the username.
      messageBodyText = Text.literal(" ").append(emotedMessage).styled(style -> style.withColor(textColor));

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
          MinecraftClient.getInstance().player.sendMessage(Text.literal(plainTextMessage));
        }
      } catch (NullPointerException e) {
        System.err.println("TWITCH BOT FAILED TO BROADCAST MESSAGE: " + e.getMessage());
      }
    } else {
//      MinecraftClient.getInstance().getF
//      Text.of("").getWithStyle(Style.EMPTY.withFont(Font))
      MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
          timestampText
          .append(usernameText)
          .append(messageBodyText));
      MinecraftClient.getInstance().getNarratorManager()
              .narrateChatMessage(Text.of(usernameText + "" + messageBodyText));
    }
  }

  private static String sanitiseMessage(String message) {
    return message.replaceAll("§", "");
  }

  public static void addNotification(MutableText message) {
    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message.formatted(Formatting.DARK_GRAY));
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
