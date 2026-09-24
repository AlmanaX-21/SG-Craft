package gcewing.sg.gui;

import gcewing.sg.DHDTE;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class FuelSlot extends Slot {
   public FuelSlot(IInventory inv, int i, int x, int y) {
      super(inv, i, x, y);
   }

   public boolean isItemValid(ItemStack stack) {
      return DHDTE.isValidFuelItem(stack);
   }
}
