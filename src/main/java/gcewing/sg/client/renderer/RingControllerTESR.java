package gcewing.sg.client.renderer;

import gcewing.sg.BaseBlockUtils;
import gcewing.sg.BaseOrientation;
import gcewing.sg.IBlockState;
import gcewing.sg.SGCraft;
import gcewing.sg.te.RingControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RingControllerTESR extends TileEntitySpecialRenderer {
   protected static final IModelCustom plateModel;
   protected static final IModelCustom buttonModel;

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      RingControllerTileEntity ctrl = (RingControllerTileEntity)tile;
      GL11.glPushMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(32826);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslated(x + (double)0.5F, y, z + (double)0.5F);
      GL11.glRotatef(this.rotateByFace(BaseBlockUtils.getWorldBlockState(tile.getWorldObj(), ctrl.getPos())), 0.0F, 1.0F, 0.0F);
      GL11.glTranslated((double)0.0F, (double)0.5F, 0.47);
      GL11.glScalef(0.6F, 0.6F, 0.6F);
      GL11.glEnable(2929);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/goauld_panel.jpg"));
      float time = (float)Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
      plateModel.renderAll();
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/goauld_buttons.jpg"));
      buttonModel.renderAll();
      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }

   protected float rotateByFace(IBlockState state) {
      EnumFacing f = (EnumFacing)state.getValue(BaseOrientation.Orient4WaysByState.FACING);
      switch (f) {
         case NORTH:
            return 180.0F;
         case WEST:
            return 270.0F;
         case SOUTH:
            return 0.0F;
         case EAST:
            return 90.0F;
         default:
            return 0.0F;
      }
   }

   static {
      plateModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/plate_goauld.obj"));
      buttonModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/buttons_goauld.obj"));
   }
}
