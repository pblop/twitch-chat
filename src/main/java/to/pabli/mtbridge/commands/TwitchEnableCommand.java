package to.pabli.mtbridge.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.text.LiteralText;
import to.pabli.mtbridge.MTBridge;
import to.pabli.mtbridge.config.ModConfig;
import to.pabli.mtbridge.twitch_integration.Bot;

public class TwitchEnableCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("enable")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        .executes(ctx -> {
          ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

          if (MTBridge.bot != null && MTBridge.bot.isConnected()) {
            ctx.getSource().sendFeedback(new LiteralText("Twitch integration is already enabled!"));
            return 1;
          }

          MTBridge.bot = new Bot(config.username, config.oauthKey, config.channel);
          MTBridge.bot.start();
          ctx.getSource().sendFeedback(new LiteralText("Connecting..."));
          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
