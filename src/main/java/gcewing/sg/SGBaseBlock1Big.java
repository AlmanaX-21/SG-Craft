package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class SGBaseBlock1Big extends SGBaseBlock1 {
   static int[][] pattern = new int[][]{{1, 2, 1, 2, 1, 2, 1}, {1, 0, 0, 0, 0, 0, 1}, {2, 0, 0, 0, 0, 0, 2}, {1, 0, 0, 0, 0, 0, 1}, {1, 0, 0, 0, 0, 0, 1}, {2, 0, 0, 0, 0, 0, 2}, {1, 2, 1, 0, 1, 2, 1}};

   void checkForMerge(World world, BlockPos pos) {
      if (!this.isMerged(world, pos)) {
         Trans3 t = this.localToGlobalTransformation(world, pos);

         for(int i = -3; i <= 3; ++i) {
            for(int j = 0; j <= 6; ++j) {
               if (i != 0 || j != 0) {
                  BlockPos rp = t.p((double)i, (double)j, (double)0.0F).blockPos();
                  int type = this.getRingBlockType(world, rp);
                  int pat = pattern[6 - j][3 + i];
                  if (pat != 0 && type != pat) {
                     return;
                  }
               }
            }
         }

         SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
         te.setMerged(true);
         BaseBlockUtils.markWorldBlockForUpdate(world, pos);

         for(int i = -3; i <= 3; ++i) {
            for(int j = 0; j <= 6; ++j) {
               if (i != 0 || j != 0) {
                  BlockPos rp = t.p((double)i, (double)j, (double)0.0F).blockPos();
                  Block block = BaseBlockUtils.getWorldBlock(world, rp);
                  if (block instanceof SGRingBlock1) {
                     ((SGRingBlock1)block).mergeWith(world, rp, pos);
                  }
               }
            }
         }

         te.checkForLink((byte)0);
      }

   }
}
