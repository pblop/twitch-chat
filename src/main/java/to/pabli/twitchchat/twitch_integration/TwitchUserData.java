package to.pabli.twitchchat.twitch_integration;

import net.minecraft.util.Formatting;

public class TwitchUserData {
    private String displayName;
    private Formatting userColor;

    public TwitchUserData(String displayName) {
        this.displayName = displayName;
        this.userColor = CalculateMinecraftColor.getDefaultUserColor(displayName);
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public Formatting getUserColor() {
        return userColor;
    }
    public void setUserColor(Formatting userColor) {
        this.userColor = userColor;
    }
}
