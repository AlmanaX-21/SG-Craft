package gcewing.sg;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;

public abstract class BaseRenderTarget implements BaseModClient.IRenderTarget {
   protected double blockX;
   protected double blockY;
   protected double blockZ;
   protected int verticesPerFace;
   protected int vertexCount;
   protected BaseModClient.ITexture texture;
   protected Vector3 normal;
   protected EnumFacing face;
   protected float red = 1.0F;
   protected float green = 1.0F;
   protected float blue = 1.0F;
   protected float alpha = 1.0F;
   protected float shade;
   protected boolean expandTrianglesToQuads;
   protected boolean textureOverride;

   public BaseRenderTarget(double x, double y, double z, IIcon overrideIcon) {
      this.blockX = x;
      this.blockY = y;
      this.blockZ = z;
      if (overrideIcon != null) {
         this.texture = BaseTexture.fromSprite(overrideIcon);
         this.textureOverride = true;
      }

   }

   public boolean isRenderingBreakEffects() {
      return this.textureOverride;
   }

   public void beginTriangle() {
      this.setMode(3);
   }

   public void beginQuad() {
      this.setMode(4);
   }

   protected void setMode(int mode) {
      if (this.vertexCount != 0) {
         throw new IllegalStateException("Changing mode in mid-face");
      } else {
         this.verticesPerFace = mode;
      }
   }

   public void setTexture(BaseModClient.ITexture texture) {
      if (!this.textureOverride) {
         if (texture == null) {
            throw new IllegalArgumentException("Setting null texture");
         }

         this.texture = texture;
      }

   }

   public void setColor(double r, double g, double b, double a) {
      this.red = (float)r;
      this.green = (float)g;
      this.blue = (float)b;
      this.alpha = (float)a;
   }

   public void setNormal(Vector3 n) {
      this.normal = n;
      this.face = n.facing();
      this.shade = (float)(0.6 * n.x * n.x + 0.8 * n.z * n.z + (n.y > (double)0.0F ? (double)1.0F : (double)0.5F) * n.y * n.y);
   }

   public void addVertex(Vector3 p, double u, double v) {
      if (this.texture.isProjected()) {
         this.addProjectedVertex(p, this.face);
      } else {
         this.addUVVertex(p, u, v);
      }

   }

   public void addUVVertex(Vector3 p, double u, double v) {
      if (this.verticesPerFace == 0) {
         throw new IllegalStateException("No face active");
      } else if (this.vertexCount >= this.verticesPerFace) {
         throw new IllegalStateException("Too many vertices in face");
      } else if (this.normal == null) {
         throw new IllegalStateException("No normal");
      } else if (this.texture == null) {
         throw new IllegalStateException("No texture");
      } else {
         double iu = this.texture.interpolateU(u);
         double iv = this.texture.interpolateV(v);
         this.rawAddVertex(p, iu, iv);
         if (++this.vertexCount == 3 && this.expandTrianglesToQuads && this.verticesPerFace == 3) {
            this.rawAddVertex(p, iu, iv);
         }

      }
   }

   public void endFace() {
      if (this.vertexCount < this.verticesPerFace) {
         throw new IllegalStateException("Too few vertices in face");
      } else {
         this.vertexCount = 0;
         this.verticesPerFace = 0;
      }
   }

   public void finish() {
      if (this.vertexCount > 0) {
         throw new IllegalStateException("Rendering ended with incomplete face");
      }
   }

   protected abstract void rawAddVertex(Vector3 var1, double var2, double var4);

   public float r() {
      return (float)((double)this.red * this.texture.red());
   }

   public float g() {
      return (float)((double)this.green * this.texture.green());
   }

   public float b() {
      return (float)((double)this.blue * this.texture.blue());
   }

   public float a() {
      return this.alpha;
   }

   public void addProjectedVertex(Vector3 p, EnumFacing face) {
      double x = p.x - this.blockX;
      double y = p.y - this.blockY;
      double z = p.z - this.blockZ;
      double u;
      double v;
      switch (face) {
         case DOWN:
            u = x;
            v = (double)1.0F - z;
            break;
         case UP:
            u = x;
            v = z;
            break;
         case NORTH:
            u = (double)1.0F - x;
            v = (double)1.0F - y;
            break;
         case SOUTH:
            u = x;
            v = (double)1.0F - y;
            break;
         case WEST:
            u = (double)1.0F - z;
            v = (double)1.0F - y;
            break;
         case EAST:
            u = z;
            v = (double)1.0F - y;
            break;
         default:
            u = (double)0.0F;
            v = (double)0.0F;
      }

      this.addUVVertex(p, u, v);
   }
}
