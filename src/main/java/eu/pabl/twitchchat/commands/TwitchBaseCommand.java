package eu.pabl.twitchchat.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

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
              source.getSource().sendFeedback(Text.translatable("text.twitchchat.command.base.noargs1"));
              source.getSource().sendFeedback(Text.translatable("text.twitchchat.command.base.noargs2"));
              return 1;
          })
      );
  }
}
