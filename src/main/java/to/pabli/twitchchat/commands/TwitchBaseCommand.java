package to.pabli.twitchchat.commands;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;

public class TwitchBaseCommand implements BaseCommand {
  public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
      dispatcher.register(ClientCommandManager.literal("twitch")
          // The command to be executed if the command "twitch" is entered with the argument "enable"
          .then(new TwitchEnableCommand().getArgumentBuilder())
          // The command to be executed if the command "twitch" is entered with the argument "disable"
          .then(new TwitchDisableCommand().getArgumentBuilder())
          .then(new TwitchWatchCommand().getArgumentBuilder())
          .then(new TwitchBroadcastCommand().getArgumentBuilder())
          .executes(source -> {
              source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs1"));
              source.getSource().sendFeedback(new TranslatableText("text.twitchchat.command.base.noargs2"));
              return 1;
          })
      );
  }
}
