package to.pabli.twitchchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.emotes.EmoteDownloader;
import to.pabli.twitchchat.twitch_integration.Bot;

public class TwitchChatMod implements ModInitializer {
  public static Bot bot;

  @Override
  public void onInitialize() {
    EmoteDownloader.getConfig().downloadBadges("global");
    ModConfig.getConfig().load();
  }

  public static void addTwitchMessage(String time, String username, String message, Formatting textColor) {
    MutableText timestampText = new LiteralText(time);
    MutableText usernameText = new LiteralText(username).formatted(textColor);
    MutableText messageBodyText = new LiteralText(": " + message);

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
}
