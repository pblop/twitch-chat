package eu.pabl.twitchchat;

import eu.pabl.twitchchat.badge.Badge;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TwitchChatResourceReloadListener implements IdentifiableResourceReloadListener {
  /**
   * @return The unique identifier of this listener.
   */
  @Override
  public Identifier getFabricId() {
    return Identifier.of("twitchchat", "reload");
  }

  /**
   * Performs a reload. Returns a future that is completed when the reload
   * is completed.
   *
   * <p>In a reload, there is a prepare stage and an apply stage. For the
   * prepare stage, you should create completable futures with {@linkplain
   * CompletableFuture#supplyAsync(Supplier, Executor)
   * CompletableFuture.supplyAsync(..., prepareExecutor)}
   * to ensure the prepare actions are done with the prepare executor. Then,
   * you should have a completable future for all the prepared actions, and
   * call {@linkplain CompletableFuture#thenCompose(Function)
   * combinedPrepare.thenCompose(synchronizer::whenPrepared)}
   * to notify the {@code synchronizer}. Finally, you should run {@linkplain
   * CompletableFuture#thenAcceptAsync(Consumer, Executor)
   * CompletableFuture.thenAcceptAsync(..., applyExecutor)} for apply actions.
   * In the end, returns the result of {@code thenAcceptAsync}.
   *
   * @param synchronizer    the synchronizer
   * @param manager         the resource manager
   * @param prepareExecutor the prepare executor
   * @param applyExecutor   the apply executor
   * @return a future for the reload
   * @see ReloadableResourceManagerImpl#reload(Executor, Executor,
   * CompletableFuture, List)
   */
  @Override
  public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

    CompletableFuture<Void> preparedAction = CompletableFuture.supplyAsync(() -> {
      TwitchChatMod.BADGES.clearResourcePackOverrides();
      Badge.loadBadges();
      return null;
    }, prepareExecutor);

    return preparedAction
        .thenCompose(synchronizer::whenPrepared)
        .thenAcceptAsync(void_ -> {}, applyExecutor);
  }
}
