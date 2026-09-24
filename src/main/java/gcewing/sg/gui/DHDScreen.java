package gcewing.sg.gui;

import gcewing.sg.BaseBlockUtils;
import gcewing.sg.BlockPos;
import gcewing.sg.DHDTE;
import gcewing.sg.SGAddressing;
import gcewing.sg.SGBaseTE;
import gcewing.sg.SGChannel;
import gcewing.sg.SGCraft;
import gcewing.sg.SGScreen;
import gcewing.sg.SGState;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class DHDScreen extends SGScreen {
   static final int dhdWidth = 320;
   static final int dhdHeight = 120;
   static final double dhdRadius1 = (double)32.0F;
   static final double dhdRadius2 = (double)88.0F;
   static final double dhdRadius3 = (double)144.0F;
   static final double dhdRadius4 = (double)144.0F;
   World world;
   BlockPos pos;
   int dhdTop;
   int dhdCentreX;
   int dhdCentreY;
   int closingDelay = 0;
   int addressLength = 9;
   DHDTE cte;

   public DHDScreen(EntityPlayer player, World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
      this.cte = this.getControllerTE();
   }

   SGBaseTE getStargateTE() {
      return this.cte != null ? this.cte.getLinkedStargateTE(this.cte.typ) : null;
   }

   DHDTE getControllerTE() {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(this.world, this.pos);
      return te instanceof DHDTE ? (DHDTE)te : null;
   }

   String getEnteredAddress() {
      return this.cte.enteredAddress;
   }

   void setEnteredAddress(String address) {
      this.cte.enteredAddress = address;
      SGChannel.sendEnteredAddressToServer(this.cte, address);
   }

   public void initGui() {
      this.dhdTop = this.height - 120;
      this.dhdCentreX = this.width / 2;
      this.dhdCentreY = this.dhdTop + 60;
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.closingDelay > 0 && --this.closingDelay == 0) {
         this.setEnteredAddress("");
         this.close();
      }

   }

   protected void mousePressed(int x, int y, int mouseButton) {
      if (mouseButton == 0) {
         int i = this.findDHDButton(x, y);
         if (i >= 0) {
            this.dhdButtonPressed(i);
            return;
         }
      }

   }

   void closeAfterDelay(int ticks) {
      this.closingDelay = ticks;
   }

   int findDHDButton(int mx, int my) {
      int x = -(mx - this.dhdCentreX);
      int y = -(my - this.dhdCentreY);
      if (y > 0 && Math.hypot((double)x, (double)y) <= (double)32.0F) {
         return 0;
      } else {
         y = y * 320 / 120;
         double r = Math.hypot((double)x, (double)y);
         if (r > (double)144.0F) {
            return -1;
         } else if (r <= (double)32.0F) {
            return 0;
         } else {
            double a = Math.toDegrees(Math.atan2((double)y, (double)x));
            if (a < (double)0.0F) {
               a += (double)360.0F;
            }

            int i0;
            int nb;
            if (r > (double)88.0F) {
               i0 = 1;
               nb = 26;
            } else {
               i0 = 27;
               nb = 11;
            }

            int i = i0 + (int)Math.floor(a * (double)nb / (double)360.0F);
            return i;
         }
      }
   }

   void dhdButtonPressed(int i) {
      if (i == 0) {
         this.buttonSoundO();
         this.orangeButtonPressed(false);
      } else if (i >= 37) {
         this.buttonSound();
         this.backspace();
      } else {
         this.buttonSound();
         this.enterCharacter(SGBaseTE.symbolToChar(i - 1));
      }

   }

   void buttonSound() {
      EntityPlayer player = this.mc.thePlayer;
      ISound sound = null;
      PositionedSoundRecord var4;
      if (this.cte.typ == 0) {
         byte random = (byte)ThreadLocalRandom.current().nextInt(0, 7);
         var4 = new PositionedSoundRecord(new ResourceLocation("sgcraft:sg_button0" + random), 1.0F, 1.0F, (float)player.posX, (float)player.posY, (float)player.posZ);
      } else {
         var4 = new PositionedSoundRecord(new ResourceLocation("sgcraft:sg_button" + this.cte.typ), 1.0F, 1.0F, (float)player.posX, (float)player.posY, (float)player.posZ);
      }

      this.mc.getSoundHandler().playSound(var4);
   }

   void buttonSoundO() {
      EntityPlayer player = this.mc.thePlayer;
      ISound sound = null;
      ISound var3 = new PositionedSoundRecord(new ResourceLocation("sgcraft:sg_oButton" + this.cte.typ), 1.0F, 1.0F, (float)player.posX, (float)player.posY, (float)player.posZ);
      this.mc.getSoundHandler().playSound(var3);
   }

   public void keyTyped(char c, int key) {
      if (key == 1) {
         this.close();
      } else if (key != 14 && key != 211) {
         if (key != 28 && key != 156) {
            String C = String.valueOf(c).toUpperCase();
            if (SGAddressing.isValidSymbolChar(C)) {
               this.enterCharacter(C.charAt(0));
            }
         } else {
            this.orangeButtonPressed(true);
         }
      } else {
         this.backspace();
      }

   }

   void orangeButtonPressed(boolean connectOnly) {
      SGBaseTE te = this.getStargateTE();
      if (te != null) {
         if (te.state == SGState.Idle) {
            this.sendConnectOrDisconnect(te, this.getEnteredAddress());
         } else if (!connectOnly) {
            this.sendConnectOrDisconnect(te, "");
         }
      }

   }

   void sendConnectOrDisconnect(SGBaseTE te, String address) {
      SGChannel.sendConnectOrDisconnectToServer(te, address);
      this.closeAfterDelay(10);
   }

   void backspace() {
      if (this.stargateIsIdle()) {
         this.buttonSound();
         String a = this.getEnteredAddress();
         int n = a.length();
         if (n > 0) {
            this.setEnteredAddress(a.substring(0, n - 1));
         }
      }

   }

   void enterCharacter(char c) {
      if (this.stargateIsIdle()) {
         this.buttonSound();
         String a = this.getEnteredAddress();
         int n = a.length();
         if (n < this.addressLength) {
            this.setEnteredAddress(a + c);
         }
      }

   }

   boolean stargateIsIdle() {
      SGBaseTE te = this.getStargateTE();
      return te != null && te.state == SGState.Idle;
   }

   protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      SGBaseTE te = this.getStargateTE();
      GL11.glPushAttrib(24576);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3008);
      this.drawBackgroundImage();
      this.drawOrangeButton();
      if (te != null && te.state == SGState.Idle) {
         this.drawEnteredSymbols();
         this.drawEnteredString();
      }

      GL11.glPopAttrib();
   }

   void drawBackgroundImage() {
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_gui" + this.cte.typ + ".png"));
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      this.drawTexturedRect((double)((this.width - 320) / 2), (double)(this.height - 120), (double)320.0F, (double)120.0F);
   }

   void drawOrangeButton() {
      SGBaseTE te = this.getStargateTE();
      boolean connected = te != null && te.isActive();
      if (this.cte.typ == 0) {
         this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre0.png"), 128, 64);
         if (te != null && te.isMerged) {
            if (connected) {
               this.setColor((double)1.0F, (double)0.5F, (double)0.0F);
            } else {
               this.setColor((double)0.5F, (double)0.25F, (double)0.0F);
            }
         } else {
            this.setColor(0.2, 0.2, 0.2);
         }
      } else if (this.cte.typ == 1) {
         this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre1.png"), 128, 64);
         if (te != null && te.isMerged) {
            if (connected) {
               this.setColor((double)0.25F, 0.28, 0.8);
            } else {
               this.setColor(0.13, 0.14, 0.4);
            }
         } else {
            this.setColor(0.3, 0.3, 0.3);
         }
      } else if (this.cte.typ == 2) {
         this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre2.png"), 128, 64);
         if (te != null && te.isMerged) {
            if (connected) {
               this.setColor(0.95, 0.69, 0.36);
            } else {
               this.setColor(0.7, 0.58, 0.3);
            }
         } else {
            this.setColor(0.3, 0.3, 0.3);
         }
      } else if (this.cte.typ == 3) {
         this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre3.png"), 128, 64);
         if (te != null && te.isMerged) {
            if (connected) {
               this.setColor((double)1.0F, (double)1.0F, (double)1.0F);
            } else {
               this.setColor((double)0.5F, (double)0.5F, (double)0.5F);
            }
         } else {
            this.setColor(0.3, 0.3, 0.3);
         }
      } else if (this.cte.typ == 4) {
         this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre4.png"), 128, 64);
         if (te != null && te.isMerged) {
            if (connected) {
               this.setColor(0.29, 0.77, 0.82);
            } else {
               this.setColor(0.09, 0.57, 0.62);
            }
         } else {
            this.setColor(0.3, 0.3, 0.3);
         }
      }

      double rx = (double)30.0F;
      double ry = (double)22.5F;
      this.drawTexturedRect((double)this.dhdCentreX - rx, (double)this.dhdCentreY - ry - (double)6.0F, (double)2.0F * rx, (double)1.5F * ry, (double)64.0F, (double)0.0F, (double)64.0F, (double)48.0F);
      this.resetColor();
      if (connected) {
         GL11.glBlendFunc(1, 1);
         double d = (double)5.0F;
         this.drawTexturedRect((double)this.dhdCentreX - rx - d, (double)this.dhdCentreY - ry - d - (double)6.0F, (double)2.0F * (rx + d), ry + d, (double)0.0F, (double)0.0F, (double)64.0F, (double)32.0F);
         this.drawTexturedRect((double)this.dhdCentreX - rx - d, (double)(this.dhdCentreY - 6), (double)2.0F * (rx + d), (double)0.5F * ry + d, (double)0.0F, (double)32.0F, (double)64.0F, (double)32.0F);
         GL11.glBlendFunc(770, 771);
      }

   }

   void drawEnteredSymbols() {
      this.drawAddressSymbols(this.width / 2, this.dhdTop - 80, this.getEnteredAddress());
   }

   void drawEnteredString() {
      String address = SGAddressing.padAddress(this.getEnteredAddress(), "|", this.addressLength);
      this.drawAddressString(this.width / 2, this.dhdTop - 20, address);
   }
}
