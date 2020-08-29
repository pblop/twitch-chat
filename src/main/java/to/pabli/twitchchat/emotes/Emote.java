package to.pabli.twitchchat.emotes;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Emote {
    public static final Set<Emote> SET = new HashSet<>();

    private final String code;
    private final int id;

    public Emote(String code, int id) {
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public int getId() {
        return id;
    }

    public File getLocalEmoteImage() {
        return FabricLoader
            .getInstance()
            .getConfigDirectory()
            .toPath()
            .resolve("twitch-chat")
            .resolve("emotes")
            .resolve(this.id + ".png")
            .toFile();
    }

    public boolean hasLocalEmoteImageCopy() {
        return getLocalEmoteImage().exists();
    }
}
