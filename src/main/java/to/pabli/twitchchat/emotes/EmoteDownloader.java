package to.pabli.twitchchat.emotes;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import to.pabli.twitchchat.config.ModConfig;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmoteDownloader {
    private ExecutorService myExecutor;
    private static EmoteDownloader SINGLE_INSTANCE;

    private EmoteDownloader() {
        this.myExecutor = Executors.newCachedThreadPool();
    }

    public static EmoteDownloader getConfig() {
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new EmoteDownloader();
        }

        return SINGLE_INSTANCE;
    }


    public File getBadgeFile(String channelId) {
        return FabricLoader
                .getInstance()
                .getConfigDirectory()
                .toPath()
                .resolve("twitch-chat")
                .resolve("badges")
                .resolve(channelId + ".json")
                .toFile();
    }

    public void downloadBadges(String channelId) {
        this.myExecutor.execute(() -> {
            try {
                if (getBadgeFile(channelId).exists()) return;
                String badgesUrlString = channelId.equals("global")
                        ? "https://badges.twitch.tv/v1/badges/global/display"
                        : "https://badges.twitch.tv/v1/badges/channels/" + channelId + "/display";
                FileUtils.copyURLToFile(
                        new URL(badgesUrlString),
                        getBadgeFile(channelId)
                );
            } catch (IOException ignored) { }
        });
    }
}
