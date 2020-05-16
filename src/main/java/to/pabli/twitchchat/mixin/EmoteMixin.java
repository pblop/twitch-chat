package to.pabli.twitchchat.mixin;

import static net.minecraft.client.gui.DrawableHelper.fill;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.time.StopWatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to.pabli.twitchchat.TwitchChatMod;

// Ok, this code doesn't work.
// The emote detection and the part where it checks where the emotes should be placed and places
// them works.
// The part where I have to get the actual Glyph and GlyphRenderer don't work. I don't know how
// to create a custom Glyph and GlyphRenderer. I just have no idea.
// I know that I have to get the Glyphs from the Twitch API and format them some way. I could be
// able to do that. But I have ABSOLUTELY no idea what to do with the glyphs.
@Mixin(TextRenderer.class)
public class EmoteMixin {
  @Final
  @Shadow
  private FontStorage fontStorage;

  @Shadow
  private void drawGlyph(GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) { }

  @Inject(at = @At("HEAD"), method = "drawLayer", cancellable = true)
  private void drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light, CallbackInfoReturnable<Float> info) {
    float f = shadow ? 0.25F : 1.0F;
    float g = (float)(color >> 16 & 255) / 255.0F * f;
    float h = (float)(color >> 8 & 255) / 255.0F * f;
    float i = (float)(color & 255) / 255.0F * f;
    float j = x;
    float k = g;
    float l = h;
    float m = i;
    float n = (float)(color >> 24 & 255) / 255.0F;
    boolean bl = false;
    boolean bl2 = false;
    boolean bl3 = false;
    boolean bl4 = false;
    boolean bl5 = false;
    List<GlyphRenderer.Rectangle> list = Lists.newArrayList();

    // Work with the text and somehow signal the code below where is the glyph located without doing significant changes.

    // This map stores the custom glyphs that are in the text by their position in it.
    HashMap<Integer, RenderableGlyph> glyphPositionMap = new HashMap<>();

    // Make it a HashSet because whatever Set 'keySet()' is using is just tooooo sloooow.
    Set<String> emoteNames = new HashSet<>(TwitchChatMod.twitchEmotes.keySet());

    // Go through every word and check if it's an emote.
    for (String word : text.split("\\s+")) {
      if (emoteNames.contains(word)) {
        // If it is an emote, note its position on the text and its glyph
        // and replace it with some random character.

        // "If it is an emote, note its position on the text and its glyph"
        int position = text.indexOf(word);
        // this should be RenderableGlyph emoteGlyph = TwitchChatMod.twitchEmotes.get(position);
        // but I'm too lazy to expose this.fontStorage.getGlyph to the TwitchChatMod class,
        // so I'm passing the character instead of the Glyph and getting the Glyph now.
        // Note: this whole thing is just for testing
        RenderableGlyph emoteGlyph = this.fontStorage.getRenderableGlyphPublic(TwitchChatMod.twitchEmotes.get(word));
        glyphPositionMap.put(position, emoteGlyph);



        // "and replace it with some random character."
        text = text.replaceFirst(word, "a");
      }
    }

    for(int o = 0; o < text.length(); ++o) {
      // o is the position of the current char in the text.

      RenderableGlyph emoteGlyph = glyphPositionMap.getOrDefault(o, null);

      char c = text.charAt(o);
      // I've added '&& emoteGlyph == null' to the pre-existing check so that if the 'emoteGlyph' is
      // non-null, the code just goes straight to the else, because we don't want any formatting to
      // change. This shouldn't be necessary though, because the char will always be 'a', thus
      // 'c == 167' will always be false meaning 'c == 167 && o + 1 < text.length()' will also
      // always be false.
      if ((c == 167 && o + 1 < text.length()) && emoteGlyph == null) {
        Formatting formatting = Formatting.byCode(text.charAt(o + 1));
        if (formatting != null) {
          if (formatting.affectsGlyphWidth()) {
            bl = false;
            bl2 = false;
            bl5 = false;
            bl4 = false;
            bl3 = false;
            k = g;
            l = h;
            m = i;
          }

          if (formatting.getColorValue() != null) {
            int p = formatting.getColorValue();
            k = (float)(p >> 16 & 255) / 255.0F * f;
            l = (float)(p >> 8 & 255) / 255.0F * f;
            m = (float)(p & 255) / 255.0F * f;
          } else if (formatting == Formatting.OBFUSCATED) {
            bl = true;
          } else if (formatting == Formatting.BOLD) {
            bl2 = true;
          } else if (formatting == Formatting.STRIKETHROUGH) {
            bl5 = true;
          } else if (formatting == Formatting.UNDERLINE) {
            bl4 = true;
          } else if (formatting == Formatting.ITALIC) {
            bl3 = true;
          }
        }

        ++o;
      } else {

        Glyph glyph;
        GlyphRenderer glyphRenderer;
        if (emoteGlyph != null) {
          // if 'emoteGlyph' is non-null (which means the current character should be an emote) use
          // that glyph


          glyph = emoteGlyph;
          glyphRenderer = this.fontStorage.getGlyphRendererPublic(emoteGlyph);
        } else {
          // if the current character is not an emote, just don't use it
          glyph = this.fontStorage.getGlyph(c);

          glyphRenderer = bl && c != ' ' ? this.fontStorage.getObfuscatedGlyphRenderer(glyph) : this.fontStorage.getGlyphRenderer(c);
        }

        float s;
        float t;
        if (!(glyphRenderer instanceof EmptyGlyphRenderer)) {
          s = bl2 ? glyph.getBoldOffset() : 0.0F;
          t = shadow ? glyph.getShadowOffset() : 0.0F;
          VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(glyphRenderer.method_24045(seeThrough));
          this.drawGlyph(glyphRenderer, bl2, bl3, s, j + t, y + t, matrix, vertexConsumer, k, l, m, n, light);
        }

        s = glyph.getAdvance(bl2);
        t = shadow ? 1.0F : 0.0F;
        if (bl5) {
          list.add(new GlyphRenderer.Rectangle(j + t - 1.0F, y + t + 4.5F, j + t + s, y + t + 4.5F - 1.0F, -0.01F, k, l, m, n));
        }

        if (bl4) {
          list.add(new GlyphRenderer.Rectangle(j + t - 1.0F, y + t + 9.0F, j + t + s, y + t + 9.0F - 1.0F, -0.01F, k, l, m, n));
        }

        j += s;
      }
    }

    if (underlineColor != 0) {
      float u = (float)(underlineColor >> 24 & 255) / 255.0F;
      float v = (float)(underlineColor >> 16 & 255) / 255.0F;
      float w = (float)(underlineColor >> 8 & 255) / 255.0F;
      float z = (float)(underlineColor & 255) / 255.0F;
      list.add(new GlyphRenderer.Rectangle(x - 1.0F, y + 9.0F, j + 1.0F, y - 1.0F, 0.01F, v, w, z, u));
    }

    if (!list.isEmpty()) {
      GlyphRenderer glyphRenderer2 = this.fontStorage.getRectangleRenderer();
      VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(glyphRenderer2.method_24045(seeThrough));
      Iterator var39 = list.iterator();

      while(var39.hasNext()) {
        GlyphRenderer.Rectangle rectangle = (GlyphRenderer.Rectangle)var39.next();
        glyphRenderer2.drawRectangle(rectangle, matrix, vertexConsumer2, light);
      }
    }

    info.setReturnValue(j);
    return;
  }
}
