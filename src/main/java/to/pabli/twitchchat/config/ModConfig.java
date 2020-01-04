package to.pabli.twitchchat.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {

  private static ModConfig SINGLE_INSTANCE = null;
  private File configFile;

  private String channel;
  private String username;
  private String oauthKey;
  private String prefix;

  public ModConfig() {
    this.configFile = FabricLoader
        .getInstance()
        .getConfigDirectory()
        .toPath()
        .resolve("config.json")
        .toFile();
    this.channel = "";
    this.username = "";
    this.oauthKey = "";
    this.prefix = "";
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
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonStr);
        this.channel = jsonObject.getAsJsonPrimitive("channel").getAsString();
        this.username = jsonObject.getAsJsonPrimitive("username").getAsString();
        this.oauthKey = jsonObject.getAsJsonPrimitive("oauthKey").getAsString();
        this.prefix = jsonObject.getAsJsonPrimitive("prefix").getAsString();

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
    this.oauthKey = oauthKey;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}
