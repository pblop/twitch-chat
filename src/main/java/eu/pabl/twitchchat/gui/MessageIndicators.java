package eu.pabl.twitchchat.gui;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;

public class MessageIndicators {
  public static final GuiMessageTag TWITCH_CHAT = new GuiMessageTag(
    0x9146FF, // Twitch purple color
    null,
    Component.translatable("text.twitchchat.indicator.message"),
  "Twitch Chat"
  );
  public static final GuiMessageTag TWITCH_SYSTEM = new GuiMessageTag(
    0x6a3fb3, // Darker purple color
    null,
    Component.translatable("text.twitchchat.indicator.system"),
    "Twitch System"
  );
}
