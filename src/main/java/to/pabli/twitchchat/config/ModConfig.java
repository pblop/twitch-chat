package to.pabli.twitchchat.config;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("unused")
@Config(name = "twitchchat")
public class ModConfig implements ConfigData {
  @Comment("The channel name you want to connect to")
  public String channel = "";

  @Comment("Your Twitch username")
  public String username = "";

  @Comment("Your Twitch oauth key")
  public String oauthKey = "";

  @Comment("Put this at the start of your messages to send them to a twitch channel")
  public String prefix = ":";
}

