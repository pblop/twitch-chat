package to.pabli.twitchchat.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import java.util.function.Function;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

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
          .setTooltip(new TranslatableText("text.twitchchat.default.username.tooltip").asString())
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.oauthKey", ModConfig.getConfig().getOauthKey())
          .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
          .setTooltip(new TranslatableText("text.twitchchat.default.oauthKey.tooltip").asString())
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.prefix", ModConfig.getConfig().getPrefix())
          .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
          .setTooltip(new TranslatableText("text.twitchchat.default.prefix.tooltip").asString())
          .build());
      defaultCategory.addEntry(entryBuilder
          .startStrField("text.twitchchat.default.dateFormat", ModConfig.getConfig().getDateFormat())
          .setSaveConsumer((s -> ModConfig.getConfig().setDateFormat(s)))
          .setTooltip(new TranslatableText("text.twitchchat.default.dateFormat.tooltip").asString())
          .build());

      return builder.build();
    };
  }
}

