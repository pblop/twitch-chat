package to.pabli.twitchchat.commands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import to.pabli.twitchchat.emotes.EmoteManager;

public class TwitchBaseCommand implements ClientCommandPlugin {
  @Override
  public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
    dispatcher.register(ArgumentBuilders.literal("twitch")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        .then(TwitchEnableCommand.getArgumentBuilder())
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        .then(TwitchDisableCommand.getArgumentBuilder())
        .then(TwitchWatchCommand.getArgumentBuilder())
        .then(
                ArgumentBuilders.literal("test")
                        .executes(ctx -> {
                            EmoteManager.getInstance().downloadChatEmoticonsBySet("0");
                            return 1;
                        })
        )
        .executes(source -> {
          source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs1"));
          source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs2"));
          return 1;
        })
    );
  }
}
