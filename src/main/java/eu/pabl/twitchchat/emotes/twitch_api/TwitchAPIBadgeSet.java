package eu.pabl.twitchchat.emotes.twitch_api;

public record TwitchAPIBadgeSet (
  String set_id,
  TwitchAPIBadge[] versions
) implements TwitchAPIObject { }
