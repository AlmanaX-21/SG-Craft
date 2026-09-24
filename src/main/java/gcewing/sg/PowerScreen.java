package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class PowerScreen extends BaseGui.Screen {
   static final int guiWidth = 128;
   static final int guiHeight = 64;
   PowerTE te;

   public static PowerScreen create(EntityPlayer player, World world, BlockPos pos) {
      PowerContainer container = PowerContainer.create(player, world, pos);
      return container != null ? new PowerScreen(container) : null;
   }

   public PowerScreen(PowerContainer container) {
      super(container, 128, 64);
      this.te = container.te;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void drawBackgroundLayer() {
      this.bindTexture(SGCraft.mod.resourceLocation("textures/gui/power_gui.png"), 128, 64);
      this.drawTexturedRect((double)0.0F, (double)0.0F, (double)128.0F, (double)64.0F, (double)0.0F, (double)0.0F);
      int cx = this.xSize / 2;
      this.drawCenteredString(this.te.getScreenTitle(), cx, 8);
      this.drawRightAlignedString(this.te.getUnitName(), 72, 28);
      this.drawRightAlignedString(String.format("%.0f", this.te.energyBuffer), 121, 28);
      this.drawRightAlignedString("Max", 72, 42);
      this.drawRightAlignedString(String.format("%.0f", this.te.energyMax), 121, 42);
      this.drawPowerGauge();
   }

   void drawPowerGauge() {
      GL11.glEnable(3042);
      GL11.glBlendFunc(1, 1);
      this.setColor((double)1.0F, (double)0.0F, (double)0.0F);
      this.drawRect((double)19.0F, (double)27.0F, (double)25.0F * this.te.energyBuffer / this.te.energyMax, (double)10.0F);
   }
}
