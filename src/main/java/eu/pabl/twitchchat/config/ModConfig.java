package eu.pabl.twitchchat.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {

  public static final String DEFAULT_CHANNEL = "";
  public static final String DEFAULT_USERNAME = "";
  public static final String DEFAULT_OAUTH_KEY = "";
  public static final String DEFAULT_PREFIX = ":";
  public static final String DEFAULT_DATE_FORMAT = "[H:mm] ";
  public static final List<String> DEFAULT_IGNORE_LIST = new ArrayList<>();
  public static final boolean DEFAULT_TWITCH_WATCH_SUGGESTIONS = false;
  public static final boolean DEFAULT_BROADCAST = false;
  public static final String DEFAULT_BROADCAST_PREFIX = "[Twitch] ";

  private static ModConfig SINGLE_INSTANCE = null;
  private final File configFile;

  private String channel;
  private String username;
  private String oauthKey;
  private String prefix;
  private String dateFormat;
  private List<String> ignoreList;
  private boolean twitchWatchSuggestions;
  private boolean broadcast;
  private String broadcastPrefix;

  public ModConfig() {
    this.configFile = FabricLoader
        .getInstance()
        .getConfigDir()
        .resolve("twitchchat.json")
        .toFile();
    this.channel = DEFAULT_CHANNEL;
    this.username = DEFAULT_USERNAME;
    this.oauthKey = DEFAULT_OAUTH_KEY;
    this.prefix = DEFAULT_PREFIX;
    this.dateFormat = DEFAULT_DATE_FORMAT;
    this.ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
    this.twitchWatchSuggestions = DEFAULT_TWITCH_WATCH_SUGGESTIONS;
    this.broadcast = DEFAULT_BROADCAST;
    this.broadcastPrefix = DEFAULT_BROADCAST_PREFIX;
  }

  public static ModConfig getConfig() {
    if (SINGLE_INSTANCE == null) {
      SINGLE_INSTANCE = new ModConfig();
    }

    return SINGLE_INSTANCE;
  }

  public void load() {
    try {
      String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
      if (!jsonStr. equals("")) {
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonStr);
        this.channel = jsonObject.has("channel")
                ? jsonObject.getAsJsonPrimitive("channel").getAsString()
                : DEFAULT_CHANNEL;
        this.username = jsonObject.has("username")
                ? jsonObject.getAsJsonPrimitive("username").getAsString()
                : DEFAULT_USERNAME;
        this.oauthKey = jsonObject.has("oauthKey")
                ? jsonObject.getAsJsonPrimitive("oauthKey").getAsString()
                : DEFAULT_OAUTH_KEY;
        this.prefix = jsonObject.has("prefix")
                ? jsonObject.getAsJsonPrimitive("prefix").getAsString()
                : DEFAULT_PREFIX;
        this.dateFormat = jsonObject.has("dateFormat")
                ? jsonObject.getAsJsonPrimitive("dateFormat").getAsString()
                : DEFAULT_DATE_FORMAT;

        if (jsonObject.has("ignoreList")) {
          JsonArray ignoreListJsonArray = jsonObject.getAsJsonArray("ignoreList");
          this.ignoreList = new ArrayList<>();
          for (JsonElement usernameJsonElement : ignoreListJsonArray) {
            this.ignoreList.add(usernameJsonElement.getAsString());
          }
        }

        this.twitchWatchSuggestions = jsonObject.has("twitchWatchSuggestions")
                ? jsonObject.getAsJsonPrimitive("twitchWatchSuggestions").getAsBoolean()
                : DEFAULT_TWITCH_WATCH_SUGGESTIONS;

        this.broadcast = jsonObject.has("broadcast")
                ? jsonObject.getAsJsonPrimitive("broadcast").getAsBoolean()
                : DEFAULT_BROADCAST;

        this.broadcastPrefix = jsonObject.has("broadcastPrefix")
                ? jsonObject.getAsJsonPrimitive("broadcastPrefix").getAsString()
                : DEFAULT_BROADCAST_PREFIX;
      }
    } catch (IOException e) {
      // Do nothing, we have no file and thus we have to keep everything as default
    }
  }

  public void save() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("channel", this.channel);
    jsonObject.addProperty("username", this.username);
    jsonObject.addProperty("oauthKey", this.oauthKey);
    jsonObject.addProperty("prefix", this.prefix);
    jsonObject.addProperty("dateFormat", this.dateFormat);

    JsonArray ignoreListJsonArray = new JsonArray();
    for (String username : this.ignoreList) {
      ignoreListJsonArray.add(username);
    }
    jsonObject.add("ignoreList", ignoreListJsonArray);

    jsonObject.addProperty("twitchWatchSuggestions", this.twitchWatchSuggestions);
    jsonObject.addProperty("broadcast", this.broadcast);
    jsonObject.addProperty("broadcastPrefix", this.broadcastPrefix);
    try (PrintWriter out = new PrintWriter(configFile)) {
       out.println(jsonObject.toString());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(String oauthKey) {
    if (oauthKey.isEmpty() || oauthKey.startsWith("oauth:")) {
      this.oauthKey = oauthKey;
    } else {
      this.oauthKey = "oauth:" + oauthKey;
    }
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public List<String> getIgnoreList() {
    return ignoreList;
  }

  public void setIgnoreList(List<String> ignoreList) {
    // Force all usernames to be lowercase
    this.ignoreList = ignoreList.parallelStream().map(String::toLowerCase).collect(Collectors.toList());
  }

  public boolean areTwitchWatchSuggestionsEnabled() {
    return twitchWatchSuggestions;
  }

  public void setTwitchWatchSuggestions(boolean twitchWatchSuggestions) {
    this.twitchWatchSuggestions = twitchWatchSuggestions;
  }

  public boolean isBroadcastEnabled() {
    return broadcast;
  }

  public void setBroadcastEnabled(boolean broadcastEnabled) {
    this.broadcast = broadcastEnabled;
  }

  public String getBroadcastPrefix() {
    return broadcastPrefix;
  }

  public void setBroadcastPrefix(String broadcastPrefix) {
    this.broadcastPrefix = broadcastPrefix;
  }
}
