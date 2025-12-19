package eu.pabl.twitchchat.twitch_integration;

import net.minecraft.network.chat.TextColor;

public class CalculateMinecraftColor {
  public static final TextColor[] DEFAULT_COLORS = new TextColor[]{
          TextColor.fromRgb(0xFF0000),
          TextColor.fromRgb(0x0000FF),
          TextColor.fromRgb(0x00FF00),
          TextColor.fromRgb(0xB22222),
          TextColor.fromRgb(0xFF7F50),
          TextColor.fromRgb(0x9ACD32),
          TextColor.fromRgb(0xFF4500),
          TextColor.fromRgb(0x2E8B57),
          TextColor.fromRgb(0xDAA520),
          TextColor.fromRgb(0xD2691E),
          TextColor.fromRgb(0x5F9EA0),
          TextColor.fromRgb(0x1E90FF),
          TextColor.fromRgb(0xFF69B4),
          TextColor.fromRgb(0x8A2BE2),
          TextColor.fromRgb(0x00FF7F)
  };
  // Code gotten from here https://discuss.dev.twitch.tv/t/default-user-color-in-chat/385/2 but a little bit adjusted.
  public static TextColor getDefaultUserColor(String username) {
    char firstChar = username.charAt(0);
    char lastChar = username.charAt(username.length() - 1);

    int n = ((int) firstChar) + ((int) lastChar);
    return DEFAULT_COLORS[n % DEFAULT_COLORS.length];
  }
}
