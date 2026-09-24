package gcewing.sg;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BaseGLRenderTarget extends BaseRenderTarget {
   protected boolean usingLightmap;
   protected int glMode;
   protected int emissiveMode;
   protected int texturedMode;

   public BaseGLRenderTarget() {
      super((double)0.0F, (double)0.0F, (double)0.0F, (IIcon)null);
   }

   public void start(boolean usingLightmap) {
      this.usingLightmap = usingLightmap;
      GL11.glPushAttrib(266304);
      GL11.glEnable(32826);
      GL11.glShadeModel(7425);
      this.glMode = 0;
      this.emissiveMode = -1;
      this.texturedMode = -1;
      this.texture = null;
   }

   public void setTexture(BaseModClient.ITexture tex) {
      if (this.texture != tex) {
         super.setTexture(tex);
         ResourceLocation loc = tex.location();
         if (loc != null) {
            this.setGLMode(0);
            BaseModClient.bindTexture(loc);
         }

         this.setTexturedMode(!tex.isSolid());
         this.setEmissiveMode(tex.isEmissive());
      }

   }

   protected void setEmissiveMode(boolean state) {
      int mode = state ? 1 : 0;
      if (this.emissiveMode != mode) {
         this.glSetEnabled(2896, !state);
         if (this.usingLightmap) {
            this.setLightmapEnabled(!state);
         }

         this.emissiveMode = mode;
      }

   }

   protected void setTexturedMode(boolean state) {
      int mode = state ? 1 : 0;
      if (this.texturedMode != mode) {
         this.setGLMode(0);
         this.glSetEnabled(3553, state);
         this.texturedMode = mode;
      }

   }

   protected void setLightmapEnabled(boolean state) {
      OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      this.glSetEnabled(3553, state);
      OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   protected void glSetEnabled(int mode, boolean state) {
      if (state) {
         GL11.glEnable(mode);
      } else {
         GL11.glDisable(mode);
      }

   }

   protected void rawAddVertex(Vector3 p, double u, double v) {
      this.setGLMode(this.verticesPerFace);
      GL11.glColor4f(this.r(), this.g(), this.b(), this.a());
      GL11.glNormal3d(this.normal.x, this.normal.y, this.normal.z);
      GL11.glTexCoord2d(u, v);
      GL11.glVertex3d(p.x, p.y, p.z);
   }

   protected void setGLMode(int mode) {
      if (this.glMode != mode) {
         if (this.glMode != 0) {
            GL11.glEnd();
         }

         this.glMode = mode;
         switch (this.glMode) {
            case 0:
               break;
            case 1:
            case 2:
            default:
               throw new IllegalStateException(String.format("Invalid glMode %s", this.glMode));
            case 3:
               GL11.glBegin(4);
               break;
            case 4:
               GL11.glBegin(7);
         }
      }

   }

   public void finish() {
      this.setGLMode(0);
      this.setEmissiveMode(false);
      this.setTexturedMode(true);
      GL11.glPopAttrib();
      super.finish();
   }
}
