package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SGBaseBlock2 extends SGBaseBlock0 {
   protected static String[] textures = new String[]{"stargateBlock2", "stargateRing2", "stargateBase_front2"};
   protected static BaseMod.ModelSpec model;

   protected String getRendererClassName() {
      return "SGRingBlock2Renderer";
   }

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return model;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      te.setType((byte)2);
      this.checkForMerge(world, pos);
   }

   void checkForMerge(World world, BlockPos pos) {
      if (!this.isMerged(world, pos)) {
         Trans3 t = this.localToGlobalTransformation(world, pos);

         for(int i = -2; i <= 2; ++i) {
            for(int j = 0; j <= 4; ++j) {
               if (i != 0 || j != 0) {
                  BlockPos rp = t.p((double)i, (double)j, (double)0.0F).blockPos();
                  int type = this.getRingBlockType(world, rp);
                  int pat = pattern[4 - j][2 + i];
                  if (pat != 0 && type != pat) {
                     return;
                  }
               }
            }
         }

         SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
         te.setMerged(true);
         BaseBlockUtils.markWorldBlockForUpdate(world, pos);

         for(int i = -2; i <= 2; ++i) {
            for(int j = 0; j <= 4; ++j) {
               if (i != 0 || j != 0) {
                  BlockPos rp = t.p((double)i, (double)j, (double)0.0F).blockPos();
                  Block block = BaseBlockUtils.getWorldBlock(world, rp);
                  if (block instanceof SGRingBlock2) {
                     ((SGRingBlock2)block).mergeWith(world, rp, pos);
                  }
               }
            }
         }

         te.checkForLink((byte)2);
      }

   }

   int getRingBlockType(World world, BlockPos pos) {
      Block block = BaseBlockUtils.getWorldBlock(world, pos);
      if (block == Blocks.air) {
         return 0;
      } else {
         if (block == SGCraft.sgRingBlock2 && !SGCraft.sgRingBlock2.isMerged(world, pos)) {
            IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
            switch ((Integer)state.getValue(SGRingBlock2.VARIANT)) {
               case 0:
                  return 1;
               case 1:
                  return 2;
            }
         }

         return -1;
      }
   }

   void unmergeRingBlock(World world, BlockPos pos, BlockPos ringPos) {
      Block block = BaseBlockUtils.getWorldBlock(world, ringPos);
      if (block instanceof SGRingBlock2) {
         ((SGRingBlock2)block).unmergeFrom(world, ringPos, pos);
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      String Side = world.isRemote ? "Client" : "Server";
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      te.setType((byte)2);
      if (te != null && te.isMerged) {
         SGCraft.mod.openGui(player, SGGui.SGBase, world, pos);
         return true;
      } else {
         return false;
      }
   }

   public boolean isMerged(IBlockAccess world, BlockPos pos) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      return te != null && te.isMerged;
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      if (te != null) {
         te.onNeighborBlockChange();
      }

   }

   void dropUpgrades(World world, BlockPos pos) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      te.setType((byte)2);
      if (te != null) {
         if (te.hasChevronUpgrade) {
            this.spawnAsEntity(world, pos, new ItemStack(SGCraft.sgChevronUpgrade));
         }

         if (te.hasIrisUpgrade) {
            this.spawnAsEntity(world, pos, new ItemStack(te.barrierKind == BarrierKind.SHIELD ? SGCraft.sgShieldUpgrade : SGCraft.sgIrisUpgrade));
         }
      }

   }

   public void unmerge(World world, BlockPos pos) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      boolean goBang = false;
      if (te != null) {
         if (te.isMerged && te.state == SGState.Connected) {
            te.state = SGState.Idle;
            goBang = true;
         }

         te.disconnect();
         te.unlinkFromController();
         te.setMerged(false);
         BaseBlockUtils.markWorldBlockForUpdate(world, pos);
         this.unmergeRing(world, pos);
      }

      if (goBang && explosionRadius > 0) {
         this.explode(world, (new Vector3(pos)).add((double)0.5F, (double)2.5F, (double)0.5F), (double)explosionRadius);
      }

   }

   static {
      model = new BaseMod.ModelSpec("block/sg_base_block.smeg", textures);
   }
}
