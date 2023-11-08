package eu.pabl.twitchchat.emotes;

import eu.pabl.twitchchat.TwitchChatMod;

public interface FailingRunnable {
  // This is a runnable interface that allows exceptions, they will be handled whenever this
  // custom runnable is called.
  public abstract void run() throws Exception;

  public default Runnable toRunnable() {
    return () -> {
      try {
        this.run();
      } catch (Exception e) {
        TwitchChatMod.LOGGER.error(e.toString());
      }
    };
  }
}
