package to.pabli.twitchchat.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

import java.util.ArrayList;
import java.util.function.Function;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (ConfigScreenFactory<Screen>) screen -> {
      ConfigBuilder builder = ConfigBuilder.create();
      builder.setTitle(new TranslatableText("config.twitchchat.title"));
      builder.setSavingRunnable(() -> ModConfig.getConfig().save());


      ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

      ConfigCategory defaultCategory = builder.getOrCreateCategory(new TranslatableText("config.twitchchat.category.default"));
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("config.twitchchat.default.username"), ModConfig.getConfig().getUsername())
              .setSaveConsumer((s -> ModConfig.getConfig().setUsername(s)))
              .setTooltip(new TranslatableText("config.twitchchat.default.username.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_USERNAME)
              .build());
      defaultCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("config.twitchchat.default.oauthKey"), ModConfig.getConfig().getOauthKey())
              .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
              .setTooltip(new TranslatableText("config.twitchchat.default.oauthKey.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_OAUTH_KEY)
              .build());

      ConfigCategory cosmeticsCategory = builder.getOrCreateCategory(new TranslatableText("config.twitchchat.category.cosmetics"));
      cosmeticsCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("config.twitchchat.cosmetics.prefix"), ModConfig.getConfig().getPrefix())
              .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
              .setTooltip(new TranslatableText("config.twitchchat.cosmetics.prefix.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_PREFIX)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startStrField(new TranslatableText("config.twitchchat.cosmetics.dateFormat"), ModConfig.getConfig().getDateFormat())
              .setSaveConsumer((s -> ModConfig.getConfig().setDateFormat(s)))
              .setTooltip(new TranslatableText("config.twitchchat.cosmetics.dateFormat.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_DATE_FORMAT)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startStrList(new TranslatableText("config.twitchchat.cosmetics.ignorelist"), ModConfig.getConfig().getIgnoreList())
              .setSaveConsumer((l -> ModConfig.getConfig().setIgnoreList(new ArrayList<>(l))))
              .setTooltip(new TranslatableText("config.twitchchat.cosmetics.ignorelist.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_IGNORE_LIST)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startBooleanToggle(new TranslatableText("config.twitchchat.cosmetics.twitchWatchSuggestions"), ModConfig.getConfig().areTwitchWatchSuggestionsEnabled())
              .setSaveConsumer((b -> ModConfig.getConfig().setTwitchWatchSuggestions(b)))
              .setTooltip(new TranslatableText("config.twitchchat.cosmetics.twitchWatchSuggestions.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_TWITCH_WATCH_SUGGESTIONS)
              .build());

      return builder.build();
    };
  }
}

