package to.pabli.twitchchat.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import to.pabli.twitchchat.config.ModConfig;

public class TwitchBroadcastCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("broadcast")
        // The command to be executed if the command "twitch" is entered with the argument "broadcast"
        // It requires true/false as an argument.
        // It will toggle the broadcast flag in the config and
        // if enabled, will relay twitch messages as say-chat messages to the server.
        .then(ArgumentBuilders.argument("enabled", BoolArgumentType.bool())
            .executes(ctx -> {
              boolean enabled = BoolArgumentType.getBool(ctx, "enabled");

              ModConfig.getConfig().setBroadcastEnabled(enabled);
              // Also switch channels if the bot has been initialized
              if (enabled) {
                ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.broadcast.enabled"));
              } else {
                ctx.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.broadcast.disabled"));
              }
              ModConfig.getConfig().save();
              return 1;
        }));
  }
}
