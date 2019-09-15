package to.pabli.mtbridge.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "mtbridge";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return screen -> AutoConfig.getConfigScreen(ModConfig.class, screen).get();
  }
}

