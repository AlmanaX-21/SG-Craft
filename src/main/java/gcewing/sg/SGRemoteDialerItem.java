package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SGRemoteDialerItem extends BaseItem {
   public SGRemoteDialerItem() {
      this.setMaxStackSize(1);
   }

   public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
      if (p_77659_2_.isRemote) {
         setHeld(p_77659_1_, true, p_77659_3_.ticksExisted);
      } else {
         SGCraft.mod.openGui(p_77659_3_, SGGui.SG_REMOTE_DIALER, p_77659_2_, new BlockPos((int)p_77659_3_.posX, (int)p_77659_3_.posY, (int)p_77659_3_.posZ));
      }
      return p_77659_1_;
   }

   public static void setHeld(ItemStack stack, boolean held, long time) {
      NBTTagCompound tag = stack.getTagCompound();
      if (tag == null) {
         tag = new NBTTagCompound();
         stack.setTagCompound(tag);
      }
      tag.setBoolean("held", held);
      tag.setLong("time", time);
   }
}
