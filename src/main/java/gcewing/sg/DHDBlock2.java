package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DHDBlock2 extends DHDBlock0 {
   protected static String[] textures = new String[]{"dhd_top2", "dhd_side2", "stargateBlock2", "dhd_button_dim2"};
   protected static BaseMod.ModelSpec model;

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return model;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 2;
      this.checkForLink(world, pos);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      DHDTE cte = (DHDTE)this.getTileEntity(world, pos);
      super.breakBlock(world, pos, state);
      if (cte != null && cte.isLinkedToStargate) {
         SGBaseTE gte = cte.getLinkedStargateTE((byte)2);
         if (gte != null) {
            gte.clearLinkToController3();
         }
      }

   }

   public void checkForLink(World world, BlockPos pos) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 2;
      if (te != null) {
         te.checkForLink((byte)2);
      }

   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 2;
      this.checkForLink(world, pos);
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 2;
   }

   static {
      model = new BaseMod.ModelSpec("dhd.smeg", new Vector3((double)0.0F, (double)-0.5F, (double)0.0F), textures);
   }
}
