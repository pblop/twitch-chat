package eu.pabl.twitchchat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.pabl.twitchchat.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchWatchSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        if (Minecraft.getInstance().level != null && ModConfig.getConfig().areTwitchWatchSuggestionsEnabled()) {
            List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();

            for (AbstractClientPlayer player : players) {
                builder.suggest(player.getName().getString());
            }
        }

        return builder.buildFuture();
    }
}
