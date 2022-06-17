package to.pabli.twitchchat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface BaseCommand {
    void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
