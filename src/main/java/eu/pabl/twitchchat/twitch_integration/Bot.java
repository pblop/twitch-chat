package eu.pabl.twitchchat.twitch_integration;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.ChannelJoinEvent;
import com.github.twitch4j.chat.events.channel.ChannelJoinFailureEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageActionEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.ChannelNoticeEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.domain.UserChatColorList;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import eu.pabl.twitchchat.TwitchChatMod;

public class Bot {
  private final TwitchClient twitchClient;
  private final String username;
  private String channel;
  private HashMap<String, Formatting> formattingColorCache; // Map of usernames to colors to keep consistency with usernames and colors

  public Bot(String username, String oauthKey, String channel) {
    this.channel = channel.toLowerCase();
    this.username = username.toLowerCase();
    this.formattingColorCache = new HashMap<>();

    OAuth2Credential oAuth2Credential = new OAuth2Credential("twitch", oauthKey);

    this.twitchClient = TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withEnableHelix(true)
        .withDefaultEventHandler(SimpleEventHandler.class)
        .withChatAccount(oAuth2Credential)
        .withDefaultAuthToken(oAuth2Credential)
        .withDefaultEventHandler(SimpleEventHandler.class)
        .build();

    this.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    this.twitchClient.getEventManager().onEvent(ChannelMessageActionEvent.class, this::onChannelMessageAction);
    this.twitchClient.getEventManager().onEvent(ChannelJoinEvent.class, this::onChannelJoin);
    this.twitchClient.getEventManager().onEvent(ChannelJoinFailureEvent.class, this::onChannelJoinFailure);
    this.twitchClient.getEventManager().onEvent(ChannelNoticeEvent.class, this::onChannelNotice);
    this.twitchClient.getEventManager().onEvent(IRCMessageEvent.class, ircMessageEvent -> {
      System.out.println("IRC: " + ircMessageEvent.toString());
    });

  }

  public void disable() {
    this.twitchClient.getChat().leaveChannel(this.channel);
    this.channel = "";
  }

  public boolean isConnected() {
    return !this.channel.equals("");
  }

  private void onChannelJoin(ChannelJoinEvent event) {
    // ERROR: This fires not only when the user joins the channel, but also when other users join
    //        the channel. Maybe filter by username/ID to only send the notification when THIS user
    //        joins the channel?
    //        Also, this fires more than once when the user joins the channel. Maybe only send the
    //        notification once? The different fires probably contain different information.
    // ALSO:  This event fires when the bot is started, but no channel is joined. I haven't yet
    //        checked what information is contained in that specific event.
    //        This causes a crash, because the in-game chat hud has not been initialized yet.
    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.connected", this.channel));
  }

  private static Text getTranslatedReasonText(ChannelJoinFailureEvent.Reason r) {
    // TODO: Add translations for the reasons (RETRIES_EXHAUSTED, USER_BANNED, CHANNEL_SUSPENDED).
    return Text.translatable("text.twitchchat.bot.join_failure_reason." + r.name().toLowerCase());
  }
  private void onChannelJoinFailure(ChannelJoinFailureEvent event) {
    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.join_failure",
        this.channel,
        getTranslatedReasonText(event.getReason()) // TODO: Maybe this requires a string instead of a Text?
    ));
  }

  private void onChannelMessage(ChannelMessageEvent event) {
    this.onChannelMessageAny(event, false);
  }
  private void onChannelMessageAction(ChannelMessageActionEvent event) {
    this.onChannelMessageAny(event, true);
  }
  private void onChannelMessageAny(AbstractChannelMessageEvent event, boolean isAction) {
    String username = event.getUser().getName();
    String userId = event.getUser().getId();
    String message = event.getMessage();

    String time = TwitchChatMod.formatDateTwitch(event.getFiredAt().getTime());

    // Get the user's color from the Helix API.
    UserChatColorList userChatColorList = this.twitchClient.getHelix()
        .getUserChatColor(null, Arrays.asList(userId)).execute();
    String userColorHex = userChatColorList.getData().get(0).getColor();

    Formatting textColor;
    if (this.isFormattingColorCached(username)) {
      textColor = this.getFormattingColor(username);
    } else {
      if (!userColorHex.equals("")) {
        Color userColor = Color.decode(userColorHex);
        textColor = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
      } else {
        textColor = CalculateMinecraftColor.getDefaultUserColor(username);
      }
      putFormattingColor(username, textColor);
    }

    TwitchChatMod.addTwitchMessage(time, username, message, textColor, isAction);
  }

  private void onChannelNotice(ChannelNoticeEvent event) {
    // TODO: Maybe get translations from Twitch, and add them to the translation files? Or try to
    //       get the messages translated already from Twitch?
    String message = event.getMessage();
    TwitchChatMod.addNotification(Text.literal(message));
  }

//
//  @Override
//  public void onKick(KickEvent event) {
//    System.out.println("TWITCH KICK: " + event.toString());
//    String message = event.getReason();
//    TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.kicked", message));
//  }
//
//  @Override
//  public void onDisconnect(DisconnectEvent event) throws Exception {
//    super.onDisconnect(event);
//    System.out.println("TWITCH DISCONNECT: " + event.toString());
//    Exception disconnectException = event.getDisconnectException();
//  }
//
//
//  Channel currentChannel;
//  @Override
//  public void onJoin(JoinEvent event) throws Exception {
//    super.onJoin(event);
//    Channel channel = event.getChannel();
//     if (currentChannel == null || !currentChannel.equals(channel)) {
//      TwitchChatMod.addNotification(Text.translatable("text.twitchchat.bot.connected", this.channel));
//      currentChannel = channel;
//    }
//  }
//

  public void sendMessage(String message) {
    this.twitchClient.getChat().sendMessage(this.channel, message);
  }

  public String getUsername() {
    return username;
  }

  public void putFormattingColor(String nick, Formatting color) {
    formattingColorCache.put(nick.toLowerCase(), color);
  }
  public Formatting getFormattingColor(String nick) {
    return formattingColorCache.get(nick.toLowerCase());
  }
  public boolean isFormattingColorCached(String nick) {
    return formattingColorCache.containsKey(nick.toLowerCase());
  }

  public void joinChannel(String channel) {
    this.twitchClient.getChat().leaveChannel(this.channel);

    this.channel = channel;
    this.twitchClient.getChat().joinChannel(channel);
  }
}