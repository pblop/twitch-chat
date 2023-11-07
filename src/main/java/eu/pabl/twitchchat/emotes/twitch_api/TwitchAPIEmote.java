package eu.pabl.twitchchat.emotes.twitch_api;

import java.util.Map;

public record TwitchAPIEmote(
  String id,
  String name,
  Map<String, String> images,
  String[] format,
  String[] scale,
  String[] theme_mode
) { }
