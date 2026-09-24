package gcewing.sg;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public class BaseModelRenderer implements BaseModClient.ICustomRenderer {
   protected BaseModClient.IModel model;
   protected BaseModClient.ITexture[] textures;
   protected Vector3 origin;

   public BaseModelRenderer(BaseModClient.IModel model, BaseModClient.ITexture... textures) {
      this(model, Vector3.zero, textures);
   }

   public BaseModelRenderer(BaseModClient.IModel model, Vector3 origin, BaseModClient.ITexture... textures) {
      this.model = model;
      this.textures = textures;
      this.origin = origin;
   }

   public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, BaseModClient.IRenderTarget target, EnumWorldBlockLayer layer, Trans3 t) {
      BaseMod.IBlock block = (BaseMod.IBlock)state.getBlock();
      Trans3 t2 = t.t(block.localToGlobalTransformation(world, pos, state, Vector3.zero)).translate(this.origin);
      this.model.render(t2, target, this.textures);
   }

   public void renderItemStack(ItemStack stack, BaseModClient.IRenderTarget target, Trans3 t) {
      this.model.render(t.translate(this.origin), target, this.textures);
   }
}
