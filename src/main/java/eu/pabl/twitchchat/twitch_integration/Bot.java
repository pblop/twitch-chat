package eu.pabl.twitchchat.twitch_integration;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.pabl.twitchchat.gui.ChatMessages;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import eu.pabl.twitchchat.TwitchChatMod;

public class Bot {
  private TwitchClient twitchClient = null;
  private final TwitchClientBuilder builder;
  private final String username;
  private String autoJoinChannel;
  private ExecutorService myExecutor;
  private HashMap<String, TextColor> formattingColorCache; // Map of usernames to colors to keep consistency with usernames and colors

  public Bot(String username, String oauthKey, String autoJoinChannel) {
    this.autoJoinChannel = autoJoinChannel.toLowerCase();
    this.username = username.toLowerCase();
    formattingColorCache = new HashMap<>();

    OAuth2Credential credential = new OAuth2Credential("twitch", oauthKey);
    builder = TwitchClientBuilder.builder()
      .withEnableChat(true)
      .withChatAutoJoinOwnChannel(false)
      .withChatAccount(credential);

    this.myExecutor = Executors.newSingleThreadExecutor();
  }

  public void start() {
    TwitchChatMod.LOGGER.info("Starting Twitch bot...");
    myExecutor.execute(() -> {
      twitchClient = builder.build();
      EventManager evtMgr = twitchClient.getEventManager();

      evtMgr.onEvent(AbstractChannelMessageEvent.class, this::onMessage);
      evtMgr.onEvent(ChannelNoticeEvent.class, this::onNotice);
      evtMgr.onEvent(ChannelJoinEvent.class, this::onJoin);
      evtMgr.onEvent(ChannelLeaveEvent.class, this::onLeave);
      evtMgr.onEvent(UserTimeoutEvent.class, this::onTimeout);
      evtMgr.onEvent(UserBanEvent.class, this::onBan);
      evtMgr.onEvent(UserStateEvent.class, this::onUserState);

      if (!autoJoinChannel.isEmpty()) {
        twitchClient.getChat().joinChannel(autoJoinChannel);
      }

    });
  }

  public void stop() {
    TwitchChatMod.LOGGER.info("Stopping Twitch bot...");
    if (twitchClient != null) {
      twitchClient.close();
      twitchClient = null;
      currentChannel = null;
    }
  }

  public boolean isConnected() {
    return twitchClient != null;
  }

  public void onUserState(UserStateEvent event) {
    // Info about our user. More at https://dev.twitch.tv/docs/chat/irc/#userstate-tags

    // Set our own username color if we got it.
    Optional<String> color = event.getColor();
    if (color.isPresent() && !color.get().isEmpty()) {
      Color userColor = Color.decode(color.get());
      TextColor formattingColor = TextColor.fromRgb(userColor.getRGB());
      putFormattingColor(username, formattingColor);
    }

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
    TextColor formattingColor = calculateUserColor(ircEvent);
    String nick = event.getUser().getName();

    Instant messageTimestamp = event.getFiredAtInstant();
    String formattedTime = ChatMessages.formatTMISentTimestamp(messageTimestamp);
    ChatMessages.addTwitchMessage(formattedTime, nick, message, formattingColor, isActionMessage);
  }

  public void onNotice(ChannelNoticeEvent event) {
    ChatMessages.addNotification(Component.literal(event.getMessage()));
  }

  public void onLeave(ChannelLeaveEvent event) {
    if (!event.getUser().getName().equalsIgnoreCase(this.username)) {
      // Ignore leave events for other users
      return;
    }

    EventChannel channel = event.getChannel();
    String channelName = channel.getName();

    ChatMessages.addNotification(Component.translatable("text.twitchchat.bot.disconnected", channelName));

    if (currentChannel != null && currentChannel.equals(channelName)) {
      currentChannel = null;
    }
  }

  public void onTimeout(UserTimeoutEvent event) {
    String user = event.getUser().getName();
    String reason = event.getReason();
    int duration = event.getDuration();

    ChatMessages.addNotification(Component.translatable("text.twitchchat.bot.timeout", user, duration, reason));
  }
  public void onBan(UserBanEvent event) {
    String user = event.getUser().getName();
    ChatMessages.addNotification(Component.translatable("text.twitchchat.bot.ban", user));
  }

  String currentChannel;
  public void onJoin(ChannelJoinEvent event)  {
    if (!event.getUser().getName().equalsIgnoreCase(this.username)) {
      // Ignore join events for other users
      return;
    }
    EventChannel channel = event.getChannel();
    String channelName = channel.getName();

    if (currentChannel == null || !currentChannel.equals(channelName)) {
      ChatMessages.addNotification(Component.translatable("text.twitchchat.bot.connected", channelName));
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
    this.autoJoinChannel = channel.toLowerCase();
    if (twitchClient != null) {
      myExecutor.execute(() -> {
        var chat = twitchClient.getChat();
        if (currentChannel != null && chat.isChannelJoined(currentChannel)) {
          chat.leaveChannel(currentChannel);
          currentChannel = null;
        }
        chat.joinChannel(this.autoJoinChannel);
      });
    }
  }
}