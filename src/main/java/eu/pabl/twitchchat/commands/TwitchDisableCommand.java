package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pabl.twitchchat.TwitchChatMod;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class TwitchDisableCommand implements SubCommand {
  public ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder() {
    return ClientCommandManager.literal("disable")
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        // It shuts down the irc bot.
        .executes(ctx -> {
          if (TwitchChatMod.bot == null || !TwitchChatMod.bot.isConnected()) {
            ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.disable.already_disabled"));
            return 1;
          }

          TwitchChatMod.bot.stop();
          ctx.getSource().sendFeedback(Component.translatable("text.twitchchat.command.disable.disabled").withStyle(
              ChatFormatting.DARK_GRAY));

          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
