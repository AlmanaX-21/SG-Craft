package gcewing.sg.te;

import gcewing.sg.BaseBlockUtils;
import gcewing.sg.BaseMod;
import gcewing.sg.BaseTileEntity;
import gcewing.sg.BlockPos;
import gcewing.sg.IBlockState;
import gcewing.sg.Trans3;
import gcewing.sg.Vector3;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class RingControllerTileEntity extends BaseTileEntity {
   private int delay = 100;
   private int tickTime = -1;
   private RingTileEntity callee;
   private RingTileEntity caller;

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      this.tick();
   }

   protected void tick() {
      if (this.tickTime == this.delay) {
         this.tickTime = -1;
         this.delay = 20;
         this.caller.startTeleportingTo(this.callee);
      } else if (this.tickTime > -1) {
         ++this.tickTime;
      }

   }

   public void startTeleportation(RingTileEntity caller, RingTileEntity callee) {
      this.caller = caller;
      this.callee = callee;
      this.tickTime = 0;
   }

   public void increaseDelay() {
      this.delay += 5;
   }

   public void decreaseDelay() {
      this.delay = Math.max(0, this.delay - 5);
   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
   }

   public BlockPos getPos() {
      return new BlockPos(this);
   }

   public Trans3 localToGlobalTransformation(Vector3 origin) {
      BlockPos pos = this.getPos();
      IBlockState state = BaseBlockUtils.getWorldBlockState(this.worldObj, pos);
      Block block = state.getBlock();
      return block instanceof BaseMod.IBlock ? ((BaseMod.IBlock)block).localToGlobalTransformation(this.worldObj, pos, state, origin) : new Trans3(origin);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
   }

   public boolean shouldRenderInPass(int pass) {
      return pass == 0;
   }
}
