package to.pabli.mtbridge.config;

import java.util.Arrays;
import java.util.List;
import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1.serializer.PartitioningSerializer;
import me.sargunvohra.mcmods.autoconfig1.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("unused")
@Config(name = "mtbridge")
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

