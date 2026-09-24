package gcewing.sg;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public class SGRingBlock0Renderer implements BaseModClient.ICustomRenderer {
   public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, BaseModClient.IRenderTarget target, EnumWorldBlockLayer layer, Trans3 t) {
      ISGBlock ringBlock = (ISGBlock)state.getBlock();
      if (!target.isRenderingBreakEffects() && (layer != EnumWorldBlockLayer.SOLID || ringBlock.isMerged(world, pos))) {
         SGBaseTE te = ringBlock.getBaseTE(world, pos);
         if (te != null) {
            ItemStack stack = te.getCamouflageStack(pos);
            if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof ItemBlock) {
                  IBlockState camoState = BaseBlockUtils.getBlockStateFromItemStack(stack);
                  if (BaseBlockUtils.blockCanRenderInLayer(camoState.getBlock(), layer)) {
                     BaseRenderingUtils.renderAlternateBlock(SGCraft.mod, world, pos, camoState, target);
                  }
               }
            }
         }
      } else {
         ((SGCraftClient)SGCraft.mod.client).renderBlockUsingModelSpec(world, pos, state, target, layer, t);
      }

   }

   public void renderItemStack(ItemStack stack, BaseModClient.IRenderTarget target, Trans3 t) {
      ((SGCraftClient)SGCraft.mod.client).renderItemStackUsingModelSpec(stack, target, t);
   }
}
