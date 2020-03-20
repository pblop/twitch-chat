package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.TwitchChatMod;
import to.pabli.twitchchat.config.ModConfig;
import to.pabli.twitchchat.twitch_integration.Bot;

public class TwitchEnableCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("enable")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        // It starts up the irc bot.
        .executes(ctx -> {
          ModConfig config = ModConfig.getConfig();

          if (TwitchChatMod.bot != null && TwitchChatMod.bot.isConnected()) {
            ctx.getSource().sendFeedback(new LiteralText("Twitch integration is already enabled!"));
            return 1;
          }

          if (config.getUsername().equals("") || config.getOauthKey().equals("")) {
            ctx.getSource().sendFeedback(new LiteralText("Before doing that you have to set your config!"));
            return -1;
          }

          if (config.getChannel().equals("")) {
            ctx.getSource().sendFeedback(new LiteralText("You won't connect to a channel because you haven't selected any. You can select a channel with /twitch channel [channel]"));
          }

          TwitchChatMod.bot = new Bot(config.getUsername(), config.getOauthKey(), config.getChannel());
          TwitchChatMod.bot.start();
          ctx.getSource().sendFeedback(new LiteralText("Connecting...").formatted(Formatting.DARK_GRAY));
          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
