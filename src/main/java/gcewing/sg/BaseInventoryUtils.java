package gcewing.sg;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class BaseInventoryUtils {
   public static InventorySide inventorySide(IInventory base, int side) {
      return (InventorySide)(base instanceof ISidedInventory ? new SidedInventorySide((ISidedInventory)base, side) : new UnsidedInventorySide(base));
   }

   public static void clearInventory(IInventory inv) {
      int n = inv.getSizeInventory();

      for(int i = 0; i < n; ++i) {
         inv.setInventorySlotContents(i, (ItemStack)null);
      }

   }

   public abstract static class InventorySide {
      public int size;

      public abstract ItemStack get(int var1);

      public abstract boolean set(int var1, ItemStack var2);

      public abstract ItemStack extract(int var1);
   }

   public static class UnsidedInventorySide extends InventorySide {
      IInventory base;

      public UnsidedInventorySide(IInventory base) {
         this.base = base;
         this.size = base.getSizeInventory();
      }

      public ItemStack get(int slot) {
         return this.base.getStackInSlot(slot);
      }

      public boolean set(int slot, ItemStack stack) {
         this.base.setInventorySlotContents(slot, stack);
         return true;
      }

      public ItemStack extract(int slot) {
         return this.get(slot);
      }
   }

   public static class SidedInventorySide extends InventorySide {
      ISidedInventory base;
      int side;
      int[] slots;

      public SidedInventorySide(ISidedInventory base, int side) {
         this.base = base;
         this.side = side;
         this.slots = base.getAccessibleSlotsFromSide(side);
         this.size = this.slots.length;
      }

      public ItemStack get(int i) {
         return this.base.getStackInSlot(this.slots[i]);
      }

      public boolean set(int i, ItemStack stack) {
         int slot = this.slots[i];
         if (this.base.canInsertItem(slot, stack, this.side)) {
            this.base.setInventorySlotContents(slot, stack);
            return true;
         } else {
            return false;
         }
      }

      public ItemStack extract(int i) {
         int slot = this.slots[i];
         ItemStack stack = this.base.getStackInSlot(slot);
         return this.base.canExtractItem(slot, stack, this.side) ? stack : null;
      }
   }
}
