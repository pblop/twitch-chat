package to.pabli.twitchchat.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import java.util.function.Function;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "twitchchat";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return (prevScreen) -> {
      ConfigBuilder builder = ConfigBuilder.create();
      builder.setTitle("text.twitchchat.title");

      ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

      ConfigCategory defaultCategory = builder.getOrCreateCategory("text.twitchchat.category.default");
      defaultCategory.addEntry(entryBuilder.startStrField("text.twitchchat.default.channel", "").build());
      defaultCategory.addEntry(entryBuilder.startStrField("text.twitchchat.default.username", "").build());
      defaultCategory.addEntry(entryBuilder.startStrField("text.twitchchat.default.oauthKey", "").build());
      defaultCategory.addEntry(entryBuilder.startStrField("text.twitchchat.default.prefix", "").build());

      return builder.build();
    };
  }
}

