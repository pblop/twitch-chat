package eu.pabl.twitchchat;

import eu.pabl.twitchchat.commands.TwitchBaseCommand;
import eu.pabl.twitchchat.config.ModConfig;
import eu.pabl.twitchchat.emotes.CustomImageManager;
import eu.pabl.twitchchat.emotes.DownloadableImage;
import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIEmoteTagElement;
import eu.pabl.twitchchat.twitch_integration.Bot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    if (ModConfig.getConfig().isEmotesEnabled()) {
      CustomImageManager.getInstance().downloadImagePack("https://api.twitch.tv/helix/chat/emotes/global", DownloadableImage.ImageTypes.EMOTE);
      CustomImageManager.getInstance().downloadImagePack("https://api.twitch.tv/helix/chat/badges/global", DownloadableImage.ImageTypes.BADGE);
    }
  }

  public static MutableText getBadgedUsername(String username, TextColor usernameColour, String[] userBadges) {
    MutableText badgedUsername = Text.empty();
    if (userBadges != null) {
      for (String badge : userBadges) {
        Integer codepoint = CustomImageManager.getInstance().getBadgeCodepointFromId(badge);
        badgedUsername.append(Text
          .literal(Character.toString(codepoint))
          .styled(style -> style.withFont(CustomImageManager.CUSTOM_IMAGE_FONT_IDENTIFIER))
        );
      }
    }
    badgedUsername.append(Text.literal(username).styled(style -> style.withColor(usernameColour)));
    return badgedUsername;
  }
  public static MutableText getEmotedMessage(String plainMessage, List<TwitchAPIEmoteTagElement> emotes) {
    MutableText emotedMessage = MutableText.of(TextContent.EMPTY);
    int currentPos = 0;

    // The emotes count in offsets by codepoints. So... we're doing the same.
    // (thus substringCodepoints, which does the same as substring, but treating codepoint indexes instead of
    // character indexes).
    if (emotes != null) {
      for (var emote : emotes) {
        if (currentPos != emote.startPosition()) {
          emotedMessage.append(Text.of(substringCodepoints(plainMessage, currentPos, emote.startPosition())));
        }
        Integer codepoint = CustomImageManager.getInstance().getEmoteCodepointFromId(emote.emoteID());

        emotedMessage.append(
          Text.literal(Character.toString(codepoint))
            .styled(style -> style.withFont(CustomImageManager.CUSTOM_IMAGE_FONT_IDENTIFIER))
        );

        // The end position is the exact end position of the emote, so we add one.
        currentPos = emote.endPosition() + 1;
      }
    }

    if (currentPos != plainMessage.length()) {
      emotedMessage.append(Text.of(substringCodepoints(plainMessage, currentPos)));
    }
    return emotedMessage;
  }

  // The difference between the method on top of this one and this one is the method on top uses the emote tags in the
  // IRC message (basically the emotes parsed by Twitch), while this one checks for the keywords on its own. I do not
  // know which is better or faster. I could probably remove the one on top and use this for everything.
  public static MutableText getEmotedMessageLocal(String plainMessage) {
    MutableText emotedMessage = MutableText.of(TextContent.EMPTY);

    // Split words and check if any of those is an emote.
    String[] wordArray = plainMessage.split(" ");
    for (int i = 0; i < wordArray.length; i++) {
      String word = wordArray[i];
      // TODO: Maybe only let user use emotes that they have in their emote sets. This could be achieved by only
      //       having the CustomImageManager#emoteNameToIdHashMap be playerEmoteNameToIdHashMap, and only add emotes
      //       if the player has them in their emote list.
      //       Or maybe we could have a list of user emotes somewhere that we check against first, and if the message is
      //       sent by the user and the user doesn't have the emote, we don't enter in the if below. (This would allow
      //       us to maybe use this method for getting emoted messages for other users, which, as outlined in the comment
      //       above, could be faster, maybe).
      String emoteId = CustomImageManager.getInstance().getEmoteIdFromName(word);
      if (emoteId != null) {
        emotedMessage.append(Text.literal(
          Character.toString(CustomImageManager.getInstance().getEmoteCodepointFromId(emoteId)))
          .setStyle(Style.EMPTY.withFont(CustomImageManager.CUSTOM_IMAGE_FONT_IDENTIFIER)
          ));
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

  private static String substringCodepoints(String str, int idx, int len) {
    int start = str.offsetByCodePoints(0, idx);
    int end = str.offsetByCodePoints(start, len-idx);
    return str.substring(start, end);
  }
  private static String substringCodepoints(String str, int idx) {
    int start = str.offsetByCodePoints(0, idx);
    return str.substring(start);
  }

  public static void addTwitchMessage(String time, String username, String message, List<TwitchAPIEmoteTagElement> emotes, TextColor usernameColour, String[] userBadges, boolean isMeMessage) {
    MutableText timestampText = Text.literal(time);
    MutableText usernameText = ModConfig.getConfig().isEmotesEnabled()
      ? getBadgedUsername(username, usernameColour, userBadges)
      : Text.literal(username).styled(style -> style.withColor(usernameColour));
    MutableText emotedMessage = ModConfig.getConfig().isEmotesEnabled()
      ? emotes == null ? getEmotedMessageLocal(message) : getEmotedMessage(message, emotes)
      : Text.literal(message);
    MutableText messageBodyText;

    if (!isMeMessage) {
      messageBodyText = Text.literal(": ").append(emotedMessage);
    } else {
      // '/me' messages have the same color as the username in the Twitch website.
      // And thus I set the color of the message to be the same as the username.
      // They also don't have a colon after the username.
      messageBodyText = Text.literal(" ").append(emotedMessage).styled(style -> style.withColor(usernameColour));

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
