package gcewing.sg;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public abstract class BaseTileEntityRenderer extends TileEntitySpecialRenderer {
   protected static BaseGLRenderTarget target = new BaseGLRenderTarget();

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float dt) {
      this.renderTileEntityAt(te, x, y, z, dt, -1);
   }

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float dt, int destroyStage) {
      BaseTileEntity bte = (BaseTileEntity)te;
      Trans3 t = bte.localToGlobalTransformation(Vector3.blockCenter(x, y, z));
      target.start(true);
      this.render(bte, dt, destroyStage, t, target);
      target.finish();
   }

   public void render(BaseTileEntity te, float dt, int destroyStage, Trans3 t, BaseModClient.IRenderTarget target) {
   }
}
