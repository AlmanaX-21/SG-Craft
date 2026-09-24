package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BaseItem extends Item implements BaseMod.IItem {
   public String[] getTextureNames() {
      return null;
   }

   public BaseMod.ModelSpec getModelSpec(ItemStack stack) {
      return null;
   }

   public int getNumSubtypes() {
      return 1;
   }

   public boolean getHasSubtypes() {
      return this.getNumSubtypes() > 1;
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      return this.onItemUse(stack, player, world, new BlockPos(x, y, z), BaseUtils.facings[side], hitX, hitY, hitZ);
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      return false;
   }
}
