package gcewing.sg;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

class CamouflageSlot extends Slot {
   public CamouflageSlot(IInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean isItemValid(ItemStack itemstack) {
      return itemstack.getItem() instanceof ItemBlock;
   }

   public int getSlotStackLimit() {
      return 1;
   }
}
