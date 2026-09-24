package gcewing.sg;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class BaseTexture implements BaseModClient.ITexture {
   public ResourceLocation location;
   public int tintIndex;
   public double red = (double)1.0F;
   public double green = (double)1.0F;
   public double blue = (double)1.0F;
   public boolean isEmissive;
   public boolean isProjected;

   public int tintIndex() {
      return this.tintIndex;
   }

   public double red() {
      return this.red;
   }

   public double green() {
      return this.green;
   }

   public double blue() {
      return this.blue;
   }

   public boolean isEmissive() {
      return this.isEmissive;
   }

   public boolean isProjected() {
      return this.isProjected;
   }

   public boolean isSolid() {
      return false;
   }

   public static Sprite fromSprite(IIcon icon) {
      return new Sprite(icon);
   }

   public static Image fromImage(ResourceLocation location) {
      return new Image(location);
   }

   public ResourceLocation location() {
      return this.location;
   }

   public BaseModClient.ITexture tinted(int index) {
      BaseTexture result = new Proxy(this);
      result.tintIndex = index;
      return result;
   }

   public BaseModClient.ITexture colored(double red, double green, double blue) {
      BaseTexture result = new Proxy(this);
      result.red = red;
      result.green = green;
      result.blue = blue;
      return result;
   }

   public BaseModClient.ITexture emissive() {
      BaseTexture result = new Proxy(this);
      result.isEmissive = true;
      return result;
   }

   public BaseModClient.ITexture projected() {
      BaseTexture result = new Proxy(this);
      result.isProjected = true;
      return result;
   }

   public BaseModClient.ITiledTexture tiled(int numRows, int numCols) {
      return new TileSet(this, numRows, numCols);
   }

   public static class Proxy extends BaseTexture {
      public BaseModClient.ITexture base;

      public Proxy(BaseModClient.ITexture base) {
         this.base = base;
         this.location = base.location();
         this.tintIndex = base.tintIndex();
         this.red = base.red();
         this.green = base.green();
         this.blue = base.blue();
         this.isEmissive = base.isEmissive();
         this.isProjected = base.isProjected();
      }

      public boolean isSolid() {
         return this.base.isSolid();
      }

      public double interpolateU(double u) {
         return this.base.interpolateU(u);
      }

      public double interpolateV(double v) {
         return this.base.interpolateV(v);
      }
   }

   public static class Sprite extends BaseTexture {
      public IIcon icon;

      public Sprite(IIcon icon) {
         this.icon = icon;
         this.red = this.green = this.blue = (double)1.0F;
      }

      public double interpolateU(double u) {
         return (double)this.icon.getInterpolatedU(u * (double)16.0F);
      }

      public double interpolateV(double v) {
         return (double)this.icon.getInterpolatedV(v * (double)16.0F);
      }

      public String toString() {
         return String.format("BaseTexture.Sprite(%.4f,%.4f,%.4f,%.4f)", this.interpolateU((double)0.0F), this.interpolateV((double)0.0F), this.interpolateU((double)1.0F), this.interpolateV((double)1.0F));
      }
   }

   public static class Image extends BaseTexture {
      public Image(ResourceLocation location) {
         this.location = location;
      }

      public double interpolateU(double u) {
         return u;
      }

      public double interpolateV(double v) {
         return v;
      }
   }

   public static class Solid extends BaseTexture {
      public Solid(double red, double green, double blue) {
         this.red = red;
         this.green = green;
         this.blue = blue;
      }

      public boolean isSolid() {
         return true;
      }

      public double interpolateU(double u) {
         return (double)0.0F;
      }

      public double interpolateV(double v) {
         return (double)0.0F;
      }
   }

   public static class TileSet extends Proxy implements BaseModClient.ITiledTexture {
      public double tileSizeU;
      public double tileSizeV;

      public TileSet(BaseModClient.ITexture base, int numRows, int numCols) {
         super(base);
         this.tileSizeU = (double)1.0F / (double)numCols;
         this.tileSizeV = (double)1.0F / (double)numRows;
      }

      public BaseModClient.ITexture tile(int row, int col) {
         return new Tile(this, row, col);
      }
   }

   public static class Tile extends Proxy {
      protected double u0;
      protected double v0;
      protected double uSize;
      protected double vSize;

      public Tile(TileSet base, int row, int col) {
         super(base);
         this.uSize = base.tileSizeU;
         this.vSize = base.tileSizeV;
         this.u0 = this.uSize * (double)col;
         this.v0 = this.vSize * (double)row;
      }

      public double interpolateU(double u) {
         return super.interpolateU(this.u0 + u * this.uSize);
      }

      public double interpolateV(double v) {
         return super.interpolateV(this.v0 + v * this.vSize);
      }
   }

   public static class Debug extends Sprite {
      public Debug(IIcon icon) {
         super(icon);
      }

      public double interpolateU(double u) {
         double iu = super.interpolateU(u);
         return iu;
      }

      public double interpolateV(double v) {
         double iv = super.interpolateV(v);
         return iv;
      }
   }
}
