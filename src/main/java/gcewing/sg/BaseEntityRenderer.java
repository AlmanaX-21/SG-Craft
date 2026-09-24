package gcewing.sg;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class BaseEntityRenderer<T extends Entity> extends Render {
   public void doRender(Entity entity, double x, double y, double z, float yaw, float dt) {
      this.renderEntity((T)entity, x, y, z, yaw, dt);
   }

   public abstract void renderEntity(T var1, double var2, double var4, double var6, float var8, float var9);

   protected ResourceLocation getEntityTexture(Entity entity) {
      return this.getTexture((T)entity);
   }

   protected ResourceLocation getTexture(T entity) {
      return null;
   }
}
