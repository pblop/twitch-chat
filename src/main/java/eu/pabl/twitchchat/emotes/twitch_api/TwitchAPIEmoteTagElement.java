package eu.pabl.twitchchat.emotes.twitch_api;

import java.util.ArrayList;
import java.util.List;

public record TwitchAPIEmoteTagElement(
  String emoteID,
  int startPosition,
  int endPosition
) implements TwitchAPIObject {

  // As per: https://dev.twitch.tv/docs/irc/tags/#privmsg-tags
  public static List<TwitchAPIEmoteTagElement> fromTagString(String emotesString) {
    if (emotesString.isBlank())
      return new ArrayList<>();

    // Just so this makes sense, the format is "emoteID:emoteStart-emoteEnd,emoteStart2-emoteEnd2".
    ArrayList<TwitchAPIEmoteTagElement> returnList = new ArrayList<>();

    String[] split1 = emotesString.split(":");
    //split1[0]: "emoteID"
    //split1[1]: "emoteStart-emoteEnd,emoteStart2-emoteEnd2"
    String emoteID = split1[0];

    String[] commaSeparated = split1[1].split(",");

    for (String emoteStartsEnds : commaSeparated) {
      String[] split2 = emoteStartsEnds.split("-");
      //split2[0]: "emoteStart". "emoteStart2" (on the second run)
      //split2[1]: "emoteEnd". "emoteEnd2" (on the second run)

      returnList.add(new TwitchAPIEmoteTagElement(
        emoteID,
        Integer.parseInt(split2[0]),
        Integer.parseInt(split2[1]))
      );
    }

    return returnList;
  }
}
