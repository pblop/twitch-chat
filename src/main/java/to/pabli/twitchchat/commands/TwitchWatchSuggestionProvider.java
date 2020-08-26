package to.pabli.twitchchat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.registry.Registry;
import to.pabli.twitchchat.config.ModConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchWatchSuggestionProvider implements SuggestionProvider<CottonClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CottonClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        if (MinecraftClient.getInstance().world != null && ModConfig.getConfig().areTwitchWatchSuggestionsEnabled()) {
            List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();

            for (AbstractClientPlayerEntity player : players) {
                builder.suggest(player.getName().asString());
            }
        }

        return builder.buildFuture();
    }
}
