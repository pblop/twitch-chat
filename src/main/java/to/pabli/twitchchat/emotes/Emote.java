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
    // e000 is the start of a PUA in Unicode
    private static char NEXT_IDENTIFIER = '\ue000';

    public static final Set<Emote> SET = new HashSet<>();

    private final String code;
    private final int id;
    // This is the character that will be put in place of this emote's code when found in any text in the game
    // And that will afterwards be switched with it's image when found in any text in the game.
    private final char charIdentifier;

    public Emote(String code, int id) {
        this.code = code;
        this.id = id;
        this.charIdentifier = NEXT_IDENTIFIER++;
    }

    public String getCode() {
        return code;
    }
    public int getId() {
        return id;
    }
    public char getCharIdentifier() {
        return charIdentifier;
    }
    public String getCharIdentifierAsString() {
        return Character.toString(getCharIdentifier());
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
