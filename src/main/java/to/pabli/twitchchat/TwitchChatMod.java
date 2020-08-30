package to.pabli.twitchchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.emotes.Emote;
import to.pabli.twitchchat.emotes.EmoteManager;
import to.pabli.twitchchat.twitch_integration.Bot;

public class TwitchChatMod implements ModInitializer {
  public static Bot bot;
  public static final String CLIENT_ID = "9tvrwrfx1397vabxtz0sfoz2iu1k3a";

  @Override
  public void onInitialize() {
    EmoteManager.getInstance().downloadBadges("global");
    ModConfig.getConfig().load();
  }

  public static void addTwitchMessage(String time, String username, String message, Formatting textColor) {
    MutableText timestampText = new LiteralText(time);
    MutableText usernameText = new LiteralText(username).formatted(textColor);
    Map<String, String> targetReplacementMap = EmoteManager.getInstance().getSetClone().stream().filter(emote -> message.contains(emote.getCode())).collect(Collectors.toMap(Emote::getCode, Emote::getCharIdentifierAsString));
    MutableText messageBodyText = new LiteralText(": " + TwitchChatMod.replaceString(message, targetReplacementMap));

    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT,
        timestampText
        .append(usernameText)
        .append(messageBodyText), UUID.randomUUID());
  }
  public static void addNotification(MutableText message) {
    MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message.formatted(Formatting.DARK_GRAY), UUID.randomUUID());
  }

  public static String formatTMISentTimestamp(String tmiSentTS) {
    Date date = new Date(Long.parseLong(tmiSentTS));
    return formatDateTwitch(date);
  }
  public static String formatDateTwitch(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat(ModConfig.getConfig().getDateFormat());
    return sf.format(date);
  }
  public static String replaceString(String string, Map<String, String> targetReplacementMap) {
    for (Map.Entry<String, String> entry : targetReplacementMap.entrySet()) {
      string = string.replace(entry.getKey(), entry.getValue());
    }
    return string;
  }
}
