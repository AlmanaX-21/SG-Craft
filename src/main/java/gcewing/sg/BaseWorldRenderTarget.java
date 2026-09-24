package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BaseWorldRenderTarget extends BaseRenderTarget {
   protected IBlockAccess world;
   protected BlockPos blockPos;
   protected Block block;
   protected Tessellator tess;
   protected float cmr = 1.0F;
   protected float cmg = 1.0F;
   protected float cmb = 1.0F;
   protected boolean ao;
   protected boolean axisAlignedNormal;
   protected boolean renderingOccurred;
   protected float vr;
   protected float vg;
   protected float vb;
   protected float va;
   protected int vlm1;
   protected int vlm2;

   public BaseWorldRenderTarget(IBlockAccess world, BlockPos pos, Tessellator tess, IIcon overrideIcon) {
      super((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), overrideIcon);
      this.world = world;
      this.blockPos = pos;
      this.block = world.getBlock(pos.x, pos.y, pos.z);
      this.tess = tess;
      this.ao = Minecraft.isAmbientOcclusionEnabled() && this.block.getLightValue() == 0;
      this.expandTrianglesToQuads = true;
   }

   public void setNormal(Vector3 n) {
      super.setNormal(n);
      this.axisAlignedNormal = n.dot(this.face) >= 0.99;
   }

   protected void rawAddVertex(Vector3 p, double u, double v) {
      this.lightVertex(p);
      this.tess.setColorRGBA_F(this.vr, this.vg, this.vb, this.va);
      this.tess.setTextureUV(u, v);
      this.tess.setBrightness(this.vlm1 << 16 | this.vlm2);
      this.tess.addVertex(p.x, p.y, p.z);
      this.renderingOccurred = true;
   }

   protected void lightVertex(Vector3 p) {
      if (this.ao) {
         this.aoLightVertex(p);
      } else {
         this.brLightVertex(p);
      }

   }

   protected void aoLightVertex(Vector3 v) {
      Vector3 n = this.normal;
      double brSum1 = (double)0.0F;
      double brSum2 = (double)0.0F;
      double lvSum = (double)0.0F;
      double wt = (double)0.0F;
      double vx = v.x + (double)0.5F * n.x;
      double vy = v.y + (double)0.5F * n.y;
      double vz = v.z + (double)0.5F * n.z;

      for(int dx = -1; dx <= 1; dx += 2) {
         for(int dy = -1; dy <= 1; dy += 2) {
            for(int dz = -1; dz <= 1; dz += 2) {
               int X = BaseUtils.ifloor(vx + (double)0.5F * (double)dx);
               int Y = BaseUtils.ifloor(vy + (double)0.5F * (double)dy);
               int Z = BaseUtils.ifloor(vz + (double)0.5F * (double)dz);
               BlockPos pos = new BlockPos(X, Y, Z);
               double wox = dx < 0 ? (double)(X + 1) - (vx - (double)0.5F) : vx + (double)0.5F - (double)X;
               double woy = dy < 0 ? (double)(Y + 1) - (vy - (double)0.5F) : vy + (double)0.5F - (double)Y;
               double woz = dz < 0 ? (double)(Z + 1) - (vz - (double)0.5F) : vz + (double)0.5F - (double)Z;
               double w = wox * woy * woz;
               if (w > (double)0.0F) {
                  int br;
                  try {
                     br = this.block.getMixedBrightnessForBlock(this.world, pos.x, pos.y, pos.z);
                  } catch (RuntimeException e) {
                     throw e;
                  }

                  float lv;
                  if (!pos.equals(this.blockPos)) {
                     lv = this.world.getBlock(pos.x, pos.y, pos.z).getAmbientOcclusionLightValue();
                  } else {
                     lv = 1.0F;
                  }

                  if (br != 0) {
                     double br1 = (double)(br >> 16 & 255) / (double)240.0F;
                     double br2 = (double)(br & 255) / (double)240.0F;
                     brSum1 += w * br1;
                     brSum2 += w * br2;
                     wt += w;
                  }

                  lvSum += w * (double)lv;
               }
            }
         }
      }

      int brv;
      if (wt > (double)0.0F) {
         brv = BaseUtils.iround(brSum1 / wt * (double)240.0F) << 16 | BaseUtils.iround(brSum2 / wt * (double)240.0F);
      } else {
         brv = this.block.getMixedBrightnessForBlock(this.world, this.blockPos.x, this.blockPos.y, this.blockPos.z);
      }

      float lvv = (float)lvSum;
      this.setLight(this.shade * lvv, brv);
   }

   protected void brLightVertex(Vector3 p) {
      Vector3 n = this.normal;
      BlockPos pos;
      if (this.axisAlignedNormal) {
         pos = new BlockPos((int)Math.floor(p.x + 0.01 * n.x), (int)Math.floor(p.y + 0.01 * n.y), (int)Math.floor(p.z + 0.01 * n.z));
      } else {
         pos = this.blockPos;
      }

      int br = this.block.getMixedBrightnessForBlock(this.world, pos.x, pos.y, pos.z);
      this.setLight(this.shade, br);
   }

   protected void setLight(float shadow, int br) {
      this.vr = shadow * this.cmr * this.r();
      this.vg = shadow * this.cmg * this.g();
      this.vb = shadow * this.cmb * this.b();
      this.va = this.a();
      this.vlm1 = br >> 16;
      this.vlm2 = br & '\uffff';
   }

   public boolean end() {
      super.finish();
      return this.renderingOccurred;
   }

   public void setRenderingOccurred() {
      this.renderingOccurred = true;
   }
}
