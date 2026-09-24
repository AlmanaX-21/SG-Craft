package gcewing.sg;

import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PowerBlock<TE extends PowerTE> extends BaseBlock<TE> {
   PowerTE lastRemovedTE;

   public PowerBlock(Class teClass, BaseBlock.IOrientationHandler orient) {
      super(SGCraft.machineMaterial, orient, teClass);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setStepSound(soundTypeMetal);
      this.setHarvestLevel("pickaxe", 0);
   }

   public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.lastRemovedTE = (PowerTE)this.getTileEntity(world, pos);
      super.breakBlock(world, pos, state);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      SGCraft.mod.openGui(player, SGGui.PowerUnit, world, pos);
      return true;
   }

   public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList();
      Item item = this.getItemDropped(state, ((World)world).rand, fortune);
      ItemStack stack = new ItemStack(item, 1);
      PowerTE te = this.lastRemovedTE;
      if (te != null && te.energyBuffer > (double)0.0F) {
         NBTTagCompound nbt = new NBTTagCompound();
         te.writeContentsToNBT(nbt);
         stack.setTagCompound(nbt);
         this.lastRemovedTE = null;
      }

      ret.add(stack);
      return ret;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
      PowerTE te = (PowerTE)this.getTileEntity(world, pos);
      NBTTagCompound nbt = stack.getTagCompound();
      if (te != null && nbt != null) {
         te.readContentsFromNBT(nbt);
      }

   }
}
