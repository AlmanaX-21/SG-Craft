package gcewing.sg;

import net.minecraft.world.IBlockAccess;

public interface ISGBlock {
   SGBaseTE getBaseTE(IBlockAccess var1, BlockPos var2);

   boolean isMerged(IBlockAccess var1, BlockPos var2);
}
