package gcewing.sg;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class SGBaseScreen extends SGScreen {
   static String screenTitle = "Stargate Address";
   static final int guiWidth = 256;
   static final int guiHeight = 256;
   int dhdTop;
   int dhdCentreX;
   int dhdCentreY;
   static final int fuelGaugeWidth = 16;
   static final int fuelGaugeHeight = 34;
   static final int fuelGaugeX = 214;
   static final int fuelGaugeY = 84;
   static final int fuelGaugeU = 0;
   static final int fuelGaugeV = 208;
   SGBaseTE te;
   String address;
   String formattedAddress;
   boolean addressValid;

   public static SGBaseScreen create(EntityPlayer player, World world, BlockPos pos) {
      SGBaseTE te = SGBaseTE.at(world, (BlockPos)pos);
      return te != null ? new SGBaseScreen(player, te) : null;
   }

   public SGBaseScreen(EntityPlayer player, SGBaseTE te) {
      super(new SGBaseContainer(player, te), 256, 256);
      this.te = te;
      this.getAddress();
      if (this.addressValid) {
         setClipboardString(this.formattedAddress);
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   void sendQuickDial(SGBaseTE te, boolean qd) {
      SGChannel.sendQuickDial(te, qd);
   }

   protected void mousePressed(int x, int y, int mouseButton) {
      if (mouseButton == 0) {
         int i = this.findQDButton(x, y);
         if (i == 1) {
            if (this.te.state.equals(SGState.Idle)) {
               this.buttonSound();
               if (this.te.qd) {
                  this.te.qd = false;
                  this.sendQuickDial(this.te, false);
               } else {
                  this.te.qd = true;
                  this.sendQuickDial(this.te, true);
               }
            }

            return;
         }
      }

   }

   void buttonSound() {
      EntityPlayer player = this.mc.thePlayer;
      ISound sound = null;
      PositionedSoundRecord var4;
      if (this.te.getType() == 0) {
         byte random = (byte)ThreadLocalRandom.current().nextInt(0, 7);
         var4 = new PositionedSoundRecord(new ResourceLocation("sgcraft:sg_button0" + random), 1.0F, 1.0F, (float)player.posX, (float)player.posY, (float)player.posZ);
      } else {
         var4 = new PositionedSoundRecord(new ResourceLocation("sgcraft:sg_button" + this.te.getType()), 1.0F, 1.0F, (float)player.posX, (float)player.posY, (float)player.posZ);
      }

      this.mc.getSoundHandler().playSound(var4);
   }

   int findQDButton(int mx, int my) {
      return mx >= 212 && mx <= 226 && my >= 90 && my <= 105 ? 1 : 0;
   }

   protected void drawBackgroundLayer() {
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/sg_gui.png"), 256, 256);
      this.drawTexturedRect((double)0.0F, (double)0.0F, (double)256.0F, (double)256.0F, (double)0.0F, (double)0.0F);
      int cx = this.xSize / 2;
      if (this.addressValid) {
         this.drawAddressSymbols(cx, 8, this.address);
      }

      this.setTextColor(19558);
      String barrier = this.te.barrierKind == BarrierKind.IRIS ? " - Iris" : this.te.barrierKind == BarrierKind.SHIELD ? " - Shield" : "";
      this.drawCenteredString(screenTitle + barrier, cx, 5);
      this.drawCenteredString(this.formattedAddress, 194, 46);
      SGBaseTE var10000 = this.te;
      if (24 > 0) {
         this.drawCenteredString("Base Camouflage", 93, 46);
      }

      this.drawCenteredString("Quickdial", 220, 80);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3008);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_centre88.png"), 32, 16);
      this.setColor(0.78, 0.59, 0.6);
      boolean active = true;
      if (this.te.qd) {
         this.setColor(0.034, 0.82, 0.23);
      }

      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      this.drawTexturedRect((double)212.0F, (double)90.0F, (double)16.0F, (double)16.0F, (double)0.0F, (double)0.0F);
      if (this.te.qd) {
         GL11.glBlendFunc(1, 1);
         this.drawTexturedRect((double)212.0F, (double)90.0F, (double)16.0F, (double)16.0F, (double)16.0F, (double)16.0F);
         GL11.glBlendFunc(770, 771);
      }

   }

   void getAddress() {
      if (this.te.homeAddress != null) {
         this.address = this.te.homeAddress;
         this.formattedAddress = SGAddressing.formatAddress(this.address, "-", "-");
         this.addressValid = true;
      } else {
         this.address = "";
         this.formattedAddress = this.te.addressError;
         this.addressValid = false;
      }

   }
}
