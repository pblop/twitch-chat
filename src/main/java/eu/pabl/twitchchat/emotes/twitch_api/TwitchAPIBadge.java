package eu.pabl.twitchchat.emotes.twitch_api;

public record TwitchAPIBadge (
  String id,
  String image_url_1x,
  String image_url_2x,
  String image_url_4x,
  String title,
  String description,
  String click_action,
  String click_url
) { }
