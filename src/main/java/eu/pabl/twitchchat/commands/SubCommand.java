package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface SubCommand {
    ArgumentBuilder<FabricClientCommandSource, ?> getArgumentBuilder();
}
