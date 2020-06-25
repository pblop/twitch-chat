package to.pabli.twitchchat.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
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
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (ConfigScreenFactory<Screen>) screen -> {
      ConfigBuilder builder = ConfigBuilder.create();
      builder.setTitle(new TranslatableText("text.twitchchat.title"));
      builder.setSavingRunnable(() -> ModConfig.getConfig().save());


      ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

      ConfigCategory defaultCategory = builder.getOrCreateCategory(new TranslatableText("text.twitchchat.category.default"));
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("text.twitchchat.default.username"), ModConfig.getConfig().getUsername())
              .setSaveConsumer((s -> ModConfig.getConfig().setUsername(s)))
              .setTooltip(new TranslatableText("text.twitchchat.default.username.tooltip"))
              .build());
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("text.twitchchat.default.oauthKey"), ModConfig.getConfig().getOauthKey())
              .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
              .setTooltip(new TranslatableText("text.twitchchat.default.oauthKey.tooltip"))
              .build());
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("text.twitchchat.default.prefix"), ModConfig.getConfig().getPrefix())
              .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
              .setTooltip(new TranslatableText("text.twitchchat.default.prefix.tooltip"))
              .build());
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("text.twitchchat.default.dateFormat"), ModConfig.getConfig().getDateFormat())
              .setSaveConsumer((s -> ModConfig.getConfig().setDateFormat(s)))
              .setTooltip(new TranslatableText("text.twitchchat.default.dateFormat.tooltip"))
              .build());

      return builder.build();
    };
  }
}

