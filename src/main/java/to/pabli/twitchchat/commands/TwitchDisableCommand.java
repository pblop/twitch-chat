package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.TwitchChatMod;

public class TwitchDisableCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("disable")
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        // It shuts down the irc bot.
        .executes(ctx -> {
          if (TwitchChatMod.bot == null || !TwitchChatMod.bot.isConnected()) {
            ctx.getSource().sendFeedback(new LiteralText("Twitch integration is already disabled!"));
            return 1;
          }

          TwitchChatMod.bot.stop();
          ctx.getSource().sendFeedback(new LiteralText("Twitch integration is now disabled!").formatted(
              Formatting.DARK_GRAY));

          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
