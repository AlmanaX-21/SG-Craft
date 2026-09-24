package gcewing.sg.te;

import gcewing.sg.BaseBlockUtils;
import gcewing.sg.BaseMod;
import gcewing.sg.BaseTileEntity;
import gcewing.sg.BlockPos;
import gcewing.sg.IBlockState;
import gcewing.sg.RingBase;
import gcewing.sg.Trans3;
import gcewing.sg.Vector3;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

public class RingTileEntity extends BaseTileEntity {
   private RingTileEntity src = null;
   private RingTileEntity dest = null;
   private int tickTime = -1;
   public float animationStartTime = 0.0F;
   public boolean shouldStartAnimation = false;

   public boolean isInUse() {
      return this.src != null || this.dest != null;
   }

   public void startTeleportingTo(RingTileEntity dest) {
      if (dest != null && !this.isInUse() && !dest.isInUse()) {
         this.dest = dest;
         this.startTeleportSequence();
         dest.startTeleportedFrom(this);
      }

      this.markChanged();
   }

   public void startTeleportedFrom(RingTileEntity src) {
      this.src = src;
      this.startTeleportSequence();
      this.markChanged();
   }

   protected void startTeleportSequence() {
      this.tickTime = 0;
      this.playSoundEffect("sgcraft:ring", 3.0F, 1.0F);
   }

   protected void endTeleportSequence() {
      this.tickTime = -1;
      this.src = null;
      this.dest = null;
   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      this.tick();
   }

   protected void tick() {
      if (this.tickTime > -1) {
         if (this.dest != null && this.tickTime == 60) {
            this.teleportEntities();
         }

         if (this.tickTime >= 124) {
            this.endTeleportSequence();
            this.markChanged();
         } else {
            ++this.tickTime;
         }
      }

   }

   protected void teleportEntities() {
      List<Entity> entities = null;
      if (this instanceof RingTileEntityUp) {
         int gd = ((RingTileEntityUp)this).computeGroundDistance();
         entities = this.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.getX() - 2), (double)(this.getY() - 2 - gd), (double)(this.getZ() - 2), (double)(this.getX() + 2), (double)(this.getY() - 2 - gd + 3), (double)(this.getZ() + 2)));
      } else {
         entities = this.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.getX() - 2), (double)(this.getY() + 1), (double)(this.getZ() - 2), (double)(this.getX() + 2), (double)(this.getY() + 3), (double)(this.getZ() + 2)));
      }

      for(Entity e : entities) {
         int gd = 0;
         if (this.dest instanceof RingTileEntityUp) {
            gd = ((RingTileEntityUp)this.dest).computeGroundDistance();
            gd += 3;
         }

         if (e instanceof EntityPlayer) {
            ((EntityPlayer)e).setPositionAndUpdate((double)this.dest.xCoord, (double)(this.dest.yCoord + 2 - gd), (double)this.dest.zCoord);
         } else {
            e.setPosition((double)this.dest.xCoord, (double)(this.dest.yCoord + 2 - gd), (double)this.dest.zCoord);
         }
      }

   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
      this.tickTime = nbt.getInteger("tickTime");
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      nbt.setInteger("tickTime", this.tickTime);
   }

   public int getTickTime() {
      return this.tickTime;
   }

   public Trans3 localToGlobalTransformation(Vector3 origin) {
      BlockPos pos = this.getPos();
      IBlockState state = BaseBlockUtils.getWorldBlockState(this.worldObj, pos);
      Block block = state.getBlock();
      return block instanceof BaseMod.IBlock ? ((BaseMod.IBlock)block).localToGlobalTransformation(this.worldObj, pos, state, origin) : new Trans3(origin);
   }

   public RingTileEntity getNextRingUp() {
      for(int y = this.yCoord + 1; y < 255; ++y) {
         if (this.getWorldObj().getBlock(this.xCoord, y, this.zCoord) instanceof RingBase) {
            return (RingTileEntity)this.getWorldObj().getTileEntity(this.xCoord, y, this.zCoord);
         }
      }

      return null;
   }

   public RingTileEntity getNextRingDown() {
      for(int y = this.yCoord - 1; y >= 0; --y) {
         if (this.getWorldObj().getBlock(this.xCoord, y, this.zCoord) instanceof RingBase) {
            return (RingTileEntity)this.getWorldObj().getTileEntity(this.xCoord, y, this.zCoord);
         }
      }

      return null;
   }

   public RingTileEntity getLastRingUp() {
      RingTileEntity tile = null;

      for(int y = this.yCoord + 1; y < 255; ++y) {
         if (this.getWorldObj().getBlock(this.xCoord, y, this.zCoord) instanceof RingBase) {
            tile = (RingTileEntity)this.getWorldObj().getTileEntity(this.xCoord, y, this.zCoord);
         }
      }

      return tile;
   }

   public RingTileEntity getLastRingDown() {
      RingTileEntity tile = null;

      for(int y = this.yCoord - 1; y >= 0; --y) {
         if (this.getWorldObj().getBlock(this.xCoord, y, this.zCoord) instanceof RingBase) {
            tile = (RingTileEntity)this.getWorldObj().getTileEntity(this.xCoord, y, this.zCoord);
         }
      }

      return tile;
   }

   protected void startTeleportSequenceAnimation() {
      this.shouldStartAnimation = true;
   }

   protected void endTeleportSequenceAnimtion() {
      this.animationStartTime = 0.0F;
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      int lastTickTime = this.tickTime;
      super.onDataPacket(net, pkt);
      if (this.tickTime > lastTickTime) {
         this.startTeleportSequenceAnimation();
      } else if (this.tickTime != lastTickTime && this.tickTime == -1) {
         this.endTeleportSequenceAnimtion();
      }

   }

   public AxisAlignedBB getRenderBoundingBox() {
      float[] size = new float[]{10.0F, 10.0F, 10.0F};
      return AxisAlignedBB.getBoundingBox((double)((float)this.xCoord - size[0]), (double)((float)this.yCoord - size[1]), (double)((float)this.zCoord - size[2]), (double)((float)this.xCoord + size[0]), (double)((float)this.yCoord + size[1]), (double)((float)this.zCoord + size[2]));
   }

   public boolean shouldRenderInPass(int pass) {
      return pass == 0;
   }
}
