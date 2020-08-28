package to.pabli.twitchchat.emotes;

import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

public enum EmoteGlyph implements RenderableGlyph {
   INSTANCE;

   private static final NativeImage IMAGE = (NativeImage)Util.make(new NativeImage(NativeImage.Format.ABGR, 5, 8, false), (nativeImage) -> {
      for(int i = 0; i < 8; ++i) {
         for(int j = 0; j < 5; ++j) {
            boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
            nativeImage.setPixelColor(j, i, bl ? -1 : -1);
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
}
