package eu.pabl.twitchchat.emotes;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class CustomImageCache {
  private static CustomImageCache instance;

  private Path cacheFolder;

  CustomImageCache() {
    this.cacheFolder = FabricLoader
      .getInstance()
      .getConfigDir()
      .resolve("twitchchat")
      .resolve("cache");
    this.cacheFolder.toFile().mkdirs();
  }

  public File getFile(String pathStr) {
    Path path = this.cacheFolder.resolve(pathStr);
    path.getParent().toFile().mkdirs();
    return path.toFile();
  }
  public File getPngFile(String customImageId) {
    return this.getFile(customImageId + ".png");
  }

  public CacheEntry[] getAllCachedFiles() throws IOException {
    return Files.walk(this.cacheFolder)
      .filter(p -> p.toFile().isFile() && p.toString().endsWith(".png"))
      .map(p -> new CacheEntry(this.cacheFolder.relativize(p).toString().replace(".png", ""), p))
      .toArray(CacheEntry[]::new);
  }

  public static CustomImageCache getInstance() {
    if (instance == null) {
      instance = new CustomImageCache();
    }
    return instance;
  }

  public record CacheEntry(String id, Path path) { }
}
