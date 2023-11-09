package eu.pabl.twitchchat.twitch_integration;

import com.google.common.collect.ImmutableMap;
import eu.pabl.twitchchat.config.ModConfig;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.net.ssl.SSLSocketFactory;

import eu.pabl.twitchchat.emotes.twitch_api.TwitchAPIEmoteTagElement;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import eu.pabl.twitchchat.TwitchChatMod;

public class Bot extends ListenerAdapter {
  private final PircBotX ircBot;
  private final String username;
  private String channel;
  private ExecutorService myExecutor;
  private HashMap<String, TextColor> formattingColorCache; // Map of usernames to colors to keep consistency with usernames and colors
  private String[] userBadges;

  public Bot(String username, String oauthKey, String channel) {
    this.channel = channel.toLowerCase();
    this.username = username.toLowerCase();
    this.formattingColorCache = new HashMap<>();
    this.userBadges = new String[0];

    Configuration.Builder builder = new Configuration.Builder()
        .setAutoNickChange(false) //Twitch doesn't support multiple users
        .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
        .setEncoding(StandardCharsets.UTF_8) // Use UTF-8 on Windows.
        .setCapEnabled(true)
        .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://dev.twitch.tv/docs/irc/guide/#twitch-irc-capabilities
        .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
        .addCapHandler(new EnableCapHandler("twitch.tv/commands"))

        .addServer("irc.chat.twitch.tv", 6697)
        .setSocketFactory(SSLSocketFactory.getDefault())
        .setName(this.username)
        .setServerPassword(oauthKey);

    if (!channel.equals("")) {
       builder.addAutoJoinChannel("#" + this.channel);
    }

    Configuration config = builder.addListener(this)
        .setAutoSplitMessage(false)
        .buildConfiguration();

    this.ircBot = new PircBotX(config);
    this.myExecutor = Executors.newSingleThreadExecutor();
  }

  public void start() {
    TwitchChatMod.LOGGER.info("Twitch bot started");
    myExecutor.execute(() -> {
      try {
        ircBot.startBot();
      } catch (IOException | IrcException e) {
        e.printStackTrace();
      }
    });
  }

  public void stop() {
    ircBot.stopBotReconnect();
    ircBot.close();
  }

  public boolean isConnected() {
    return ircBot.isConnected();
  }

  @Override
  public void onMessage(MessageEvent event) throws Exception {
    try {
    String message = event.getMessage();
    TwitchChatMod.LOGGER.debug("TWITCH MESSAGE: " + message);
    User user = event.getUser();
    if (user != null) {
      ImmutableMap<String, String> v3Tags = event.getV3Tags();
      if (v3Tags != null) {
        String nick = user.getNick();
        if (!ModConfig.getConfig().getIgnoreList().contains(nick)) {
          TextColor formattingColor = this.getOrComputeUserColour(nick, v3Tags.get("color"));
          String formattedTime = TwitchChatMod.formatTMISentTimestamp(v3Tags.get("tmi-sent-ts"));

          String emotesString = v3Tags.get("emotes");
          List<TwitchAPIEmoteTagElement> emotes = null;
          // The twitch documentation is a bit lackluster. Here's some better info.
          // https://discuss.dev.twitch.com/t/message-tags-parse-emotes/1637/3
          if (!emotesString.startsWith("\\001ACTION")) {
            emotes = Arrays.stream(emotesString.split("/"))
              .flatMap(singleEmote -> TwitchAPIEmoteTagElement.fromTagString(singleEmote).stream())
              .collect(Collectors.toList());
          }

          TwitchChatMod.addTwitchMessage(formattedTime, nick, message, emotes, formattingColor, null,false);
        }
      } else {
        TwitchChatMod.LOGGER.warn("Message with no v3tags: " + event.getMessage());
      }
    } else {
      TwitchChatMod.LOGGER.warn("NON-USER MESSAGE" + event.getMessage());
    }} catch (Exception e) {e.printStackTrace();}
  }

  @Override
  public void onUnknown(UnknownEvent event) throws Exception {
    switch (event.getCommand()) {
      case "USERSTATE" -> {
        // Info about our user. More at https://dev.twitch.tv/docs/irc/commands/#userstate

        // Set our correct colour :).
        ImmutableMap<String, String> tags = event.getTags();
        String colorTag = tags.get("color");
        if (colorTag != null) {
          this.putFormattingColor(getUsername(), this.computeUserColour(getUsername(), colorTag));
        }

        // Set our correct badges for the current room.
        String badges = tags.get("badges");
        if (badges != null) {
          this.userBadges = badges.split(",");
          // WIP: Here we would check if the badges exist or not, and add them.
        }
      }
      case "CAP", "ROOMSTATE" -> TwitchChatMod.LOGGER.debug(event.getCommand() + " event: " + event);
      default -> TwitchChatMod.LOGGER.warn("UNKNOWN TWITCH EVENT: " + event);
    }
  }

