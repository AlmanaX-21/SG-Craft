package gcewing.sg.client.renderer;

import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class WormholeVideo implements Runnable {
   private static final int FRAMES = 130;
   private static final int WIDTH = 960;
   private static final int HEIGHT = 540;
   private static final double FPS = 30000.0 / 1001.0;
   private static int texture = -1;
   private final BlockingQueue<Frame> decoded = new ArrayBlockingQueue<Frame>(3);
   private final Thread decoder = new Thread(this, "SGCraft wormhole video");
   private final ISound sound = PositionedSoundRecord.func_147673_a(new ResourceLocation("sgcraft", "wormhole_transit"));
   private volatile int wanted;
   private boolean ready;

   public static WormholeVideo play() {
      WormholeVideo video = new WormholeVideo();
      video.decoder.setDaemon(true);
      video.decoder.start();
      Minecraft.getMinecraft().getSoundHandler().playSound(video.sound);
      return video;
   }

   public void stop() {
      decoder.interrupt();
      Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
   }

   public boolean bind(double seconds) {
      wanted = Math.min(FRAMES - 1, (int)(seconds * FPS));
      Frame latest = null;
      Frame next;
      while ((next = decoded.peek()) != null && next.index <= wanted) {
         latest = decoded.poll();
      }
      if (texture < 0) {
         texture = TextureUtil.glGenTextures();
         TextureUtil.allocateTexture(texture, WIDTH, HEIGHT);
      }
      if (latest != null) {
         TextureUtil.uploadTexture(texture, latest.pixels, WIDTH, HEIGHT);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
         ready = true;
      }
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      return ready;
   }

   public void run() {
      try {
         for (int i = 0; i < FRAMES; i = Math.max(i + 1, wanted)) {
            decoded.put(new Frame(i, read(i)));
         }
      } catch (InterruptedException e) {
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private static int[] read(int frame) throws IOException {
      ResourceLocation location = new ResourceLocation("sgcraft", String.format("textures/wormhole/%03d.jpg", frame));
      try (InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream()) {
         byte[] bgr = ((DataBufferByte)ImageIO.read(new MemoryCacheImageInputStream(in)).getRaster().getDataBuffer()).getData();
         int[] pixels = new int[WIDTH * HEIGHT];
         for (int p = 0; p < pixels.length; p++) {
            pixels[p] = 0xFF000000 | (bgr[p * 3 + 2] & 0xFF) << 16 | (bgr[p * 3 + 1] & 0xFF) << 8 | bgr[p * 3] & 0xFF;
         }
         return pixels;
      }
   }

   private static class Frame {
      final int index;
      final int[] pixels;

      Frame(int index, int[] pixels) {
         this.index = index;
         this.pixels = pixels;
      }
   }
}
