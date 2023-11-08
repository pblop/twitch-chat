package eu.pabl.twitchchat.emotes.twitch_api;

import java.util.ArrayList;
import java.util.List;

public record TwitchAPIEmoteTagElement(
  String emoteID,
  int startPosition,
  int endPosition
) {

  // As per: https://dev.twitch.tv/docs/irc/tags/#privmsg-tags
  public static List<TwitchAPIEmoteTagElement> fromTagString(String emotesString) {
    // Just so this makes sense, the format is "emoteID:emoteStart-emoteEnd".
    // If there are multiple emotes with the same id, and they're next to each other,
    //    e.g.: "Kappa x Kappa x"
    // the emoteID won't be repeated, like "emoteID:emoteStart-emoteEnd,emoteStart2-emoteEnd2".
    ArrayList<TwitchAPIEmoteTagElement> returnList = new ArrayList<>();

    String[] commaSeparated = emotesString.split(",");
    String lastEmoteID = null;

    for (String emoteTagString : commaSeparated) {
      int emoteIdSeparatorIdx = emoteTagString.indexOf(':');
      if (emoteIdSeparatorIdx != -1) {
        // We're basically doing a split, on the character ':', what's before is supposed to be an
        // emoteId, which is placed in lastEmotedID.
        // What's after will replace the emoteTagString, and it's supposed to be emoteStart-emoteEnd.
        // This way, the second part of the emote will parse correctly the common part to all emote
        // values.
        lastEmoteID = emoteTagString.substring(0, emoteIdSeparatorIdx);
        emoteTagString = emoteTagString.substring(emoteIdSeparatorIdx+1);
      }

      String[] split = emoteTagString.split("-");
      returnList.add(new TwitchAPIEmoteTagElement(lastEmoteID, Integer.parseInt(split[0]), Integer.parseInt(split[1])));
    }

    return returnList;
  }
}
