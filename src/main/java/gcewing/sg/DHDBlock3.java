package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DHDBlock3 extends DHDBlock0 {
   protected static String[] textures = new String[]{"dhd_top3", "dhd_side3", "stargateBlock3", "dhd_button_dim3"};
   protected static BaseMod.ModelSpec model;

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return model;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 3;
      this.checkForLink(world, pos);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      DHDTE cte = (DHDTE)this.getTileEntity(world, pos);
      super.breakBlock(world, pos, state);
      if (cte != null && cte.isLinkedToStargate) {
         SGBaseTE gte = cte.getLinkedStargateTE((byte)3);
         if (gte != null) {
            gte.clearLinkToController4();
         }
      }

   }

   public void checkForLink(World world, BlockPos pos) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 3;
      if (te != null) {
         te.checkForLink((byte)3);
      }

   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 3;
      this.checkForLink(world, pos);
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 3;
   }

   static {
      model = new BaseMod.ModelSpec("dhd.smeg", new Vector3((double)0.0F, (double)-0.5F, (double)0.0F), textures);
   }
}
