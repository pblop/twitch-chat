package eu.pabl.twitchchat.twitch_integration;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import net.minecraft.util.Formatting;
import eu.pabl.twitchchat.TwitchChatMod;

public class Bot {
  private final TwitchClient twitchClient;
  private final String username;
  private String channel;
  private ExecutorService myExecutor;
  private HashMap<String, Formatting> formattingColorCache; // Map of usernames to colors to keep consistency with usernames and colors

  public Bot(String username, String oauthKey, String channel) {
    this.channel = channel.toLowerCase();
    this.username = username.toLowerCase();
    formattingColorCache = new HashMap<>();

    OAuth2Credential oAuth2Credential = new OAuth2Credential("twitch", oauthKey);

    this.twitchClient = TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withDefaultEventHandler(SimpleEventHandler.class)
        .withChatAccount(oAuth2Credential)
        .withDefaultAuthToken(oAuth2Credential)
        .build();

    this.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::onChannelMessage);
  }

  public void disable() {
    this.twitchClient.getChat().leaveChannel(this.channel);
    this.channel = "";
  }

  public boolean isConnected() {
    return !this.channel.equals("");
  }

  private void onChannelMessage(ChannelMessageEvent event) {
    String username = event.getUser().getName();
    String message = event.getMessage();

    String time = TwitchChatMod.formatDateTwitch(event.getFiredAt().getTime());
    // TODO: Helix supports getting the colour of a user, so we can use that instead of calculating
    //       the colour ourselves. It works the same way as it did previously with tags and pircbotx.
    Formatting textColor = CalculateMinecraftColor.getDefaultUserColor(username);

    TwitchChatMod.addTwitchMessage(time, username, message, textColor, false);
  }

//  @Override
//  public void onMessage(MessageEvent event) throws Exception {
//    String message = event.getMessage();
//    System.out.println("TWITCH MESSAGE: " + message);
//    User user = event.getUser();
//    if (user != null) {
//      ImmutableMap<String, String> v3Tags = event.getV3Tags();
//      if (v3Tags != null) {
//        String nick = user.getNick();
//        if (!ModConfig.getConfig().getIgnoreList().contains(nick)) {
//          String colorTag = v3Tags.get("color");
//          Formatting formattingColor;
//
//          if (isFormattingColorCached(nick)) {
//            formattingColor = getFormattingColor(nick);
//          } else {
//            if (colorTag.equals("")) {
//              formattingColor = CalculateMinecraftColor.getDefaultUserColor(nick);
//            } else {
//              Color userColor = Color.decode(colorTag);
//              formattingColor = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
//            }
//            putFormattingColor(nick, formattingColor);
//          }
//
//          String formattedTime = TwitchChatMod.formatTMISentTimestamp(v3Tags.get("tmi-sent-ts"));
//          TwitchChatMod.addTwitchMessage(formattedTime, nick, message, formattingColor, false);
//        }
//      } else {
//        System.out.println("Message with no v3tags: " + event.getMessage());
//      }
//    } else {
//      System.out.println("NON-USER MESSAGE" + event.getMessage());
//    }
//  }
//
//  @Override
//  public void onUnknown(UnknownEvent event) throws Exception {
//    System.out.println("UNKNOWN TWITCH EVENT: " + event.toString());
//  }
//
//  @Override
//  public void onNotice(NoticeEvent event) {
//    System.out.println("TWITCH NOTICE: " + event.toString());
//    TwitchChatMod.addNotification(Text.literal(event.getNotice()));
//  }
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
//  // Handle /me
//  @Override
//  public void onAction(ActionEvent event) throws Exception {
//    User user = event.getUser();
//
//    if (user != null) {
//      String nick = user.getNick();
//
//      if (!ModConfig.getConfig().getIgnoreList().contains(nick.toLowerCase())) {
//        String formattedTime = TwitchChatMod.formatTMISentTimestamp(event.getTimestamp());
//
//        Formatting formattingColor;
//        if (isFormattingColorCached(nick)) {
//          formattingColor = getFormattingColor(nick);
//        } else {
//          formattingColor = CalculateMinecraftColor.getDefaultUserColor(nick);
//          putFormattingColor(nick, formattingColor);
//        }
//
//        TwitchChatMod.addTwitchMessage(formattedTime, nick, event.getMessage(), formattingColor, true);
//      }
//    } else {
//      System.out.println("NON-USER ACTION" + event.getMessage());
//    }
//  }
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
//  /**
//   * We MUST respond to this or else we will get kicked
//   */
//  @Override
//  public void onPing(PingEvent event) {
//    ircBot.sendRaw().rawLineNow(String.format("PONG %s\r\n", event.getPingValue()));
//  }

  public void sendMessage(String message) {
//    ircBot.sendIRC().message("#" + this.channel, message);
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
//    String oldChannel = this.channel;
//    this.channel = channel.toLowerCase();
//    if (ircBot.isConnected()) {
//      myExecutor.execute(() -> {
//        ircBot.sendRaw().rawLine("PART #" + oldChannel); // Leave the channel
//        ircBot.sendIRC().joinChannel("#" + this.channel); // Join the new channel
//        ircBot.sendCAP().request("twitch.tv/membership", "twitch.tv/tags", "twitch.tv/commands"); // Ask for capabilities
//      });
//    }
  }
}