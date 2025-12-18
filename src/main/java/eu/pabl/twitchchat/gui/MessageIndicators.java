package eu.pabl.twitchchat.gui;

import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

public class MessageIndicators {
  public static final MessageIndicator TWITCH_CHAT = new MessageIndicator(
    0x9146FF, // Twitch purple color
    null,
    Text.translatable("text.twitchchat.indicator.message"),
  "Twitch Chat"
  );
  public static final MessageIndicator TWITCH_SYSTEM = new MessageIndicator(
    0x6a3fb3, // Darker purple color
    null,
    Text.translatable("text.twitchchat.indicator.system"),
    "Twitch System"
  );
}
