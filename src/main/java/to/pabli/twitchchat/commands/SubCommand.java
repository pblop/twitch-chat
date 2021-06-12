package to.pabli.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public interface SubCommand {
    ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder();
}
