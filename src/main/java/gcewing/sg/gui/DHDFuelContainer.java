package gcewing.sg.gui;

import gcewing.sg.BaseContainer;
import gcewing.sg.BlockPos;
import gcewing.sg.DHDTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DHDFuelContainer extends BaseContainer {
   static final int numFuelSlotColumns = 2;
   static final int fuelSlotsX = 174;
   static final int fuelSlotsY = 84;
   static final int playerSlotsX = 48;
   static final int playerSlotsY = 124;
   DHDTE te;

   public static DHDFuelContainer create(EntityPlayer player, World world, BlockPos pos) {
      DHDTE te = DHDTE.at(world, (BlockPos)pos);
      return te != null ? new DHDFuelContainer(player, te) : null;
   }

   public DHDFuelContainer(EntityPlayer player, DHDTE te) {
      super(256, 208);
      this.te = te;
      this.addFuelSlots();
      this.addPlayerSlots(player, 48, 124);
   }

   void addFuelSlots() {
      int b = 0;
      int n = 4;

      for(int i = 0; i < n; ++i) {
         int row = i / 2;
         int col = i % 2;
         int x = 174 + col * 18;
         int y = 84 + row * 18;
         this.addSlotToContainer(new FuelSlot(this.te, b + i, x, y));
      }

   }
}
