package eu.pabl.twitchchat.twitch_integration;

import java.util.Arrays;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class CalculateMinecraftColor {
  public static final TextColor[] MINECRAFT_COLORS = Arrays.stream(Formatting.values()).filter(Formatting::isColor).map(TextColor::fromFormatting).toArray(TextColor[]::new);
  // Code gotten from here https://discuss.dev.twitch.tv/t/default-user-color-in-chat/385/2 but a little bit adjusted.
  public static TextColor getDefaultUserColor(String username) {
    char firstChar = username.charAt(0);
    char lastChar = username.charAt(username.length() - 1);

    int n = ((int) firstChar) + ((int) lastChar);
    return MINECRAFT_COLORS[n % MINECRAFT_COLORS.length];
  }
}
