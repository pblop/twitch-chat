package eu.pabl.twitchchat.twitch_integration;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.google.common.collect.ImmutableMap;
import eu.pabl.twitchchat.config.ModConfig;
import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import eu.pabl.twitchchat.TwitchChatMod;

public class Bot {
  private TwitchClient twitchClient = null;
  private final TwitchClientBuilder builder;
  private final String username;
  private String channel;
  private ExecutorService myExecutor;
  private HashMap<String, TextColor> formattingColorCache; // Map of usernames to colors to keep consistency with usernames and colors

  public Bot(String username, String oauthKey, String channel) {
    this.channel = channel.toLowerCase();
    this.username = username.toLowerCase();
    formattingColorCache = new HashMap<>();

    OAuth2Credential credential = new OAuth2Credential("twitch", oauthKey);
    builder = TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withChatAccount(credential);
//
//    Configuration.Builder builder = new Configuration.Builder()
//        .setAutoNickChange(false) //Twitch doesn't support multiple users
//        .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
//        .setEncoding(StandardCharsets.UTF_8) // Use UTF-8 on Windows.
//        .setCapEnabled(true)
//        .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://dev.twitch.tv/docs/irc/guide/#twitch-irc-capabilities
//        .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
//        .addCapHandler(new EnableCapHandler("twitch.tv/commands"))
//
//        .addServer("irc.chat.twitch.tv", 6697)
//        .setSocketFactory(SSLSocketFactory.getDefault())
//        .setName(this.username)
//        .setServerPassword(oauthKey);

//    if (!channel.equals("")) {
//       builder.addAutoJoinChannel("#" + this.channel);
//    }
//
//    Configuration config = builder.addListener(this)
//        .setAutoSplitMessage(false)
//        .buildConfiguration();

//    this.ircBot = new PircBotX(config);
    this.myExecutor = Executors.newSingleThreadExecutor();
  }

  public void start() {
    System.out.println("TWITCH BOT STARTED");
    myExecutor.execute(() -> {
      twitchClient = builder.build();
      EventManager evtMgr = twitchClient.getEventManager();

      evtMgr.onEvent(AbstractChannelMessageEvent.class, this::onMessage);
      evtMgr.onEvent(ChannelNoticeEvent.class, this::onNotice);
      evtMgr.onEvent(ChannelJoinEvent.class, this::onJoin);
      evtMgr.onEvent(ChannelLeaveEvent.class, this::onLeave);
      evtMgr.onEvent(UserTimeoutEvent.class, this::onTimeout);
      evtMgr.onEvent(UserBanEvent.class, this::onBan);

      // Auto-join the channel if specified
      if (!channel.isEmpty()) {
        twitchClient.getChat().joinChannel(channel);
      }
    });
  }

  public void stop() {
    System.out.println("TWITCH BOT STOPPED");
    if (twitchClient != null) {
      twitchClient.close();
      twitchClient = null;
    }
  }

  public boolean isConnected() {
    return twitchClient != null;
  }

  /**
   * Get the user's chat color, from cache if possible, otherwise from Twitch tags,
   * and if all else fails, calculate a default color.
   * @param event The IRC message event
   * @return The TextColor for the user
   */
  private TextColor calculateUserColor(IRCMessageEvent event) {
    String nick = event.getUser().getName();
    Optional<String> colorTag = event.getUserChatColor();
    TextColor formattingColor;

    if (isFormattingColorCached(nick)) {
      formattingColor = getFormattingColor(nick);
    } else if (colorTag.isPresent() && !colorTag.get().isEmpty()) {
      Color userColor = Color.decode(colorTag.get());
      formattingColor = TextColor.fromRgb(userColor.getRGB());
      putFormattingColor(nick, formattingColor);
    } else {
      formattingColor = CalculateMinecraftColor.getDefaultUserColor(nick);
      putFormattingColor(nick, formattingColor);
    }

    return formattingColor;
  }

  public void onMessage(AbstractChannelMessageEvent event) {
    boolean isActionMessage = event instanceof ChannelMessageActionEvent;
    String message = event.getMessage();
    IRCMessageEvent ircEvent = event.getMessageEvent();
    System.out.println("TWITCH MESSAGE: " + message);
    TextColor formattingColor = calculateUserColor(ircEvent);
    String nick = event.getUser().getName();

    Instant messageTimestamp = event.getFiredAtInstant();
    String formattedTime = TwitchChatMod.formatTMISentTimestamp(messageTimestamp);
    TwitchChatMod.addTwitchMessage(formattedTime, nick, message, formattingColor, isActionMessage);
  }

  public void onNotice(ChannelNoticeEvent event) {
    System.out.println("TWITCH NOTICE: " + event.toString());
    TwitchChatMod.addNotification(Text.literal(event.getMessage()));
  }

  public void onLeave(ChannelLeaveEvent event) {
    EventChannel channel = event.getChannel();
    String channelName = channel.getName();
    if (currentChannel != null && currentChannel.equals(channelName)) {
      TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.leave", this.channel));
      currentChannel = null;
    }
  }

  public void onTimeout(UserTimeoutEvent event) {
    String user = event.getUser().getName();
    String reason = event.getReason();
    int duration = event.getDuration();

    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.timeout", user, duration, reason));
  }
  public void onBan(UserBanEvent event) {
    String user = event.getUser().getName();
    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.ban", user));
  }

  String currentChannel;
  public void onJoin(ChannelJoinEvent event)  {
    EventChannel channel = event.getChannel();
    String channelName = channel.getName();
     if (currentChannel == null || !currentChannel.equals(channelName)) {
      TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.connected", this.channel));
      currentChannel = channelName;
    }
  }

  public void sendMessage(String message) {
    if (twitchClient != null && currentChannel != null)
      twitchClient.getChat().sendMessage(currentChannel, message);
  }

  public String getUsername() {
    return username;
  }

  public void putFormattingColor(String nick, TextColor color) {
    formattingColorCache.put(nick.toLowerCase(), color);
  }
  public TextColor getFormattingColor(String nick) {
    return formattingColorCache.get(nick.toLowerCase());
  }
  public boolean isFormattingColorCached(String nick) {
    return formattingColorCache.containsKey(nick.toLowerCase());
  }

  public void joinChannel(String channel) {
    this.channel = channel.toLowerCase();
    if (twitchClient != null) {
      myExecutor.execute(() -> {
        var chat = twitchClient.getChat();
        if (currentChannel != null && chat.isChannelJoined(currentChannel)) {
          chat.leaveChannel(currentChannel);
          currentChannel = null;
        }
        chat.joinChannel(this.channel);
      });
    }
  }
}