package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pabl.twitchchat.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class TwitchBroadcastCommand implements SubCommand {
  public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
    return ClientCommandManager.literal("broadcast")
        // The command to be executed if the command "twitch" is entered with the argument "broadcast"
        // It requires true/false as an argument.
        // It will toggle the broadcast flag in the config and
        // if enabled, will relay twitch messages as say-chat messages to the server.
        .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
            .executes(ctx -> {
              boolean enabled = BoolArgumentType.getBool(ctx, "enabled");

              ModConfig.getConfig().setBroadcastEnabled(enabled);
              // Also switch channels if the bot has been initialized
              if (enabled) {
                ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.broadcast.enabled"));
              } else {
                ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.broadcast.disabled"));
              }
              ModConfig.getConfig().save();
              return 1;
        }));
  }
}
