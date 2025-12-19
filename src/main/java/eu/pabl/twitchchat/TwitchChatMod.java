package eu.pabl.twitchchat;

import eu.pabl.twitchchat.commands.TwitchBaseCommand;
import eu.pabl.twitchchat.config.ModConfig;
import eu.pabl.twitchchat.twitch_integration.Bot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchChatMod implements ModInitializer {
  public static Bot bot;
  public static Logger LOGGER = LoggerFactory.getLogger("TwitchChat");

  @Override
  public void onInitialize() {
    ModConfig.getConfig().load();

    // Register commands
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
        new TwitchBaseCommand().registerCommands(dispatcher));
  }

}
