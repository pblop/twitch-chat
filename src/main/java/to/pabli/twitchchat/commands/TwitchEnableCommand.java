package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import to.pabli.twitchchat.TwitchChatMod;

public class TwitchEnableCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("enable")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        .executes(ctx -> {
//          ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

          if (TwitchChatMod.bot != null && TwitchChatMod.bot.isConnected()) {
            ctx.getSource().sendFeedback(new LiteralText("Twitch integration is already enabled!"));
            return 1;
          }

//          TwitchChatMod.bot = new Bot(config.username, config.oauthKey, config.channel);
//          TwitchChatMod.bot.start();
          ctx.getSource().sendFeedback(new LiteralText("Connecting...").formatted(Formatting.DARK_GRAY));
          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
