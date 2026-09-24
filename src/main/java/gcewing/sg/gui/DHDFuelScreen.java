package gcewing.sg.gui;

import gcewing.sg.BlockPos;
import gcewing.sg.DHDTE;
import gcewing.sg.SGCraft;
import gcewing.sg.SGScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class DHDFuelScreen extends SGScreen {
   static String screenTitle = "Stargate Controller";
   static final int guiWidth = 256;
   static final int guiHeight = 208;
   static final int fuelGaugeWidth = 16;
   static final int fuelGaugeHeight = 34;
   static final int fuelGaugeX = 214;
   static final int fuelGaugeY = 84;
   static final int fuelGaugeU = 0;
   static final int fuelGaugeV = 208;
   DHDTE te;

   public static DHDFuelScreen create(EntityPlayer player, World world, BlockPos pos) {
      DHDTE te = DHDTE.at(world, (BlockPos)pos);
      return te != null ? new DHDFuelScreen(player, te) : null;
   }

   public DHDFuelScreen(EntityPlayer player, DHDTE te) {
      super(new DHDFuelContainer(player, te), 256, 208);
      this.te = te;
   }

   protected void drawBackgroundLayer() {
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_fuel_gui.png"), 256, 256);
      this.drawTexturedRect((double)0.0F, (double)0.0F, (double)256.0F, (double)208.0F, (double)0.0F, (double)0.0F);
      this.drawFuelGauge();
      System.out.println(this.xSize);
      int cx = this.xSize / 2;
      this.setTextColor(19558);
      this.drawCenteredString(screenTitle, cx, 8);
      double var10000 = (double)34.0F * this.te.getEnergyInBuffer();
      DHDTE var10001 = this.te;
      int level = (int)(var10000 / DHDTE.maxEnergyBuffer);
      DHDTE var3 = this.te;
      if (4 > 0) {
         this.drawString("Fuel", 150, 96);
      }

   }

   void drawFuelGauge() {
      double var10000 = (double)34.0F * this.te.getEnergyInBuffer();
      DHDTE var10001 = this.te;
      int level = (int)(var10000 / DHDTE.maxEnergyBuffer);
      System.out.println(level);
      if (level > 34) {
         level = 34;
      }

      GL11.glEnable(3042);
      this.drawTexturedRect((double)214.0F, (double)(118 - level), (double)16.0F, (double)level, (double)0.0F, (double)208.0F);
      GL11.glDisable(3042);
   }
}
