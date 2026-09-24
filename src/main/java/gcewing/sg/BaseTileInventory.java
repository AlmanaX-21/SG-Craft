package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BaseTileInventory extends BaseTileEntity implements IInventory, ISidedInventory {
   int[] allSlots;

   protected IInventory getInventory() {
      return null;
   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         NBTTagList list = nbt.getTagList("inventory", 10);
         int n = list.tagCount();

         for(int i = 0; i < n; ++i) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getInteger("slot");
            ItemStack stack = ItemStack.loadItemStackFromNBT(item);
            inventory.setInventorySlotContents(slot, stack);
         }
      }

   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         NBTTagList list = new NBTTagList();
         int n = inventory.getSizeInventory();

         for(int i = 0; i < n; ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
               NBTTagCompound item = new NBTTagCompound();
               item.setInteger("slot", i);
               stack.writeToNBT(item);
               list.appendTag(item);
            }
         }

         nbt.setTag("inventory", list);
      }

   }

   public boolean hasStackInSlot(int i) {
      ItemStack stack = this.getStackInSlot(i);
      return stack != null && stack.stackSize > 0;
   }

   public boolean damageStackInSlot(int i, int amount) {
      ItemStack stack = this.getStackInSlot(i);
      int damage = stack.getItemDamage() + amount;
      stack.setItemDamage(damage);
      if (damage >= stack.getMaxDamage()) {
         this.setInventorySlotContents(i, (ItemStack)null);
         return true;
      } else {
         this.onInventoryChanged(i);
         return false;
      }
   }

   void onInventoryChanged(int slot) {
      this.markDirty();
   }

   public int getSizeInventory() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getSizeInventory() : 0;
   }

   public ItemStack getStackInSlot(int slot) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getStackInSlot(slot) : null;
   }

   public ItemStack decrStackSize(int slot, int amount) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         ItemStack result = inventory.decrStackSize(slot, amount);
         this.onInventoryChanged(slot);
         return result;
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         ItemStack result = inventory.getStackInSlotOnClosing(slot);
         this.onInventoryChanged(slot);
         return result;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.setInventorySlotContents(slot, stack);
         this.onInventoryChanged(slot);
      }

   }

   public String getInventoryName() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getInventoryName() : "";
   }

   public int getInventoryStackLimit() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getInventoryStackLimit() : 0;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.isUseableByPlayer(player) : true;
   }

   public void openInventory() {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.openInventory();
      }

   }

   public void closeInventory() {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.closeInventory();
      }

   }

   public boolean isItemValidForSlot(int slot, ItemStack stack) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.isItemValidForSlot(slot, stack) : false;
   }

   public boolean hasCustomInventoryName() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.hasCustomInventoryName() : false;
   }

   public int[] getAccessibleSlotsFromSide(int side) {
      IInventory inventory = this.getInventory();
      if (inventory instanceof ISidedInventory) {
         return ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
      } else {
         if (this.allSlots == null) {
            int n = this.getSizeInventory();
            this.allSlots = new int[n];

            for(int i = 0; i < n; this.allSlots[i] = i++) {
            }
         }

         return this.allSlots;
      }
   }

   public boolean canInsertItem(int slot, ItemStack stack, int side) {
      IInventory inventory = this.getInventory();
      return inventory instanceof ISidedInventory ? ((ISidedInventory)inventory).canInsertItem(slot, stack, side) : true;
   }

   public boolean canExtractItem(int slot, ItemStack stack, int side) {
      IInventory inventory = this.getInventory();
      return inventory instanceof ISidedInventory ? ((ISidedInventory)inventory).canExtractItem(slot, stack, side) : true;
   }
}
