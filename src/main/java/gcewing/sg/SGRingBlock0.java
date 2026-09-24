package gcewing.sg;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SGRingBlock0 extends SGBlock<SGRingTE> {
   static final int numSubBlocks = 2;
   public static IProperty<Integer> VARIANT = PropertyInteger.create("variant", 0, 1);
   static String[] textures = new String[]{"stargateBlock0", "stargateRing0", "stargateChevron0"};
   static BaseMod.ModelSpec[] models = new BaseMod.ModelSpec[]{new BaseMod.ModelSpec("block/sg_ring_block.smeg", new String[]{"stargateBlock0", "stargateRing0"}), new BaseMod.ModelSpec("block/sg_ring_block.smeg", new String[]{"stargateBlock0", "stargateChevron0"})};
   static String[] subBlockTitles = new String[]{"Stargate Ring Block", "Stargate Chevron Block"};

   public SGRingBlock0() {
      super(Material.rock, SGRingTE.class);
      this.setHardness(1.5F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   protected void defineProperties() {
      super.defineProperties();
      this.addProperty(VARIANT);
   }

   public int getNumSubtypes() {
      return VARIANT.getAllowedValues().size();
   }

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return models[(Integer)state.getValue(VARIANT)];
   }

   protected String getRendererClassName() {
      return "SGRingBlock0Renderer";
   }

   public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
      return true;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
      return true;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      if (te.isMerged) {
         IBlockState baseState = BaseBlockUtils.getWorldBlockState(world, te.basePos);
         Block block = baseState.getBlock();
         if (block instanceof SGBaseBlock0) {
            ((SGBaseBlock0)block).onBlockActivated(world, te.basePos, baseState, player, side, cx, cy, cz);
         }

         return true;
      } else {
         return false;
      }
   }

   public SGBaseTE getBaseTE(IBlockAccess world, BlockPos pos) {
      SGRingTE rte = (SGRingTE)this.getTileEntity(world, pos);
      return rte != null ? rte.getBaseTE() : null;
   }

   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(int i = 0; i < 2; ++i) {
         list.add(new ItemStack(item, 1, i));
      }

   }

   public boolean isMerged(IBlockAccess world, BlockPos pos) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      return te != null && te.isMerged;
   }

   public void mergeWith(World world, BlockPos pos, BlockPos basePos) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      te.isMerged = true;
      te.basePos = basePos;
      BaseBlockUtils.markWorldBlockForUpdate(world, pos);
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

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      SGRingTE te = (SGRingTE)this.getTileEntity(world, pos);
      super.breakBlock(world, pos, state);
      if (te != null && te.isMerged) {
         this.updateBaseBlocks(world, pos, te);
      }

   }

   void updateBaseBlocks(World world, BlockPos pos, SGRingTE te) {
      for(int j = -6; j <= 0; ++j) {
         int reach = j == 0 ? 6 : 3;
         for(int i = -reach; i <= reach; ++i) {
            for(int k = -reach; k <= reach; ++k) {
               BlockPos bp = pos.add(i, j, k);
               Block block = BaseBlockUtils.getWorldBlock(world, bp);
               if (block instanceof SGBaseBlock0) {
                  SGBaseBlock0 base = (SGBaseBlock0)block;
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
}
