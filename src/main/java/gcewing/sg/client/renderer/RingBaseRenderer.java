package gcewing.sg.client.renderer;

import gcewing.sg.BaseGLUtils;
import gcewing.sg.SGCraft;
import gcewing.sg.Vector3;
import gcewing.sg.te.RingTileEntity;
import gcewing.sg.te.RingTileEntityFlat;
import gcewing.sg.te.RingTileEntityUp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RingBaseRenderer extends TileEntitySpecialRenderer {
   protected static final IModelCustom ringModel;
   protected static final IModelCustom ringModelFlat;
   protected static final IModelCustom ringSchwingModel;

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      RingTileEntity ring = (RingTileEntity)tile;
      if (ring.shouldStartAnimation) {
         ring.animationStartTime = (float)Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks - (float)ring.getTickTime();
         ring.shouldStartAnimation = false;
      }

      GL11.glPushMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(32826);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslated(x + (double)0.5F, y + (double)0.3F, z + (double)0.5F);
      GL11.glScalef(0.6F, 0.6F, 0.6F);
      GL11.glEnable(2929);
      BaseGLUtils.glMultMatrix(ring.localToGlobalTransformation(Vector3.zero));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (ring instanceof RingTileEntityFlat) {
         this.renderFloorRingFlat();
      } else if (!(ring instanceof RingTileEntityUp)) {
         this.renderFloorRing();
      }

      if (ring.getTickTime() > -1) {
         float time = (float)Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;

         for(int i = 0; i < 5; ++i) {
            this.renderRing(ring, i, time);
         }

         this.renderRingSchwing(ring, time);
      }

      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }

   private void renderFloorRing() {
      GL11.glPushMatrix();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/rings_black.jpg"));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(0.0F, 2.8F, 0.0F);
      ringModel.renderAll();
      GL11.glPopMatrix();
   }

   private void renderFloorRingFlat() {
      GL11.glPushMatrix();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/rings_flat.png"));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(0.0F, 2.85F, 0.0F);
      ringModelFlat.renderAll();
      GL11.glPopMatrix();
   }

   private void renderRing(RingTileEntity ring, int i, float time) {
      GL11.glPushMatrix();
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/rings_black.jpg"));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (ring instanceof RingTileEntityUp) {
         this.translateRingFromUp((RingTileEntityUp)ring, i, time);
      } else {
         this.translateRingFromDown(ring, i, time);
      }

      ringModel.renderAll();
      GL11.glPopMatrix();
   }

   private void translateRingFromDown(RingTileEntity ring, int i, float time) {
      if (ring.getTickTime() < 75) {
         GL11.glTranslated((double)0.0F, (double)this.getInterpolatedProgressRings(time - ring.animationStartTime, (float)(i * 6), 25.0F) * (double)7.5F - (double)((float)i * 1.0F), (double)0.0F);
      } else {
         GL11.glTranslated((double)0.0F, (double)(1.0F - this.getInterpolatedProgressRings(time - ring.animationStartTime - 75.0F, (float)((4 - i) * 6), 25.0F)) * (double)7.5F - (double)((float)i * 1.0F), (double)0.0F);
      }

   }

   private void translateRingFromUp(RingTileEntityUp ring, int i, float time) {
      if (ring.getTickTime() < 75) {
         GL11.glTranslated((double)0.0F, (double)(this.getInterpolatedProgressRings(time - ring.animationStartTime, (float)(i * 6), 25.0F) * (float)ring.getGroundDistance() * -1.66F + (float)i * 1.0F - 0.6F), (double)0.0F);
      } else {
         GL11.glTranslated((double)0.0F, (double)((1.0F - this.getInterpolatedProgressRings(time - ring.animationStartTime - 75.0F, (float)((4 - i) * 6), 25.0F)) * (float)ring.getGroundDistance() * -1.66F + (float)i * 1.0F - 0.6F), (double)0.0F);
      }

   }

   private void renderRingSchwing(RingTileEntity ring, float time) {
      GL11.glPushMatrix();
      GL11.glColor4f(255.0F, 255.0F, 255.0F, 1.0F);
      if (ring instanceof RingTileEntityUp) {
         GL11.glTranslated((double)0.0F, (double)((float)((RingTileEntityUp)ring).getGroundDistance() * -1.66F + 4.0F - 0.6F) - (double)this.getInterpolatedProgressSchwingPosition(time - ring.animationStartTime - 55.0F, 20.0F) * (double)7.5F, (double)0.0F);
      } else {
         GL11.glTranslated((double)0.0F, (double)7.5F - (double)this.getInterpolatedProgressSchwingPosition(time - ring.animationStartTime - 55.0F, 20.0F) * (double)7.5F, (double)0.0F);
      }

      GL11.glScalef(1.0F, this.getInterpolatedProgressSchwingHeight(time - ring.animationStartTime - 55.0F, 20.0F) * 2.0F, 1.0F);
      GL11.glDisable(2896);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/ringSchwing.png"));
      ringSchwingModel.renderAll();
      GL11.glEnable(2896);
      GL11.glPopMatrix();
   }

   private float getInterpolatedProgressRings(float timeSinceStart, float offset, float duration) {
      float x = Math.max(Math.min((timeSinceStart - offset) / duration, 1.0F), 0.0F);
      float c1 = 0.6F;
      float c3 = c1 + 1.0F;
      return (float)((double)1.0F + (double)c3 * Math.pow((double)(x - 1.0F), (double)3.0F) + (double)c1 * Math.pow((double)(x - 1.0F), (double)2.0F));
   }

   private float getInterpolatedProgressSchwingHeight(float timeSinceStart, float duration) {
      float x = Math.max(Math.min(timeSinceStart / duration, 1.0F), 0.0F);
      return (float)((double)-16.0F * Math.pow((double)x - (double)0.5F, (double)4.0F) + (double)1.0F);
   }

   private float getInterpolatedProgressSchwingPosition(float timeSinceStart, float duration) {
      float x = Math.max(Math.min(timeSinceStart / duration, 1.0F), 0.0F);
      return x;
   }

   static {
      ringModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/transportRings.obj"));
      ringModelFlat = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/transportRingsFlat.obj"));
      ringSchwingModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/ringSchwing.obj"));
   }
}
