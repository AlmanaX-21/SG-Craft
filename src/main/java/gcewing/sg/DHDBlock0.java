package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DHDBlock0 extends BaseBlock<DHDTE> {
   protected static String[] textures = new String[]{"dhd_top0", "dhd_side0", "stargateBlock0", "dhd_button_dim0"};
   protected static BaseMod.ModelSpec model;

   public DHDBlock0() {
      super(Material.rock, DHDTE.class);
      this.setHardness(1.5F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public String[] getTextureNames() {
      return textures;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return model;
   }

   public BaseBlock.IOrientationHandler getOrientationHandler() {
      return BaseOrientation.orient4WaysByState;
   }

   public int getRenderType() {
      return -1;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 0;
      this.checkForLink(world, pos);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 0;
      this.checkForLink(world, pos);
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 0;
   }

   public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
      return true;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      DHDTE cte = (DHDTE)this.getTileEntity(world, pos);
      super.breakBlock(world, pos, state);
      if (cte != null && cte.isLinkedToStargate) {
         SGBaseTE gte = cte.getLinkedStargateTE((byte)0);
         if (gte != null) {
            gte.clearLinkToController();
         }
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      SGGui id = (double)cy > (double)0.5F ? SGGui.SGController : SGGui.DHDFuel;
      SGCraft.mod.openGui(player, id, world, pos);
      return true;
   }

   public void checkForLink(World world, BlockPos pos) {
      DHDTE te = (DHDTE)this.getTileEntity(world, pos);
      te.typ = 0;
      if (te != null) {
         te.checkForLink((byte)0);
      }

   }

   static {
      model = new BaseMod.ModelSpec("dhd.smeg", new Vector3((double)0.0F, (double)-0.5F, (double)0.0F), textures);
   }
}
