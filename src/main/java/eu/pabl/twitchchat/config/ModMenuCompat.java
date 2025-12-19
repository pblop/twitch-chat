package eu.pabl.twitchchat.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.twitchchat.titie"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("config.twitchchat.category.cosmetics"))
                                .option(
                                        Option.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.cosmetics.prefix"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.cosmetics.prefix.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_PREFIX,
                                                        () -> ModConfig.getConfig().getPrefix(),
                                                        newPrefix -> ModConfig.getConfig().setPrefix(newPrefix)
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.cosmetics.dateFormat"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.cosmetics.dateFormat.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_DATE_FORMAT,
                                                        () -> ModConfig.getConfig().getDateFormat(),
                                                        newDateFormat -> ModConfig.getConfig().setDateFormat(newDateFormat)
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        ListOption.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.cosmetics.ignorelist"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.cosmetics.ignorelist.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_IGNORE_LIST,
                                                        () -> ModConfig.getConfig().getIgnoreList(),
                                                        newIgnoreList -> ModConfig.getConfig().setIgnoreList(newIgnoreList)
                                                )
                                                .initial("")
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("config.twitchchat.cosmetics.twitchWatchSuggestions"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.cosmetics.twitchWatchSuggestions.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_TWITCH_WATCH_SUGGESTIONS,
                                                        () -> ModConfig.getConfig().areTwitchWatchSuggestionsEnabled(),
                                                        newWatchSuggestionsEnabled -> ModConfig.getConfig().setTwitchWatchSuggestions(newWatchSuggestionsEnabled)
                                                )
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("config.twitchchat.category.broadcast"))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("config.twitchchat.broadcast.toggle"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.broadcast.toggle.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_BROADCAST,
                                                        () -> ModConfig.getConfig().isBroadcastEnabled(),
                                                        newBroadcastEnabled -> ModConfig.getConfig().setBroadcastEnabled(newBroadcastEnabled)
                                                )
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.broadcast.prefix"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.broadcast.prefix.tooltip"))
                                                                .build())
                                                .binding(
                                                        ModConfig.DEFAULT_BROADCAST_PREFIX,
                                                        () -> ModConfig.getConfig().getBroadcastPrefix(),
                                                        newBroadcastPrefix -> ModConfig.getConfig().setBroadcastPrefix(newBroadcastPrefix)
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("config.twitchchat.category.credentials"))
                                .option(
                                        Option.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.credentials.username"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.credentials.username.tooltip"))
                                                                .build()
                                                )
                                                .binding(
                                                        ModConfig.DEFAULT_USERNAME,
                                                        () -> ModConfig.getConfig().getUsername(),
                                                        newUsername -> ModConfig.getConfig().setUsername(newUsername)
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<String>createBuilder()
                                                .name(Component.translatable("config.twitchchat.credentials.oauthKey"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("config.twitchchat.credentials.oauthKey.tooltip"))
                                                                .build()
                                                )
                                                .binding(
                                                        ModConfig.DEFAULT_OAUTH_KEY,
                                                        () -> ModConfig.getConfig().getOauthKey(),
                                                        newOauthKey -> ModConfig.getConfig().setOauthKey(newOauthKey)
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .save(() -> ModConfig.getConfig().save())
                .build().generateScreen(parentScreen);
    }
}

