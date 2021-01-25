package to.pabli.twitchchat.commands;


import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;

public class TwitchBaseCommand implements ClientCommandPlugin {
  @Override
  public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
    dispatcher.register(ArgumentBuilders.literal("twitch")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        .then(TwitchEnableCommand.getArgumentBuilder())
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        .then(TwitchDisableCommand.getArgumentBuilder())
        .then(TwitchWatchCommand.getArgumentBuilder())
        .then(TwitchBroadcastCommand.getArgumentBuilder())
        .executes(source -> {
          source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs1"));
          source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs2"));
          return 1;
        })
    );
  }
}
