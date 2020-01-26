package to.pabli.twitchchat.twitch_integration;

import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import net.minecraft.util.Formatting;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.ListenerExceptionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import to.pabli.twitchchat.TwitchChatMod;

public class Bot extends ListenerAdapter {
  private final PircBotX ircBot;
  private final String username;
  public String channel;
  private Thread botRunThread;

  public Bot(String username, String oauthKey, String channel) {
    this.channel = channel.toLowerCase();
    this.username = username.toLowerCase();

    Configuration config = new Configuration.Builder()
        .setAutoNickChange(false) //Twitch doesn't support multiple users
        .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
        .setCapEnabled(true)
        .addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://dev.twitch.tv/docs/irc/guide/#twitch-irc-capabilities
        .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
        .addCapHandler(new EnableCapHandler("twitch.tv/commands"))

        .addServer("irc.chat.twitch.tv", 6697)
        .setSocketFactory(SSLSocketFactory.getDefault())
        .setName(this.username)
        .setServerPassword(oauthKey)
        .addAutoJoinChannel("#" + this.channel)

        .addListener(this)
        .setAutoSplitMessage(false)
        .buildConfiguration();

    ircBot = new PircBotX(config);
  }

  public void start() {
    System.out.println("TWITCH BOT STARTED");
    botRunThread = new Thread(() -> {
      try {
        ircBot.startBot();
      } catch (IOException | IrcException e) {
        e.printStackTrace();
      }
    });
    botRunThread.start();
  }

  public void stop() {
    ircBot.stopBotReconnect();
    ircBot.close();
//    botRunThread.interrupt();
  }

  public boolean isConnected() {
    return ircBot.isConnected();
  }

  @Override
  public void onMessage(MessageEvent event) throws Exception {
    String message = event.getMessage();
    System.out.println("TWITCH MESSAGE: " + message);
    User user = event.getUser();
    if (user != null) {
      ImmutableMap<String, String> v3Tags = event.getV3Tags();
      if (v3Tags != null) {
        String colorTag = v3Tags.get("color");
        Formatting formattingColor;
        if (colorTag.equals("")) {
          formattingColor = CalculateMinecraftColor.getDefaultUserColor(user.getNick());
        } else {
          Color userColor = Color.decode(colorTag);
          formattingColor = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
        }
        String formattedTime = TwitchChatMod.formatTMISentTimestamp(v3Tags.get("tmi-sent-ts"));
        TwitchChatMod.addTwitchMessage(formattedTime, user.getNick(), message, formattingColor);
      } else {
        System.out.println();
      }
    } else {
      System.out.println("NON-USER MESSAGE" + event.getMessage());
    }
  }

  @Override
  public void onUnknown(UnknownEvent event) throws Exception {
    System.out.println("UNKNOWN TWITCH EVENT: " + event.toString());
  }

  @Override
  public void onNotice(NoticeEvent event) {
    System.out.println("TWITCH NOTICE: " + event.toString());
    TwitchChatMod.addNotification(event.getNotice());
  }

  @Override
  public void onKick(KickEvent event) {
    System.out.println("TWITCH KICK: " + event.toString());
    String message = event.getReason();
    TwitchChatMod.addNotification("KICK: " + message);
  }

  @Override
  public void onDisconnect(DisconnectEvent event) throws Exception {
    super.onDisconnect(event);
    System.out.println("TWITCH DISCONNECT: " + event.toString());
    Exception disconnectException = event.getDisconnectException();
    TwitchChatMod.addNotification("DISCONNECT: " + disconnectException.getMessage());
  }

  Channel currentChannel;
  @Override
  public void onJoin(JoinEvent event) throws Exception {
    super.onJoin(event);
    Channel channel = event.getChannel();
     if (currentChannel == null || !currentChannel.equals(channel)) {
      TwitchChatMod.addNotification("Connected to channel " + this.channel);
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
}