  @Override
  public void onNotice(NoticeEvent event) {
    TwitchChatMod.LOGGER.debug("TWITCH NOTICE: " + event.toString());
    TwitchChatMod.addNotification(Text.literal(event.getNotice()));
  }

  @Override
  public void onKick(KickEvent event) {
    TwitchChatMod.LOGGER.debug("TWITCH KICK: " + event.toString());
    String message = event.getReason();
    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.kicked", message));
  }

  @Override
  public void onDisconnect(DisconnectEvent event) throws Exception {
    super.onDisconnect(event);
    TwitchChatMod.LOGGER.debug("TWITCH DISCONNECT: " + event.toString());
    Exception disconnectException = event.getDisconnectException();
  }

  // Handle /me
  @Override
  public void onAction(ActionEvent event) throws Exception {
    User user = event.getUser();

    if (user != null) {
      String nick = user.getNick();

      if (!ModConfig.getConfig().getIgnoreList().contains(nick.toLowerCase())) {
        String formattedTime = TwitchChatMod.formatTMISentTimestamp(event.getTimestamp());

        TextColor formattingColor = this.getOrComputeUserColour(nick);

        String emotesString = event.getTags().get("emotes");
        List<TwitchAPIEmoteTagElement> emotes = null;
        if (!emotesString.startsWith("\\001ACTION")) {
          emotes = Arrays.stream(emotesString.split("/"))
            .flatMap(singleEmote -> TwitchAPIEmoteTagElement.fromTagString(singleEmote).stream())
            .collect(Collectors.toList());
        }

        TwitchChatMod.addTwitchMessage(formattedTime, nick, event.getMessage(), emotes, formattingColor, null, true);
      }
    } else {
      TwitchChatMod.LOGGER.debug("NON-USER ACTION" + event.getMessage());
    }
  }

  Channel currentChannel;
  @Override
  public void onJoin(JoinEvent event) throws Exception {
    super.onJoin(event);
    Channel channel = event.getChannel();
     if (currentChannel == null || !currentChannel.equals(channel)) {
      TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.connected", this.channel));
      currentChannel = channel;
    }
  }

  /**
   * We MUST respond to this or else we will get kicked
   */
  @Override
  public void onPing(PingEvent event) {
    ircBot.sendRaw().rawLineNow(String.format("PONG %s\r\n", event.getPingValue()));
  }

  public void sendMessage(String message) {
    ircBot.sendIRC().message("#" + this.channel, message);
  }

  public String getUsername() {
    return username;
  }

  public void putFormattingColor(String nick, TextColor color) {
    formattingColorCache.put(nick.toLowerCase(), color);
  }

  // Get the user colour from the formatting colour cache, or otherwise, if not found,
  // calculate it before storing it again.
  public TextColor getOrComputeUserColour(String nick) {
    return this.getOrComputeUserColour(nick, null);
  }
  private TextColor getOrComputeUserColour(String nick, String hexColour) {
    return this.formattingColorCache.computeIfAbsent(nick.toLowerCase(), unusedNick -> this.computeUserColour(nick, hexColour));
  }
  private TextColor computeUserColour(String nick, String hexColour) {
    if (hexColour != null) {
      Color javaColour = Color.decode(hexColour);
      return TextColor.fromRgb(javaColour.getRGB());
    } else {
      return TwitchColourCalculator.getDefaultUserColor(nick);
    }
  }

  public void joinChannel(String channel) {
    String oldChannel = this.channel;
    this.channel = channel.toLowerCase();
    if (ircBot.isConnected()) {
      myExecutor.execute(() -> {
        ircBot.sendRaw().rawLine("PART #" + oldChannel); // Leave the channel
        ircBot.sendIRC().joinChannel("#" + this.channel); // Join the new channel
        ircBot.sendCAP().request("twitch.tv/membership", "twitch.tv/tags", "twitch.tv/commands"); // Ask for capabilities
      });
      // We're switching channels, so we have to clear our badges.
      this.userBadges = new String[0];
    }
  }

  public String[] getUserBadges() {
    return this.userBadges;
  }
}