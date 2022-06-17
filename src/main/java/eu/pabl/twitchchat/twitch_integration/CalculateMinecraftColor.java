package eu.pabl.twitchchat.twitch_integration;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Formatting;

public class CalculateMinecraftColor {
  public static Formatting findNearestMinecraftColor(Color color) {
    return Arrays.stream(Formatting.values())
        .filter(Formatting::isColor)
        .map(formatting -> {
          Color formattingColor = new Color(formatting.getColorValue());

          int distance = Math.abs(color.getRed() - formattingColor.getRed()) +
              Math.abs(color.getGreen() - formattingColor.getGreen()) +
              Math.abs(color.getBlue() - formattingColor.getBlue());
          return new FormattingAndDistance(formatting, distance);
        })
        .sorted(Comparator.comparing(FormattingAndDistance::getDistance))
        .map(FormattingAndDistance::getFormatting)
        .findFirst()
        .orElse(Formatting.WHITE);
  }


  public static final Formatting[] MINECRAFT_COLORS = Arrays.stream(Formatting.values()).filter(Formatting::isColor).toArray(Formatting[]::new);
  // Code gotten from here https://discuss.dev.twitch.tv/t/default-user-color-in-chat/385/2 but a little bit adjusted.
  public static Map<String, Formatting> cachedNames = new HashMap<>();
  public static Formatting getDefaultUserColor(String username) {
    if (cachedNames.containsKey(username)) {
      return cachedNames.get(username);
    } else {
      // If we don't have the color cached, calculate it.
      char firstChar = username.charAt(0);
      char lastChar = username.charAt(username.length() - 1);

      int n = ((int) firstChar) + ((int) lastChar);
      return MINECRAFT_COLORS[n % MINECRAFT_COLORS.length];
    }
  }

  private static class FormattingAndDistance {
    private Formatting formatting;

    public Formatting getFormatting() {
      return formatting;
    }

    private int distance;

    public int getDistance() {
      return distance;
    }

    public FormattingAndDistance(Formatting formatting, int distance) {
      this.formatting = formatting;
      this.distance = distance;
    }
  }
}
