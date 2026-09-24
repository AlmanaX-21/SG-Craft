package gcewing.sg;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class BaseModel implements BaseModClient.IModel {
   public double[] bounds;
   public Face[] faces;
   public double[][] boxes;
   static Gson gson = new Gson();

   public static BaseModel fromResource(ResourceLocation location) {
      String path = String.format("/assets/%s/%s", location.getResourceDomain(), location.getResourcePath());
      InputStream in = BaseModel.class.getResourceAsStream(path);
      if (in == null) {
         throw new RuntimeException(String.format("Cannot find resource %s", path));
      } else {
         BaseModel model = (BaseModel)gson.fromJson(new InputStreamReader(in), BaseModel.class);
         model.prepare();
         return model;
      }
   }

   public AxisAlignedBB getBounds() {
      return AxisAlignedBB.getBoundingBox(this.bounds[0], this.bounds[1], this.bounds[2], this.bounds[3], this.bounds[4], this.bounds[5]);
   }

   void prepare() {
      for(Face face : this.faces) {
         double[][] p = face.vertices;
         int[] t = face.triangles[0];
         face.normal = Vector3.unit(Vector3.sub(p[t[1]], p[t[0]]).cross(Vector3.sub(p[t[2]], p[t[0]])));
      }

   }

   public void addBoxesToList(Trans3 t, List list) {
      if (this.boxes != null && this.boxes.length > 0) {
         for(int i = 0; i < this.boxes.length; ++i) {
            this.addBoxToList(this.boxes[i], t, list);
         }
      } else {
         this.addBoxToList(this.bounds, t, list);
      }

   }

   protected void addBoxToList(double[] b, Trans3 t, List list) {
      t.addBox(b[0], b[1], b[2], b[3], b[4], b[5], list);
   }

   public void render(Trans3 t, BaseModClient.IRenderTarget renderer, BaseModClient.ITexture... textures) {
      Vector3 p = null;
      Vector3 n = null;

      for(Face face : this.faces) {
         int k = face.texture;
         if (k >= textures.length) {
            k = textures.length - 1;
         }

         BaseModClient.ITexture tex = textures[k];
         if (tex != null) {
            renderer.setTexture(tex);

            for(int[] tri : face.triangles) {
               renderer.beginTriangle();

               for(int i = 0; i < 3; ++i) {
                  int j = tri[i];
                  double[] c = face.vertices[j];
                  p = t.p(c[0], c[1], c[2]);
                  n = t.v(c[3], c[4], c[5]);
                  renderer.setNormal(n);
                  renderer.addVertex(p, c[6], c[7]);
               }

               renderer.endFace();
            }
         }
      }

   }

   public static class Face {
      int texture;
      double[][] vertices;
      int[][] triangles;
      Vector3 normal;
   }
}
