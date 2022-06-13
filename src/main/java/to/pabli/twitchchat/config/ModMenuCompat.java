package to.pabli.twitchchat.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.ArrayList;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (ConfigScreenFactory<Screen>) screen -> {
      ConfigBuilder builder = ConfigBuilder.create();
      builder.setTitle(Text.translatable("config.twitchchat.title"));
      builder.setSavingRunnable(() -> ModConfig.getConfig().save());


      ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

      ConfigCategory cosmeticsCategory = builder.getOrCreateCategory(Text.translatable("config.twitchchat.category.cosmetics"));
      cosmeticsCategory.addEntry(entryBuilder
              .startStrField(Text.translatable("config.twitchchat.cosmetics.prefix"), ModConfig.getConfig().getPrefix())
              .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
              .setTooltip(Text.translatable("config.twitchchat.cosmetics.prefix.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_PREFIX)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startStrField(Text.translatable("config.twitchchat.cosmetics.dateFormat"), ModConfig.getConfig().getDateFormat())
              .setSaveConsumer((s -> ModConfig.getConfig().setDateFormat(s)))
              .setTooltip(Text.translatable("config.twitchchat.cosmetics.dateFormat.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_DATE_FORMAT)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startStrList(Text.translatable("config.twitchchat.cosmetics.ignorelist"), ModConfig.getConfig().getIgnoreList())
              .setSaveConsumer((l -> ModConfig.getConfig().setIgnoreList(new ArrayList<>(l))))
              .setTooltip(Text.translatable("config.twitchchat.cosmetics.ignorelist.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_IGNORE_LIST)
              .build());
      cosmeticsCategory.addEntry(entryBuilder
              .startBooleanToggle(Text.translatable("config.twitchchat.cosmetics.twitchWatchSuggestions"), ModConfig.getConfig().areTwitchWatchSuggestionsEnabled())
              .setSaveConsumer((b -> ModConfig.getConfig().setTwitchWatchSuggestions(b)))
              .setTooltip(Text.translatable("config.twitchchat.cosmetics.twitchWatchSuggestions.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_TWITCH_WATCH_SUGGESTIONS)
              .build());

      ConfigCategory broadcastCategory = builder.getOrCreateCategory(Text.translatable("config.twitchchat.category.broadcast"));
      broadcastCategory.addEntry(entryBuilder
              .startBooleanToggle(Text.translatable("config.twitchchat.broadcast.toggle"), ModConfig.getConfig().isBroadcastEnabled())
              .setSaveConsumer((b -> ModConfig.getConfig().setBroadcastEnabled(b)))
              .setTooltip(Text.translatable("config.twitchchat.broadcast.toggle.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_BROADCAST)
              .build());
      broadcastCategory.addEntry(entryBuilder
              .startStrField(Text.translatable("config.twitchchat.broadcast.prefix"), ModConfig.getConfig().getBroadcastPrefix())
              .setSaveConsumer((s -> ModConfig.getConfig().setBroadcastPrefix(s)))
              .setTooltip(Text.translatable("config.twitchchat.broadcast.prefix.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_BROADCAST_PREFIX)
              .build());

      ConfigCategory credentialsCategory = builder.getOrCreateCategory(Text.translatable("config.twitchchat.category.credentials"));
      credentialsCategory.addEntry(entryBuilder
              .startStrField(Text.translatable("config.twitchchat.credentials.username"), ModConfig.getConfig().getUsername())
              .setSaveConsumer((s -> ModConfig.getConfig().setUsername(s)))
              .setTooltip(Text.translatable("config.twitchchat.credentials.username.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_USERNAME)
              .build());
      credentialsCategory.addEntry(entryBuilder
              .startStrField(Text.translatable("config.twitchchat.credentials.oauthKey"), ModConfig.getConfig().getOauthKey())
              .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
              .setTooltip(Text.translatable("config.twitchchat.credentials.oauthKey.tooltip"))
              .setDefaultValue(ModConfig.DEFAULT_OAUTH_KEY)
              .build());

      return builder.build();
    };
  }
}

