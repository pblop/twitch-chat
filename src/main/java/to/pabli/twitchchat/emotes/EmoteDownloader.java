package to.pabli.twitchchat.emotes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import to.pabli.twitchchat.TwitchChatMod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmoteDownloader {
    private static EmoteDownloader INSTANCE;
    public static EmoteDownloader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmoteDownloader();
        }

        return INSTANCE;
    }

    private ExecutorService myExecutor;

    private EmoteDownloader() {
        this.myExecutor = Executors.newCachedThreadPool();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void downloadEmoteImage(Emote emote) {
        this.myExecutor.execute(() -> {
            try {
                FileUtils.copyURLToFile(
                        new URL("https://static-cdn.jtvnw.net/emoticons/v1/" + emote.getId() + "/1.0"),
                        emote.getLocalEmoteImage()
                );
            } catch (MalformedURLException e) {
                System.err.println("Somehow we malformed an emote url");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Exception copying url to file");
                e.printStackTrace();
            }
        });
    }

    public void downloadChatEmoticonsBySet(String set) {
        this.myExecutor.execute(() -> {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://api.twitch.tv/kraken/chat/emoticon_images?emotesets="+set);
            httpGet.setHeader("Accept", "application/vnd.twitchtv.v5+json");
            httpGet.setHeader("Client-ID", TwitchChatMod.CLIENT_ID);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String body = istos(httpResponse.getEntity().getContent());

                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(body);
                JsonObject emoticonSets = jsonObject.getAsJsonObject("emoticon_sets");
                JsonArray emoticonSet = emoticonSets.get(set).getAsJsonArray();

                for (JsonElement emoticonElement : emoticonSet) {
                    JsonObject emoticon = emoticonElement.getAsJsonObject();
                    Emote emote = new Emote(emoticon.get("code").getAsString(), emoticon.get("id").getAsInt());
                    if (!emote.hasLocalEmoteImageCopy()) {
                        downloadEmoteImage(emote);
                    }
                    Emote.SET.add(emote);
                }

                System.out.println(Emote.SET);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String istos(InputStream is) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "utf-8");
        return writer.toString();
    }
}
