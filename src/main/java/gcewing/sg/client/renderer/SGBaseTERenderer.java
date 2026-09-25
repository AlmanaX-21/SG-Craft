package gcewing.sg.client.renderer;

import gcewing.sg.BaseGLUtils;
import gcewing.sg.BaseTileEntityRenderer;
import gcewing.sg.BarrierKind;
import gcewing.sg.SGBaseBlock0Big;
import gcewing.sg.SGBaseBlock1Big;
import gcewing.sg.SGBaseBlock2Big;
import gcewing.sg.SGBaseBlock3Big;
import gcewing.sg.SGBaseBlock4Big;
import gcewing.sg.SGBaseTE;
import gcewing.sg.SGCraft;
import gcewing.sg.SGState;
import gcewing.sg.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class SGBaseTERenderer extends BaseTileEntityRenderer {
   protected static final int numRingSegments = 32;
   protected static final double ringInnerRadius = (double)2.0F;
   protected static final int textureTilesWide = 32;
   protected static final int textureTilesHigh = 2;
   protected static final double textureScaleU = (double)0.001953125F;
   protected static final double textureScaleV = (double)0.03125F;
   protected static final double numIrisBlades = (double)12.0F;
   protected static final int chevronLockAnimationDuration = 15;
   protected static final int chevronGlowDuration = 10;
   protected static final int irisHitImpactTime = 6;
   protected static final IModelCustom irisModel;
   protected static final IModelCustom gateModel;
   protected static final IModelCustom ringModel;
   protected static final IModelCustom ringModelOri;
   protected static final IModelCustom atlantisRingModel;
   protected static final IModelCustom chevronFrameModel;
   protected static final IModelCustom chevronBackModel;
   protected static final IModelCustom chevronLightModel;
   protected static final IModelCustom chevronMovingModel;
   protected static final IModelCustom universeGateModel;
   protected static final IModelCustom universeChevronModel;
   protected static IModelCustom[] universeSymbols;
   protected static final IModelCustom tollanGateModel;
   protected static final IModelCustom tollanChevronFrameModel;
   protected static final IModelCustom tollanChevronLightModel;
   protected static final IModelCustom tollanChevronSecondaryLightModel;
   protected static final IModelCustom tollanChevronMovingModel;
   protected static final ResourceLocation[] ehTexture;
   protected static final ResourceLocation[] ehTextureKawoosh;
   double u0;
   double v0;

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t, int destroyStage) {
      SGBaseTE tesg = (SGBaseTE)te;
      float time = (float)Minecraft.getMinecraft().thePlayer.ticksExisted + t;
      if (tesg.isMerged) {
         GL11.glPushMatrix();
         GL11.glDepthMask(true);
         if (SGBaseTE.transparency) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
         } else {
            GL11.glDisable(3042);
         }

         GL11.glEnable(32826);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Vec3 center = tesg.localToGlobalTransformation().p(0, 2, 0).toVec3();
         GL11.glTranslated(x + center.xCoord - tesg.xCoord, y + center.yCoord - tesg.yCoord, z + center.zCoord - tesg.zCoord);
         GL11.glEnable(2929);
         GL11.glPushMatrix();
         applyTypeSizes(tesg);
         switch (tesg.getType()) {
            case 0:
               this.renderStargateMilkyway(tesg, t, time);
               break;
            case 1:
               this.renderStargateAtlantis(tesg, t, time);
               break;
            case 2:
               this.renderStargateTollan(tesg, t, time);
               break;
            case 3:
               this.renderStargateDestiny(tesg, t, time);
               break;
            case 4:
               this.renderStargateOri(tesg, t, time);
               break;
            default:
               this.renderStargateMilkyway(tesg, t, time);
         }

         GL11.glPopMatrix();
         GL11.glDisable(32826);
         GL11.glPopMatrix();
      }

   }

   protected static void applyTypeSizes(SGBaseTE te) {
      double offset;
      float scale;
      if (te.getBlock() instanceof SGBaseBlock0Big) {
         offset = 0.7;
         scale = 1.6F;
      } else if (te.getBlock() instanceof SGBaseBlock1Big) {
         offset = 0.7;
         scale = 1.6F;
      } else if (te.getBlock() instanceof SGBaseBlock2Big) {
         offset = 0.65;
         scale = 1.45F;
      } else if (te.getBlock() instanceof SGBaseBlock3Big) {
         offset = 0.6;
         scale = 1.4F;
      } else if (te.getBlock() instanceof SGBaseBlock4Big) {
         offset = 0.7;
         scale = 1.6F;
      } else {
         return;
      }
      Vec3 shift = te.localToGlobalTransformation(Vector3.zero).v(0, offset, 0).toVec3();
      GL11.glTranslated(shift.xCoord, shift.yCoord, shift.zCoord);
      GL11.glScalef(scale, scale, scale);
   }

   protected void renderStargateOri(SGBaseTE te, float t, float time) {
      BaseGLUtils.glMultMatrix(te.localToGlobalTransformation(Vector3.zero));
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate" + te.getType() + ".png"));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (te.isConnected()) {
         this.renderEventHorizon(te, time, t);
      }

      if (te.hasIrisUpgrade) {
         this.renderBarrier(te, (double)t, time);
      }

      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/gatering7_ori.jpg"));
      GL11.glScalef(0.53F, 0.53F, 0.53F);
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.15);
      gateModel.renderAll();
      GL11.glPushMatrix();
      GL11.glRotatef((float)te.interpolatedRingAngle((double)t), 0.0F, 0.0F, 1.0F);
      ringModelOri.renderAll();
      GL11.glPopMatrix();
      this.renderChevrons(te, time);
   }

   protected void renderStargateAtlantis(SGBaseTE te, float t, float time) {
      BaseGLUtils.glMultMatrix(te.localToGlobalTransformation(Vector3.zero));
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate" + te.getType() + ".png"));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (te.isConnected()) {
         this.renderEventHorizon(te, time, t);
      }

      if (te.hasIrisUpgrade) {
         this.renderBarrier(te, (double)t, time);
      }

      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/gatering7.jpg"));
      GL11.glScalef(0.53F, 0.53F, 0.53F);
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.15);
      gateModel.renderAll();
      GL11.glPushMatrix();
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/gatering7_atlantis.jpg"));
      atlantisRingModel.renderAll();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      this.renderChevrons(te, time);
      GL11.glPopMatrix();
      this.renderAtlantisSymbols(te, t, time);
   }

   protected void renderAtlantisSymbols(SGBaseTE te, float t, float time) {
      GL11.glPushMatrix();
      GL11.glDisable(2896);
      setLightingDisabled(true);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/symbols.png"));
      if (te.state != SGState.Idle && te.state != SGState.Connected) {
         if (te.state != SGState.Connected) {
            int[] sequence = SGBaseTE.chevronSequence(te.diallingChevronCount);

            for(int i = 0; i < 9; ++i) {
               int j = sequence[i];
               boolean engaged = te.chevronIsEngaged(j);
               if (engaged) {
                  this.renderAtlantisSymbol(te, time, time, (float)(-i) * 40.0F, (float)"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(te.dialledAddress.substring(j, j + 1)), 1.0F);
               }
            }

            float angle = (float)Math.round(te.interpolatedRingAngle((double)t)) - (float)Math.round(te.interpolatedRingAngle((double)t)) % 10.0F;
            int i = (int)(Math.abs(angle) / 40.0F);
            if (i == 9) {
               i = 0;
            }

             if ((angle % 40.0F != 0.0F || angle % 40.0F == 0.0F && !te.chevronIsEngaged(sequence[i])) && te.numEngagedChevrons < te.diallingChevronCount) {
               this.renderAtlantisSymbol(te, t, time, angle, (float)"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(te.dialledAddress.substring(te.numEngagedChevrons, te.numEngagedChevrons + 1)), 1.0F);
            }
         }
      } else {
         for(int i = 0; i < 36; ++i) {
            this.renderAtlantisSymbol(te, time, time, (float)i * 10.0F, (float)i, 1.0F);
         }
      }

      GL11.glEnable(2896);
      setLightingDisabled(false);
      GL11.glPopMatrix();
   }

   protected void renderAtlantisSymbol(SGBaseTE te, float t, float time, float angle, float symbol, float alpha) {
      GL11.glPushMatrix();
      GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(0.4F, 0.4F, 0.4F);
      GL11.glTranslatef(0.0F, 10.7F, 0.0F);
      GL11.glColor4f(0.29F, 0.7F, 1.0F, alpha);
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      GL11.glBegin(7);
      this.vertex3f(-0.5F, -0.5F, 0.5F, symbol / 36.0F, 1.0F);
      this.vertex3f(0.5F, -0.5F, 0.5F, (symbol + 1.0F) / 36.0F, 1.0F);
      this.vertex3f(0.5F, 0.5F, 0.5F, (symbol + 1.0F) / 36.0F, 0.0F);
      this.vertex3f(-0.5F, 0.5F, 0.5F, symbol / 36.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   protected void renderStargateMilkyway(SGBaseTE te, float t, float time) {
      BaseGLUtils.glMultMatrix(te.localToGlobalTransformation(Vector3.zero));
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate" + te.getType() + ".png"));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (te.isConnected()) {
         this.renderEventHorizon(te, time, t);
      }

      if (te.hasIrisUpgrade) {
         this.renderBarrier(te, (double)t, time);
      }

      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/gatering7.jpg"));
      GL11.glScalef(0.53F, 0.53F, 0.53F);
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.15);
      gateModel.renderAll();
      GL11.glPushMatrix();
      GL11.glRotatef((float)te.interpolatedRingAngle((double)t), 0.0F, 0.0F, 1.0F);
      ringModel.renderAll();
      GL11.glPopMatrix();
      this.renderChevrons(te, time);
   }

   protected void renderStargateTollan(SGBaseTE te, float t, float time) {
      BaseGLUtils.glMultMatrix(te.localToGlobalTransformation(Vector3.zero));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      GL11.glPushMatrix();
      GL11.glScalef(1.1F, 1.1F, 1.1F);
      if (te.isConnected()) {
         GL11.glPushMatrix();
         GL11.glScalef(0.88F, 0.88F, 0.88F);
         this.renderEventHorizon(te, time, t);
         GL11.glPopMatrix();
      }

      if (te.hasIrisUpgrade) {
         this.renderBarrier(te, (double)t, time);
      }

      GL11.glPopMatrix();
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate2/gatering7.jpg"));
      GL11.glScalef(0.53F, 0.53F, 0.53F);
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.1);
      tollanGateModel.renderAll();
      GL11.glPushMatrix();
      GL11.glRotatef((float)te.interpolatedRingAngle((double)t), 0.0F, 0.0F, 1.0F);
      ringModel.renderAll();
      GL11.glPopMatrix();
      this.renderChevronsTollan(te, time);
   }

   protected void renderStargateDestiny(SGBaseTE te, float t, float time) {
      BaseGLUtils.glMultMatrix(te.localToGlobalTransformation(Vector3.zero));
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (te.isConnected()) {
         GL11.glPushMatrix();
         GL11.glScalef(0.88F, 0.88F, 0.88F);
         this.renderEventHorizon(te, time, t);
         GL11.glPopMatrix();
      }

      if (te.hasIrisUpgrade) {
         this.renderBarrier(te, (double)t, time);
      }

      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_gate.jpg"));
      GL11.glScalef(0.53F, 0.53F, 0.53F);
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.15);
      GL11.glPushMatrix();
      GL11.glRotatef((float)te.interpolatedRingAngle((double)t), 0.0F, 0.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glRotatef(-9.5F, 0.0F, 0.0F, 1.0F);
      universeGateModel.renderAll();
      GL11.glPopMatrix();
      this.renderChevronsUniverse(te, time);
      this.renderUniverseSymbols(te, t, time);
      GL11.glPopMatrix();
   }

   protected void renderUniverseSymbols(SGBaseTE te, float t, float time) {
      GL11.glTranslatef(0.0F, 0.0F, 0.34F);
      GL11.glPushMatrix();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(70.4F, 0.0F, 1.0F, 0.0F);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/symbol.png"));

      for(int i = 0; i < 36; ++i) {
         this.renderUniverseSymbol(te, t, time, (float)(i * -10), i, te.dialledAddress.substring(0, te.numEngagedChevrons).contains("" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i - 1 < 0 ? 35 : i - 1)));
      }

      GL11.glPopMatrix();
   }

   protected void renderUniverseSymbol(SGBaseTE te, float t, float time, float angle, int symbol, boolean enabled) {
      if (enabled) {
         GL11.glDisable(2896);
         setLightingDisabled(true);
      } else {
         GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
      }

      universeSymbols[symbol].renderAll();
      if (enabled) {
         GL11.glEnable(2896);
         setLightingDisabled(false);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderChevrons(SGBaseTE te, float time) {
      this.detectChevronEngagement(te, time);
      GL11.glScalef(1.1F, 1.1F, 1.1F);
      int[] sequence = SGBaseTE.chevronSequence(te.diallingChevronCount);

      for(int i = 0; i < 9; ++i) {
         boolean engaged = te.chevronIsEngaged(sequence[i]);
         this.renderChevron(te, i, engaged, time);
      }

   }

   private void renderChevronsUniverse(SGBaseTE te, float time) {
      this.detectChevronEngagement(te, time);
      GL11.glScalef(1.0F, 1.0F, 1.0F);
      int[] sequence = SGBaseTE.chevronSequence(te.diallingChevronCount);

      for(int i = 0; i < 9; ++i) {
         boolean engaged = te.chevronIsEngaged(sequence[i]);
         this.renderChevronUniverse(te, i, engaged, time);
      }

   }

   private void renderChevronsTollan(SGBaseTE te, float time) {
      this.detectChevronEngagement(te, time);
      GL11.glScalef(1.1F, 1.1F, 1.1F);
      int[] sequence = SGBaseTE.chevronSequence(te.diallingChevronCount);

      for(int i = 0; i < 9; ++i) {
         boolean engaged = te.chevronIsEngaged(sequence[i]);
         this.renderChevronTollan(te, i, engaged, time);
      }

   }

   private void detectChevronEngagement(SGBaseTE te, float time) {
      if (te.newChevronEngaged) {
         te.newChevronEngaged = false;
         te.chevronEngagingTimes[te.numEngagedChevrons] = -2.0F;
         te.lastNumChevrons = te.numEngagedChevrons;
         te.chevronMovingStartTime = time;
      }

   }

   private void detectChevronDeactivation(SGBaseTE te, int index, boolean engaged, float time) {
      if (!engaged && te.chevronEngaged[this.getChevronRealNumber(index)]) {
         te.chevronEngagingTimes[this.getChevronRealNumber(index)] = time;
         te.chevronEngaged[this.getChevronRealNumber(index)] = false;
      }

   }

   private void renderChevron(SGBaseTE te, int index, boolean engaged, float time) {
      GL11.glPushMatrix();
      GL11.glRotatef((float)(-40 * index), 0.0F, 0.0F, 1.0F);
      GL11.glTranslated((double)0.0F, -0.45, (double)0.0F);
      if (engaged) {
         te.chevronEngaged[this.getChevronRealNumber(index)] = engaged;
      }

      this.detectChevronDeactivation(te, index, engaged, time);
      this.bindTexture(SGCraft.mod.resourceLocation(this.getChevronTexture(te, index, engaged, time)));
      chevronFrameModel.renderAll();
      chevronBackModel.renderAll();
      GL11.glPushMatrix();
      if (index == 0 && te.chevronMovingStartTime > -1.0F) {
         if (time - te.chevronMovingStartTime > 15.0F) {
            te.chevronMovingStartTime = -1.0F;
            te.chevronEngagingTimes[te.numEngagedChevrons] = time;
         } else if (te.getType() == 0) {
            GL11.glTranslated((double)0.0F, -0.1 * (double)(1.0F - Math.abs((time - te.chevronMovingStartTime) / 7.0F - 1.0F)), (double)0.0F);
         }
      }

      chevronMovingModel.renderAll();
      GL11.glPopMatrix();
      if (engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] != -2.0F || index == 0 && te.chevronMovingStartTime > -1.0F && te.getType() != 1) {
         GL11.glPushMatrix();
         GL11.glDisable(2896);
         setLightingDisabled(true);
      }

      GL11.glPushMatrix();
      if (index == 0 && te.chevronMovingStartTime > -1.0F && te.getType() == 0) {
         GL11.glTranslated((double)0.0F, 0.1 * (double)(1.0F - Math.abs((time - te.chevronMovingStartTime) / 7.0F - 1.0F)), (double)0.0F);
      }

      chevronLightModel.renderAll();
      GL11.glPopMatrix();
      if (engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] != -2.0F || index == 0 && te.chevronMovingStartTime > -1.0F && te.getType() != 1) {
         GL11.glEnable(2896);
         setLightingDisabled(false);
         GL11.glPopMatrix();
      }

      GL11.glPopMatrix();
   }

   private void renderChevronUniverse(SGBaseTE te, int index, boolean engaged, float time) {
      GL11.glPushMatrix();
      GL11.glRotatef((float)(-40 * index - 90), 0.0F, 0.0F, 1.0F);
      if (engaged) {
         if (!te.chevronEngaged[this.getChevronRealNumber(index)]) {
            te.chevronEngagingTimes[this.getChevronRealNumber(index)] = time;
         }

         te.chevronEngaged[this.getChevronRealNumber(index)] = engaged;
      }

      this.detectChevronDeactivation(te, index, engaged, time);
      this.bindTexture(SGCraft.mod.resourceLocation(this.getChevronTextureUniverse(te, index, engaged, time)));
      if (te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
         GL11.glDisable(2896);
         setLightingDisabled(true);
      }

      universeChevronModel.renderAll();
      if (te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
         GL11.glEnable(2896);
         setLightingDisabled(false);
      }

      GL11.glPopMatrix();
   }

   private void renderChevronTollan(SGBaseTE te, int index, boolean engaged, float time) {
      GL11.glPushMatrix();
      GL11.glRotatef((float)(-40 * index), 0.0F, 0.0F, 1.0F);
      GL11.glTranslated((double)0.0F, -0.45, (double)0.0F);
      if (engaged) {
         te.chevronEngaged[this.getChevronRealNumber(index)] = engaged;
      }

      this.detectChevronDeactivation(te, index, engaged, time);
      this.bindTexture(SGCraft.mod.resourceLocation(this.getChevronTexture(te, index, engaged, time)));
      tollanChevronFrameModel.renderAll();
      GL11.glPushMatrix();
      if (index == 0 && te.chevronMovingStartTime > -1.0F) {
         if (time - te.chevronMovingStartTime > 15.0F) {
            te.chevronMovingStartTime = -1.0F;
            te.chevronEngagingTimes[te.numEngagedChevrons] = time;
         } else {
            GL11.glTranslated((double)0.0F, -0.1 * (double)(1.0F - Math.abs((time - te.chevronMovingStartTime) / 7.0F - 1.0F)), (double)0.0F);
         }
      }

      tollanChevronMovingModel.renderAll();
      if ((!engaged || te.chevronEngagingTimes[this.getChevronRealNumber(index)] == -2.0F) && (index != 0 || !(te.chevronMovingStartTime > -1.0F))) {
         tollanChevronLightModel.renderAll();
         tollanChevronSecondaryLightModel.renderAll();
      } else {
         GL11.glDisable(2896);
         setLightingDisabled(true);
         tollanChevronLightModel.renderAll();
         tollanChevronSecondaryLightModel.renderAll();
         GL11.glEnable(2896);
         setLightingDisabled(false);
      }

      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   private String getChevronTexture(SGBaseTE te, int index, boolean engaged, float time) {
      if (index == 0 && te.chevronMovingStartTime > -1.0F && !engaged && te.getType() != 1) {
         return this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * (1.0F - Math.abs((time - te.chevronMovingStartTime) / 7.5F - 1.0F)), 0.0F) + ".jpg";
      } else if (index == 0 && te.chevronMovingStartTime > -1.0F && engaged) {
         return this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * ((time - te.chevronMovingStartTime) / 15.0F), 0.0F) + ".jpg";
      } else if (engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
         return time - te.chevronEngagingTimes[this.getChevronRealNumber(index)] <= 10.0F ? this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * ((time - te.chevronEngagingTimes[this.getChevronRealNumber(index)]) / 10.0F), 0.0F) + ".jpg" : this.getStargateTextureBaseDir(te) + "chevron10.jpg";
      } else if (engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] != -2.0F) {
         return this.getStargateTextureBaseDir(te) + "chevron10.jpg";
      } else {
         if (!engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
            if (time - te.chevronEngagingTimes[this.getChevronRealNumber(index)] <= 10.0F) {
               return this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * (1.0F - (time - te.chevronEngagingTimes[this.getChevronRealNumber(index)]) / 10.0F), 0.0F) + ".jpg";
            }

            te.chevronEngagingTimes[this.getChevronRealNumber(index)] = -1.0F;
         }

         return this.getStargateTextureBaseDir(te) + "chevron0.jpg";
      }
   }

   private String getChevronTextureUniverse(SGBaseTE te, int index, boolean engaged, float time) {
      if (engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
         return time - te.chevronEngagingTimes[this.getChevronRealNumber(index)] <= 10.0F ? this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * ((time - te.chevronEngagingTimes[this.getChevronRealNumber(index)]) / 10.0F), 0.0F) + ".jpg" : this.getStargateTextureBaseDir(te) + "chevron10.jpg";
      } else {
         if (!engaged && te.chevronEngagingTimes[this.getChevronRealNumber(index)] > 0.0F) {
            if (time - te.chevronEngagingTimes[this.getChevronRealNumber(index)] <= 10.0F) {
               return this.getStargateTextureBaseDir(te) + "chevron" + (int)Math.max(10.0F * (1.0F - (time - te.chevronEngagingTimes[this.getChevronRealNumber(index)]) / 10.0F), 0.0F) + ".jpg";
            }

            te.chevronEngagingTimes[this.getChevronRealNumber(index)] = -1.0F;
         }

         return this.getStargateTextureBaseDir(te) + "chevron0.jpg";
      }
   }

   private String getStargateTextureBaseDir(SGBaseTE te) {
      return "textures/tileentity/stargate" + te.getType() + "/";
   }

   private int getChevronRealNumber(int index) {
      if (index != 0) {
         if (index <= 3) {
            return index;
         }

         if (index >= 6) {
            return index - 2;
         }

         if (index == 4 || index == 5) {
            return index + 3;
         }
      }

      return index;
   }

   private int getChevronIndexNumber(int number, int k) {
      if (k == 1 && number == 9) {
         return 0;
      } else if (k == 0 && number == 7) {
         return 0;
      } else if (number <= 3) {
         return number;
      } else if (number > 3 && number <= 6) {
         return number + 2;
      } else {
         return number != 7 && number != 8 ? number : number - 3;
      }
   }

   protected static void setLightingDisabled(boolean off) {
      OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      if (off) {
         GL11.glDisable(3553);
      } else {
         GL11.glEnable(3553);
      }

      OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   void renderEventHorizon(SGBaseTE te, float time, float partial) {
      if (te.state == SGState.Transient && te.transientStartTime == 0.0F) {
         te.transientStartTime = time;
      }
      boolean opening = te.transientStartTime >= 0.0F && time - te.transientStartTime < 100.0F;
      if (!opening) {
         te.transientStartTime = -1.0F;
      }
      GL11.glPushMatrix();
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.08);
      GL11.glDisable(2896);
      setLightingDisabled(true);
      if (opening) {
         this.bindTexture(ehTextureKawoosh[te.getType()]);
         GL11.glColor3f(2.0F, 2.0F, 2.0F);
      } else {
         this.bindTexture(ehTexture[te.getType()]);
      }

      GL11.glDisable(2884);
      GL11.glNormal3d((double)0.0F, (double)0.0F, (double)1.0F);
      GL11.glColor4d((double)1000.0F, (double)1000.0F, (double)1000.0F, (double)400.0F);
      int nrmlBlendSrc = GL11.glGetInteger(3041);
      int nrmlBlendDest = GL11.glGetInteger(3040);
      int mod = opening ? SGBaseTE.openingFrame(time - te.transientStartTime) : (int)(time * 6.0F % 185.0F);
      float uOffset = 0.071428575F * (float)(mod % 14);
      float vOffset = 0.071428575F * (float)(mod / 14);
      float[][][] grid = te.getEventHorizonGrid();

      for(int i = 0; i < grid.length - 1; ++i) {
         for(int j = 0; j < grid[i].length - 1; ++j) {
            if (i + j >= 2 && (i != 0 || j < grid[i].length - 3) && (i != 1 || j != grid[i].length - 2) && (i != grid[i].length - 3 || j != 0) && (i != grid[i].length - 2 || j >= 2) && i + j <= 14) {
               this.drawEHQuad((float)i, (float)j, grid[i][j][1] + (grid[i][j][0] - grid[i][j][1]) * partial, grid[i][j + 1][1] + (grid[i][j + 1][0] - grid[i][j + 1][1]) * partial, grid[i + 1][j + 1][1] + (grid[i + 1][j + 1][0] - grid[i + 1][j + 1][1]) * partial, grid[i + 1][j][1] + (grid[i + 1][j][0] - grid[i + 1][j][1]) * partial, (float)(grid.length - 1), uOffset, vOffset);
            }
         }
      }

      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBlendFunc(nrmlBlendSrc, nrmlBlendDest);
      GL11.glEnable(2896);
      setLightingDisabled(false);
      GL11.glPopMatrix();
   }

   void renderEventHorizon2(SGBaseTE te, float time, float partial) {
      GL11.glPushMatrix();
      GL11.glTranslated((double)0.0F, (double)0.0F, 0.08);
      GL11.glDisable(2896);
      setLightingDisabled(true);
      if (te.transientStartTime > 0.0F) {
         GL11.glColor3f(2.0F, 2.0F, 2.0F);
         this.bindTexture(ehTextureKawoosh[te.getType()]);
      } else {
         this.bindTexture(ehTexture[te.getType()]);
      }

      GL11.glDisable(2884);
      GL11.glNormal3d((double)0.0F, (double)0.0F, (double)1.0F);
      if (time - te.transientStartTime > 60.0F) {
         GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)(1.0F - (time - te.transientStartTime - 60.0F) / 40.0F));
      }

      int nrmlBlendSrc = GL11.glGetInteger(3041);
      int nrmlBlendDest = GL11.glGetInteger(3040);
      GL11.glBlendFunc(770, 1);
      double rclip = (double)2.5F * (te.irisIsClosed() ? te.getIrisAperture((double)0.0F) : (double)1.0F);
      int mod = (int)(time * 6.0F % 185.0F);
      float uOffset = 0.071428575F * (float)(mod % 14);
      float vOffset = 0.071428575F * (float)(mod / 14);
      float[][][] grid = te.getEventHorizonGrid();

      for(int i = 0; i < grid.length - 1; ++i) {
         for(int j = 0; j < grid[i].length - 1; ++j) {
            if (i + j >= 2 && (i != 0 || j < grid[i].length - 3) && (i != 1 || j != grid[i].length - 2) && (i != grid[i].length - 3 || j != 0) && (i != grid[i].length - 2 || j >= 2) && i + j <= 14) {
               this.drawEHQuad((float)i, (float)j, grid[i][j][1] + (grid[i][j][0] - grid[i][j][1]) * partial, grid[i][j + 1][1] + (grid[i][j + 1][0] - grid[i][j + 1][1]) * partial, grid[i + 1][j + 1][1] + (grid[i + 1][j + 1][0] - grid[i + 1][j + 1][1]) * partial, grid[i + 1][j][1] + (grid[i + 1][j][0] - grid[i + 1][j][1]) * partial, (float)(grid.length - 1), uOffset, vOffset);
            }
         }
      }

      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBlendFunc(nrmlBlendSrc, nrmlBlendDest);
      GL11.glEnable(2896);
      setLightingDisabled(false);
      GL11.glPopMatrix();
   }

   private void drawEHQuad(float i, float j, float z1, float z2, float z3, float z4, float size, float uOffset, float vOffset) {
      Tessellator t = Tessellator.instance;
      t.startDrawingQuads();
      this.addEHVertex(t, i * 0.5F - 2.25F, j * 0.5F - 2.25F, z1, size, uOffset, vOffset);
      this.addEHVertex(t, i * 0.5F - 2.25F, (j + 1.0F) * 0.5F - 2.25F, z2, size, uOffset, vOffset);
      this.addEHVertex(t, (i + 1.0F) * 0.5F - 2.25F, (j + 1.0F) * 0.5F - 2.25F, z3, size, uOffset, vOffset);
      this.addEHVertex(t, (i + 1.0F) * 0.5F - 2.25F, j * 0.5F - 2.25F, z4, size, uOffset, vOffset);
      t.draw();
   }

   private void addEHVertex(Tessellator t, float x, float y, float z, float size, float uOffset, float vOffset) {
      double radius = Math.sqrt(x * x + y * y);
      if (radius > 2.0) {
         x *= 2.0 / radius;
         y *= 2.0 / radius;
      }
      // UV follows clamped position
      t.addVertexWithUV(x, y, z, (x + 2.25F) * 2.0F / size / 14.0F + uOffset, (y + 2.25F) * 2.0F / size / 14.0F + vOffset);
   }

   protected void detectIrisHit(SGBaseTE te, double t, float time) {
      if (te.irisHitTime == -1.0F) {
         te.irisHitTime = time;
      } else if (te.irisHitTime != -2.0F && time - te.irisHitTime > 6.0F) {
         te.irisHitTime = -2.0F;
      }

   }

   protected void renderBarrier(SGBaseTE te, double t, float time) {
      if (te.barrierKind == BarrierKind.IRIS) {
         this.renderIris(te, t);
      } else if (te.barrierKind == BarrierKind.SHIELD) {
         this.renderIrisForcefield(te, t, time);
      }
   }

   protected void renderIrisForcefield(SGBaseTE te, double t, float time) {
      if (te.getIrisAperture(t) < (double)1.0F) {
         this.detectIrisHit(te, t, time);
         int progress = (int)(te.getIrisAperture(t) * (double)7.0F);
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)((double)0.5F * ((double)1.0F - te.getIrisAperture(t))));
         GL11.glDisable(2896);
         setLightingDisabled(true);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/iris_force_field_" + te.getType() + ".png"));
         GL11.glBegin(7);
         this.vertex3f(-2.0F, -2.0F, 0.1F, (float)progress / 8.0F, 1.0F);
         this.vertex3f(2.0F, -2.0F, 0.1F, (float)(progress + 1) / 8.0F, 1.0F);
         this.vertex3f(2.0F, 2.0F, 0.1F, (float)(progress + 1) / 8.0F, 0.0F);
         this.vertex3f(-2.0F, 2.0F, 0.1F, (float)progress / 8.0F, 0.0F);
         GL11.glEnd();
         GL11.glBegin(7);
         this.vertex3f(-2.0F, 2.0F, 0.1F, (float)progress / 8.0F, 0.0F);
         this.vertex3f(2.0F, 2.0F, 0.1F, (float)(progress + 1) / 8.0F, 0.0F);
         this.vertex3f(2.0F, -2.0F, 0.1F, (float)(progress + 1) / 8.0F, 1.0F);
         this.vertex3f(-2.0F, -2.0F, 0.1F, (float)progress / 8.0F, 1.0F);
         GL11.glEnd();
         GL11.glDisable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(2896);
         setLightingDisabled(false);
         GL11.glPopMatrix();
         if (te.irisHitTime > 0.0F) {
            this.renderIrisForcefieldHit(te, t, time, te.irisHitEntitySize, te.irisHitOffsetX);
         }
      }

   }

   protected void renderIrisForcefieldHit(SGBaseTE te, double t, float time, float entitySize, float offsetX) {
      GL11.glPushMatrix();
      GL11.glDisable(2896);
      setLightingDisabled(true);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 772);
      GL11.glScalef(0.9F, 0.9F, 1.0F);
      GL11.glScalef(1.0F, entitySize, 1.0F);
      GL11.glTranslatef(offsetX, -1.0F * (2.0F - entitySize), 0.0F);
      this.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/iris_hit.png"));
      GL11.glBegin(7);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)((double)0.5F * ((double)-16.0F * Math.pow((double)((time - te.irisHitTime) / 6.0F - 0.5F), (double)4.0F) + (double)1.0F)));
      this.vertex3f(-1.0F, -1.0F, 0.11F, 0.0F, 1.0F);
      this.vertex3f(1.0F, -1.0F, 0.11F, 1.0F, 1.0F);
      this.vertex3f(1.0F, 1.0F, 0.11F, 1.0F, 0.0F);
      this.vertex3f(-1.0F, 1.0F, 0.11F, 0.0F, 0.0F);
      GL11.glEnd();
      GL11.glDisable(3042);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(2896);
      setLightingDisabled(false);
      GL11.glPopMatrix();
   }

   void renderIris(SGBaseTE te, double t) {
      this.bindTexture(SGCraft.mod.resourceLocation(te.getType() == 0 ? "textures/tileentity/iris_texture.jpg" : "textures/tileentity/iris_texture" + te.getType() + ".png"));
      GL11.glPushMatrix();
      GL11.glScalef(0.53F, 0.53F, 0.53F);

      for(int i = 0; i < 20; ++i) {
         GL11.glPushMatrix();
         GL11.glRotatef((float)(i * 18), 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef(4.45F, 0.0F, 0.1F);
         GL11.glRotatef((float)((double)4.0F * ((double)1.0F - te.getIrisAperture(t))), 0.0F, 1.0F, 0.0F);
         GL11.glRotatef((float)((double)57.0F * ((double)1.0F - te.getIrisAperture(t))) - 1.0F, 0.0F, 0.0F, 1.0F);
         irisModel.renderAll();
         GL11.glPopMatrix();
      }

      GL11.glPopMatrix();
   }

   void selectTile(int index) {
      this.u0 = (double)(index % 32) * (double)0.03125F;
      this.v0 = (double)(index / 32) * (double)0.5F;
   }

   void vertex(double x, double y, double z, double u, double v) {
      GL11.glTexCoord2d(this.u0 + u * (double)0.001953125F, this.v0 + v * (double)0.03125F);
      GL11.glVertex3d(x, y, z);
   }

   void vertex3f(float x, float y, float z, float u, float v) {
      GL11.glTexCoord2d((double)u, (double)v);
      GL11.glVertex3d((double)x, (double)y, (double)z);
   }

   static {
      irisModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/iris.obj"));
      gateModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/gate.obj"));
      ringModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/ring.obj"));
      ringModelOri = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/ring_ori.obj"));
      atlantisRingModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/ring_atlantis.obj"));
      chevronFrameModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/chevronFrame.obj"));
      chevronBackModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/chevronBack.obj"));
      chevronLightModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/chevronLight.obj"));
      chevronMovingModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/chevronMoving.obj"));
      universeGateModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/universe/gate.obj"));
      universeChevronModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/universe/universe_chevron2.obj"));
      universeSymbols = new IModelCustom[36];
      tollanGateModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/tollan/gate.obj"));
      tollanChevronFrameModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/tollan/chevronFrame.obj"));
      tollanChevronLightModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/tollan/chevronLight.obj"));
      tollanChevronSecondaryLightModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/tollan/chevronLight_secondary.obj"));
      tollanChevronMovingModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/tollan/chevronMoving.obj"));
      ehTexture = new ResourceLocation[]{SGCraft.mod.resourceLocation("textures/tileentity/eventhorizon0.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizon1.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizon2.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizon3.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizon4.jpg")};
      ehTextureKawoosh = new ResourceLocation[]{SGCraft.mod.resourceLocation("textures/tileentity/eventhorizonKawoosh0.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizonKawoosh1.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizonKawoosh2.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizonKawoosh3.jpg"), SGCraft.mod.resourceLocation("textures/tileentity/eventhorizonKawoosh4.jpg")};

      for(int i = 1; i <= universeSymbols.length; ++i) {
         universeSymbols[i - 1] = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/universe/" + (i < 10 ? "0" + i : "" + i) + ".obj"));
      }

   }
}
