package to.pabli.twitchchat.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import java.util.function.Function;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "twitchchat";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return (prevScreen) -> {
      ConfigBuilder builder = ConfigBuilder.create();
      builder.setTitle("text.twitchchat.title");
      builder.setSavingRunnable(() -> ModConfig.getConfig().save());


      ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

      ConfigCategory defaultCategory = builder.getOrCreateCategory("text.twitchchat.category.default");
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.username", ModConfig.getConfig().getUsername())
          .setSaveConsumer((s -> ModConfig.getConfig().setUsername(s)))
          .setTooltip("Your Twitch username")
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.oauthKey", ModConfig.getConfig().getOauthKey())
          .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
          .setTooltip("Your Twitch oauth key")
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.prefix", ModConfig.getConfig().getPrefix())
          .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
          .setTooltip("Put this at the start of your messages to send them to a Twitch channel")
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.dateFormat", ModConfig.getConfig().getDateFormat())
          .setSaveConsumer((s -> ModConfig.getConfig().setDateFormat(s)))
          .setTooltip("Customize the timestamp format messages are going to follow")
          .build());

      return builder.build();
    };
  }
}

