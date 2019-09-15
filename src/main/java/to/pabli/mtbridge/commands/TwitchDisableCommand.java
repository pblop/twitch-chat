package to.pabli.mtbridge.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import to.pabli.mtbridge.MTBridge;

public class TwitchDisableCommand {
  public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
    return ArgumentBuilders.literal("disable")
        .executes(ctx -> {
          if (MTBridge.bot == null || !MTBridge.bot.isConnected()) {
            ctx.getSource().sendFeedback(new LiteralText("Twitch integration is already disabled!"));
            return 1;
          }

          MTBridge.bot.stop();
          ctx.getSource().sendFeedback(new LiteralText("Twitch integration is now disabled!"));

          // Return a result. -1 is failure, 0 is a pass and 1 is success.
          return 1;
        });
  }
}
