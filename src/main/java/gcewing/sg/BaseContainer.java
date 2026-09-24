package gcewing.sg;

import java.lang.reflect.Constructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseContainer extends Container {
   int xSize;
   int ySize;
   SlotRange playerSlotRange;
   SlotRange containerSlotRange;

   public BaseContainer(int width, int height) {
      this.xSize = width;
      this.ySize = height;
   }

   public BaseContainer(int width, int height, EntityPlayer player) {
      this(width, height);
      this.addPlayerSlots(player);
   }

   protected void beginContainerSlots() {
      this.containerSlotRange = new SlotRange();
   }

   protected void endContainerSlots() {
      this.containerSlotRange.end();
   }

   protected void beginPlayerSlots() {
      this.playerSlotRange = new SlotRange();
   }

   protected void endPlayerSlots() {
      this.playerSlotRange.end();
   }

   public void addPlayerSlots(EntityPlayer player) {
      this.addPlayerSlots(player, (this.xSize - 160) / 2, this.ySize - 82);
   }

   public void addPlayerSlots(EntityPlayer player, int x, int y) {
      this.beginPlayerSlots();
      InventoryPlayer inventory = player.inventory;

      for(int var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(inventory, var4 + var3 * 9 + 9, x + var4 * 18, y + var3 * 18));
         }
      }

      for(int var3 = 0; var3 < 9; ++var3) {
         this.addSlotToContainer(new Slot(inventory, var3, x + var3 * 18, y + 58));
      }

      this.endPlayerSlots();
   }

   public void addPlayerSlotsRotated(EntityPlayer player, int x, int y) {
      this.beginPlayerSlots();
      InventoryPlayer inventory = player.inventory;

      for(int var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(inventory, var4 + var3 * 9 + 9, x + 58 - var3 * 18, y + var4 * 18));
         }
      }

      for(int var3 = 0; var3 < 9; ++var3) {
         this.addSlotToContainer(new Slot(inventory, var3, x, y + var3 * 18));
      }

      this.endPlayerSlots();
   }

   public SlotRange addSlots(IInventory inventory, int x, int y, int numRows) {
      return this.addSlots(inventory, 0, inventory.getSizeInventory(), x, y, numRows);
   }

   public SlotRange addSlots(IInventory inventory, int x, int y, int numRows, Class slotClass) {
      return this.addSlots(inventory, 0, inventory.getSizeInventory(), x, y, numRows, slotClass);
   }

   public SlotRange addSlots(IInventory inventory, int firstSlot, int numSlots, int x, int y, int numRows) {
      return this.addSlots(inventory, firstSlot, numSlots, x, y, numRows, Slot.class);
   }

   public SlotRange addSlots(IInventory inventory, int firstSlot, int numSlots, int x, int y, int numRows, Class slotClass) {
      SlotRange range = new SlotRange();

      try {
         Constructor slotCon = slotClass.getConstructor(IInventory.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
         int numCols = (numSlots + numRows - 1) / numRows;

         for(int i = 0; i < numSlots; ++i) {
            int row = i / numCols;
            int col = i % numCols;
            this.addSlotToContainer((Slot)slotCon.newInstance(inventory, firstSlot + i, x + col * 18, y + row * 18));
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      range.end();
      return range;
   }

   public boolean canInteractWith(EntityPlayer var1) {
      return true;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < this.crafters.size(); ++i) {
         ICrafting crafter = (ICrafting)this.crafters.get(i);
         this.sendStateTo(crafter);
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer player, int index) {
      ItemStack result = null;
      Slot slot = (Slot)this.inventorySlots.get(index);
      ItemStack stack = slot.getStack();
      if (slot != null && slot.getHasStack()) {
         SlotRange destRange = this.transferSlotRange(index, stack);
         if (destRange != null) {
            result = stack.copy();
            if (!this.mergeItemStackIntoRange(stack, destRange)) {
               return null;
            }

            if (stack.stackSize == 0) {
               slot.putStack((ItemStack)null);
            } else {
               slot.onSlotChanged();
            }
         }
      }

      return result;
   }

   protected boolean mergeItemStackIntoRange(ItemStack stack, SlotRange range) {
      return this.mergeItemStack(stack, range.firstSlot, range.firstSlot + range.numSlots, range.reverseMerge);
   }

   protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean reverse) {
      boolean result = false;
      int n = endSlot - startSlot;
      if (stack.isStackable()) {
         for(int i = 0; i < n && stack.stackSize > 0; ++i) {
            int k = reverse ? endSlot - 1 - i : startSlot + i;
            Slot slot = (Slot)this.inventorySlots.get(k);
            ItemStack slotStack = slot.getStack();
            if (slotStack != null && slotStack.isStackable() && slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, slotStack) && this.transferToSlot(stack, slot)) {
               result = true;
            }
         }
      }

      for(int i = 0; i < n && stack.stackSize > 0; ++i) {
         int k = reverse ? endSlot - 1 - i : startSlot + i;
         Slot slot = (Slot)this.inventorySlots.get(k);
         if (!slot.getHasStack() && slot.isItemValid(stack)) {
            ItemStack newStack = stack.copy();
            newStack.stackSize = 0;
            slot.putStack(newStack);
            if (this.transferToSlot(stack, slot)) {
               result = true;
            }
         }
      }

      return result;
   }

   protected boolean transferToSlot(ItemStack stack, Slot slot) {
      ItemStack slotStack = slot.getStack();
      int slotLimit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
      int oldSlotSize = slotStack.stackSize;
      int newSlotSize = Math.min(oldSlotSize + stack.stackSize, slotLimit);
      int transferSize = newSlotSize - oldSlotSize;
      if (transferSize > 0) {
         stack.stackSize -= transferSize;
         slotStack.stackSize += transferSize;
         slot.onSlotChanged();
         return true;
      } else {
         return false;
      }
   }

   protected SlotRange transferSlotRange(int srcSlotIndex, ItemStack stack) {
      return null;
   }

   void sendStateTo(ICrafting crafter) {
   }

   public void updateProgressBar(int i, int value) {
   }

   public class SlotRange {
      public int firstSlot;
      public int numSlots;
      public boolean reverseMerge;

      public SlotRange() {
         this.firstSlot = BaseContainer.this.inventorySlots.size();
      }

      public void end() {
         this.numSlots = BaseContainer.this.inventorySlots.size() - this.firstSlot;
      }

      public boolean contains(int slot) {
         return slot >= this.firstSlot && slot < this.firstSlot + this.numSlots;
      }

      public String toString() {
         return String.format("SlotRange(%s to %s)", this.firstSlot, this.firstSlot + this.numSlots - 1);
      }
   }
}
