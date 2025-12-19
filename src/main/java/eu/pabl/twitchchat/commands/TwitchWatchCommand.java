package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pabl.twitchchat.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import eu.pabl.twitchchat.TwitchChatMod;

public class TwitchWatchCommand implements SubCommand {
  public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
    return ClientCommandManager.literal("watch")
        // The command to be executed if the command "twitch" is entered with the argument "watch"
        // It requires channel_name as an argument.
        // It will switch channels in the config to the channel name provided and
        // if the bot is connected to some channel, it will switch channels on the fly.
        .then(ClientCommandManager.argument("channel_name", StringArgumentType.string())
            .suggests(new TwitchWatchSuggestionProvider())
            .executes(ctx -> {
              String channelName = StringArgumentType.getString(ctx, "channel_name");

              ModConfig.getConfig().setChannel(channelName);
              // Also switch channels if the bot has been initialized
              if (TwitchChatMod.bot != null) {
                ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.watch.switching", channelName));
                TwitchChatMod.bot.joinChannel(channelName);
              } else {
                ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.watch.connect_on_enable", channelName));
              }
              ModConfig.getConfig().save();
              return 1;
        }));
  }
}
