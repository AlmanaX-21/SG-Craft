package gcewing.sg.oc;

import gcewing.sg.BaseBlockUtils;
import gcewing.sg.BaseContainer;
import gcewing.sg.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OCInterfaceContainer extends BaseContainer {
   static final int guiWidth = 176;
   static final int guiHeight = 125;
   static final int slotsLeft = 8;
   static final int slotsTop = 17;
   OCInterfaceTE te;

   public OCInterfaceContainer(EntityPlayer player, World world, BlockPos pos) {
      super(176, 125);
      this.te = (OCInterfaceTE)BaseBlockUtils.getWorldTileEntity(world, pos);
      this.addPlayerSlots(player);
      this.addSlots(this.te, 8, 17, 1, UpgradeSlot.class);
   }

   public static class UpgradeSlot extends Slot {
      public UpgradeSlot(IInventory inv, int i, int x, int y) {
         super(inv, i, x, y);
      }

      public boolean isItemValid(ItemStack stack) {
         return OCInterfaceTE.isNetworkCard(stack);
      }

      public int getSlotStackLimit() {
         return 1;
      }
   }
}
