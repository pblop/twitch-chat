package to.pabli.twitchchat.config;

public class ModConfig {

    private static ModConfig SINGLE_INSTANCE = null;

    private String channel;
    private String username;
    private String oauthKey;
    private String prefix;

    public ModConfig() {
        this.channel = "";
        this.username = "";
        this.oauthKey = "";
        this.prefix = "";
    }

    public static ModConfig getConfig() {
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ModConfig();
        }

        return SINGLE_INSTANCE;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOauthKey() {
        return oauthKey;
    }

    public void setOauthKey(String oauthKey) {
        this.oauthKey = oauthKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
