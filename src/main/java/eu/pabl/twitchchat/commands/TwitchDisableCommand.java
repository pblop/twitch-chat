package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pabl.twitchchat.TwitchChatMod;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TwitchDisableCommand implements SubCommand {
  public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
    // TODO: Maybe change the name of this command (and by extension its feedback messages) to 'disconnect' or 'leave'
    //       to make it more clear that it's not disabling the mod, just disconnecting from the Twitch channel.
    return ClientCommandManager.literal("disable")
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        // It shuts down the irc bot.
        .executes(ctx -> {
          if (!TwitchChatMod.bot.isConnected()) {
            ctx.getSource().sendFeedback(Text.translatable("text.twitchchat.command.disable.already_disabled"));
            return 1;
          }

          TwitchChatMod.bot.disable();
          ctx.getSource().sendFeedback(Text.translatable("text.twitchchat.command.disable.disabled").formatted(Formatting.DARK_GRAY));

          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
