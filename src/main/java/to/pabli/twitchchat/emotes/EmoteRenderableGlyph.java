package to.pabli.twitchchat.emotes;

import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

public enum EmoteRenderableGlyph implements RenderableGlyph {
   INSTANCE;

   private static final NativeImage IMAGE = Util.make(new NativeImage(NativeImage.Format.ABGR, 5, 8, false), (nativeImage) -> {
      for(int i = 0; i < 8; ++i) {
         for(int j = 0; j < 5; ++j) {
            boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
               nativeImage.setPixelColor(j, i, bl ? 0xFF3333CC : 0xFFCC3333);
         }
      }

      nativeImage.untrack();
   });

   public int getWidth() {
      return 5;
   }

   public int getHeight() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float getOversample() {
      return 1.0F;
   }

   public void upload(int x, int y) {
      IMAGE.upload(0, x, y, false);
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
