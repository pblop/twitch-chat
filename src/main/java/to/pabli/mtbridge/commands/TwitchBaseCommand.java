package to.pabli.mtbridge.commands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;

public class TwitchBaseCommand implements ClientCommandPlugin {
  @Override
  public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
    dispatcher.register(ArgumentBuilders.literal("twitch")
        // The command to be executed if the command "twitch" is entered with the argument "enable"
        .then(TwitchEnableCommand.getArgumentBuilder())
        // The command to be executed if the command "twitch" is entered with the argument "disable"
        .then(TwitchDisableCommand.getArgumentBuilder())
        .executes(source -> {
          source.getSource().sendFeedback(new LiteralText("Welcome to the Minecraft-Twitch Bridge mod!"));
          source.getSource().sendFeedback(new LiteralText("To enable it just do /twitch enable when you're done setting up the config."));
          return 1;
        })
    );
  }
}
