package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class SGBaseBlock0 extends SGBlock<SGBaseTE> {
   static boolean debugMerge = false;
   static int explosionRadius = 10;
   static boolean fieryExplosion = true;
   static boolean smokyExplosion = true;
   static int[][] pattern = new int[][]{{2, 1, 2, 1, 2}, {1, 0, 0, 0, 1}, {2, 0, 0, 0, 2}, {1, 0, 0, 0, 1}, {2, 1, 0, 1, 2}};
   protected static String[] textures = new String[]{"stargateBlock0", "stargateRing0", "stargateBase_front0"};
   protected static BaseMod.ModelSpec model;

   public static void configure(BaseConfiguration config) {
      explosionRadius = config.getInteger("stargate", "explosionRadius", explosionRadius);
      fieryExplosion = config.getBoolean("stargate", "explosionFlame", fieryExplosion);
      smokyExplosion = config.getBoolean("stargate", "explosionSmoke", smokyExplosion);
   }

   public SGBaseBlock0() {
      super(Material.rock, SGBaseTE.class);
      this.setHardness(1.5F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
      return true;
   }

   public boolean canRenderInPass(int pass) {
      ForgeHooksClient.setRenderPass(pass);
      return true;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderBlockPass() {
      return 1;
   }

   public BaseBlock.IOrientationHandler getOrientationHandler() {
      return BaseOrientation.orientStargateByState;
   }

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return model;
   }

   public SGBaseTE getBaseTE(IBlockAccess world, BlockPos pos) {
      return (SGBaseTE)this.getTileEntity(world, pos);
   }

   protected String getRendererClassName() {
      return "SGRingBlock0Renderer";
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
      return true;
   }

   public boolean isMerged(IBlockAccess world, BlockPos pos) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      return te != null && te.isMerged;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      te.setType((byte)0);
      this.checkForMerge(world, pos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      String Side = world.isRemote ? "Client" : "Server";
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      te.setType((byte)0);
      if (te != null && te.isMerged) {
         SGCraft.mod.openGui(player, SGGui.SGBase, world, pos);
         return true;
      } else {
         return false;
      }
   }

   public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      if (te != null) {
         te.onNeighborBlockChange();
      }

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
                  if (block instanceof SGRingBlock0) {
                     ((SGRingBlock0)block).mergeWith(world, rp, pos);
                  }
               }
            }
         }

         te.checkForLink((byte)0);
      }

   }

   int getRingBlockType(World world, BlockPos pos) {
      Block block = BaseBlockUtils.getWorldBlock(world, pos);
      if (block == Blocks.air) {
         return 0;
      } else {
         if (block == SGCraft.sgRingBlock0 && !SGCraft.sgRingBlock0.isMerged(world, pos)) {
            IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
            switch ((Integer)state.getValue(SGRingBlock0.VARIANT)) {
               case 0:
                  return 1;
               case 1:
                  return 2;
            }
         }

         return -1;
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      SGBaseTE gate = this.getTileEntity(world, pos);
      if (!world.isRemote && gate != null) {
         SGGateRegistry.retire(gate);
      }
      this.unmerge(world, pos);
      this.dropUpgrades(world, pos);
      super.breakBlock(world, pos, state);
   }

   void dropUpgrades(World world, BlockPos pos) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
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

   void explode(World world, Vector3 p, double s) {
      world.newExplosion((Entity)null, p.x, p.y, p.z, (float)s, fieryExplosion, smokyExplosion);
   }

   void unmergeRing(World world, BlockPos pos) {
      for(int i = -6; i <= 6; ++i) {
         for(int j = 0; j <= 6; ++j) {
            for(int k = -6; k <= 6; ++k) {
               this.unmergeRingBlock(world, pos, pos.add(i, j, k));
            }
         }
      }

   }

   void unmergeRingBlock(World world, BlockPos pos, BlockPos ringPos) {
      Block block = BaseBlockUtils.getWorldBlock(world, ringPos);
      if (block instanceof SGRingBlock0) {
         ((SGRingBlock0)block).unmergeFrom(world, ringPos, pos);
      }

   }

   public boolean canProvidePower() {
      return true;
   }

   public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getWeakPower(world, pos, state, side);
   }

   public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
      SGBaseTE te = (SGBaseTE)this.getTileEntity(world, pos);
      return te != null && te.state != SGState.Idle ? 15 : 0;
   }

   static {
      model = new BaseMod.ModelSpec("block/sg_base_block.smeg", textures);
   }
}
