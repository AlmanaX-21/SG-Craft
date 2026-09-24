package gcewing.sg;

import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

public class SGScreen extends BaseGui.Screen {
   static final String symbolTextureFile = "symbols48.png";
   static final int symbolsPerRowInTexture = 10;
   static final int symbolWidthInTexture = 48;
   static final int symbolHeightInTexture = 48;
   static final int symbolTextureWidth = 512;
   static final int symbolTextureHeight = 256;
   static final int frameWidth = 236;
   static final int frameHeight = 44;
   static final int borderSize = 6;
   static final int cellSize = 24;
   double uscale;
   double vscale;
   float red = 1.0F;
   float green = 1.0F;
   float blue = 1.0F;

   public SGScreen() {
      super(new BaseContainer(0, 0));
   }

   public SGScreen(Container container, int width, int height) {
      super(container, width, height);
   }

   protected void drawAddressSymbols(int x, int y, String address) {
      int x0 = x - address.length() * 24 / 2;
      int y0 = y + 22 - 12;
      this.bindSGTexture("symbols48.png", 256, 128);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      int n = address.length();

      for(int i = 0; i < n; ++i) {
         int s = SGBaseTE.charToSymbol(address.charAt(i));
         int row = s / 10;
         int col = s % 10;
         this.drawTexturedRect((double)(x0 + i * 24), (double)y0, (double)24.0F, (double)24.0F, (double)(col * 24), (double)(row * 24));
      }

   }

   protected void drawAddressString(int x, int y, String address) {
      this.drawCenteredString(this.fontRendererObj, address, x, y, 16777215);
   }

   void bindSGTexture(String name) {
      this.bindSGTexture(name, 1, 1);
   }

   void bindSGTexture(String name, int usize, int vsize) {
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/" + name), usize, vsize);
   }
}
