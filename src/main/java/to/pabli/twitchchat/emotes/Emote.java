package to.pabli.twitchchat.emotes;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Emote {
    // e000 is the start of a PUA in Unicode
    private static char NEXT_IDENTIFIER = '\ue000';

    private final String code;
    private final int id;
    // This is the character that will be put in place of this emote's code when found in any text in the game
    // And that will afterwards be switched with it's image when found in any text in the game.
    private final char charIdentifier;
    private EmoteRenderableGlyph renderableGlyph;

    public Emote(String code, int id) {
        this.code = code;
        this.id = id;
        this.charIdentifier = NEXT_IDENTIFIER++;
        this.renderableGlyph = null;

        // Automatically load the image if found
        if (hasLocalEmoteImageCopy()) loadRenderableGlyph();
    }
    public void loadRenderableGlyph() {
        try {
            this.renderableGlyph = new EmoteRenderableGlyph(this);
        } catch (IOException e) {
            System.err.println("Couldn't load image onto RenderableGlyph");
            e.printStackTrace();
        }
    }

    public String getCode() {
        return this.code;
    }
    public int getId() {
        return this.id;
    }
    public char getCharIdentifier() {
        return this.charIdentifier;
    }
    public EmoteRenderableGlyph getRenderableGlyph() {
        return this.renderableGlyph;
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

    // Important: this has to always be run in a different thread from the main game one, or else it will block the
    // game.
    public void downloadEmoteImage() {
        try {
            FileUtils.copyURLToFile(
                    new URL("https://static-cdn.jtvnw.net/emoticons/v1/" + id + "/1.0"),
                    getLocalEmoteImage()
            );
            loadRenderableGlyph();
        } catch (MalformedURLException e) {
            System.err.println("Somehow we malformed an emote url");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Exception copying url to file");
            e.printStackTrace();
        }
    }

    public boolean hasLocalEmoteImageCopy() {
        return getLocalEmoteImage().exists();
    }
}
