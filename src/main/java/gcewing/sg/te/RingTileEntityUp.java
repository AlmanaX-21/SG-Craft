package gcewing.sg.te;

import net.minecraft.init.Blocks;

public class RingTileEntityUp extends RingTileEntity {
   protected int groundDistance = -1;

   protected void startTeleportSequenceAnimation() {
      super.startTeleportSequenceAnimation();
      this.groundDistance = this.computeGroundDistance();
   }

   public int computeGroundDistance() {
      for(int y = this.yCoord - 2; y >= 0; --y) {
         if (!this.getWorldObj().getBlock(this.xCoord, y, this.zCoord).equals(Blocks.air)) {
            return this.yCoord - y - 2;
         }
      }

      return -1;
   }

   public int getGroundDistance() {
      return this.groundDistance;
   }
}
