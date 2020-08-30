package to.pabli.twitchchat.emotes;

import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EmoteRenderableGlyph implements RenderableGlyph {
   private final NativeImage image;
   private final int width;
   private final int height;
   // TODO: Calculate oversample dynamically to fit.
   private final float oversample = 3F;

   public EmoteRenderableGlyph(Emote emote) throws IOException {
      BufferedImage image = ImageIO.read(emote.getLocalEmoteImage());

      this.width = image.getWidth();
      this.height = image.getHeight();

      this.image = Util.make(new NativeImage(NativeImage.Format.ABGR, width, height, false), (nativeImage) -> {
         for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
               nativeImage.setPixelColor(x, y, image.getRGB(x, y));
            }
         }
         nativeImage.untrack();
      });
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public float getAdvance() {
      return (float)width / oversample + 1;
   }

   public float getOversample() {
      return oversample;
   }

   public void upload(int x, int y) {
      this.image.upload(0, x, y, false);
   }

   public boolean hasColor() {
      return true;
   }

   public Glyph getGlyph() {
      return new Glyph() {
         @Override
         public float getAdvance() {
            return EmoteRenderableGlyph.this.getAdvance();
         }

         @Override
         public float getAdvance(boolean bold) {
            return this.getAdvance();
         }

         @Override
         public float getBearingX() {
            return this.getAdvance();
         }

         @Override
         public float getBoldOffset() {
            return this.getAdvance();
         }

         @Override
         public float getShadowOffset() {
            return 1F;
         }
      };
   }
}
