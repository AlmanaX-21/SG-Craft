package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SGRingBlock2 extends SGRingBlock0 {
   static String[] textures = new String[]{"stargateBlock2", "stargateRing2", "stargateChevron2"};
   static BaseMod.ModelSpec[] models = new BaseMod.ModelSpec[]{new BaseMod.ModelSpec("block/sg_ring_block.smeg", new String[]{"stargateBlock2", "stargateRing2"}), new BaseMod.ModelSpec("block/sg_ring_block.smeg", new String[]{"stargateBlock2", "stargateChevron2"})};

   protected String getRendererClassName() {
      return "SGRingBlock2Renderer";
   }

   public String[] getTextureNames() {
      return textures;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      if (te.isMerged) {
         IBlockState baseState = BaseBlockUtils.getWorldBlockState(world, te.basePos);
         Block block = baseState.getBlock();
         if (block instanceof SGBaseBlock2) {
            ((SGBaseBlock2)block).onBlockActivated(world, te.basePos, baseState, player, side, cx, cy, cz);
         }

         return true;
      } else {
         return false;
      }
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return models[(Integer)state.getValue(VARIANT)];
   }

   void updateBaseBlocks(World world, BlockPos pos, SGRingTE te) {
      for(int j = -6; j <= 0; ++j) {
         int reach = j == 0 ? 6 : 3;
         for(int i = -reach; i <= reach; ++i) {
            for(int k = -reach; k <= reach; ++k) {
               BlockPos bp = pos.add(i, j, k);
               Block block = BaseBlockUtils.getWorldBlock(world, bp);
               if (block instanceof SGBaseBlock2) {
                  SGBaseBlock2 base = (SGBaseBlock2)block;
                  if (!te.isMerged) {
                     base.checkForMerge(world, bp);
                  } else if (te.basePos.equals(bp)) {
                     base.unmerge(world, bp);
                  }
               }
            }
         }
      }

   }

   public void unmergeFrom(World world, BlockPos pos, BlockPos basePos) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      if (te.isMerged && te.basePos.equals(basePos)) {
         te.isMerged = false;
         te.markBlockChanged();
      }

   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      this.updateBaseBlocks(world, pos, te);
   }
}
