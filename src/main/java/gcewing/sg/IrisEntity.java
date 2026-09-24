package gcewing.sg;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class IrisEntity extends BaseEntity implements IEntityAdditionalSpawnData {
   BlockPos blockPos;

   public IrisEntity(World world) {
      super(world);
   }

   public IrisEntity(SGBaseTE te) {
      this(BaseBlockUtils.getTileEntityWorld(te));
      double radius = (double)2.0F;
      double thickness = 0.2;
      double cx = (double)0.0F;
      double cy = (double)2.0F;
      double cz = 0.1;
      AxisAlignedBB localBox = BaseUtils.newAxisAlignedBB(cx - radius, cy - radius, cz - thickness, cx + radius, cy + radius, cz + thickness);
      Trans3 t = te.localToGlobalTransformation();
      AxisAlignedBB globalBox = t.t(localBox);
      this.init(te.getPos(), globalBox);
   }

   void init(BlockPos pos, AxisAlignedBB box) {
      this.blockPos = pos;
      this.setPosition(box.minX, box.minY, box.minZ);
      this.setBoundingBox(box);
   }

   protected void entityInit() {
   }

   SGBaseTE getBaseTE() {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.blockPos);
      return te instanceof SGBaseTE ? (SGBaseTE)te : null;
   }

   public boolean canBeCollidedWith() {
      SGBaseTE te = this.getBaseTE();
      boolean result;
      if (te != null) {
         result = te.irisIsClosed();
      } else {
         result = false;
      }

      return result;
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return this.canBeCollidedWith() ? super.getEntityBoundingBox() : null;
   }

   public boolean canBePushed() {
      return false;
   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      int blockX = nbt.getInteger("blockX");
      int blockY = nbt.getInteger("blockY");
      int blockZ = nbt.getInteger("blockZ");
      BlockPos pos = new BlockPos(blockX, blockY, blockZ);
      double minX = nbt.getDouble("minX");
      double minY = nbt.getDouble("minY");
      double minZ = nbt.getDouble("minZ");
      double maxX = nbt.getDouble("maxX");
      double maxY = nbt.getDouble("maxY");
      double maxZ = nbt.getDouble("maxZ");
      AxisAlignedBB box = BaseUtils.newAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
      this.init(pos, box);
   }

   public void writeEntityToNBT(NBTTagCompound nbt) {
      nbt.setInteger("blockX", this.blockPos.getX());
      nbt.setInteger("blockY", this.blockPos.getY());
      nbt.setInteger("blockZ", this.blockPos.getZ());
      AxisAlignedBB box = this.getEntityBoundingBox();
      nbt.setDouble("minX", box.minX);
      nbt.setDouble("minY", box.minY);
      nbt.setDouble("minZ", box.minZ);
      nbt.setDouble("maxX", box.maxX);
      nbt.setDouble("maxY", box.maxY);
      nbt.setDouble("maxZ", box.maxZ);
   }

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   public void writeSpawnData(ByteBuf buffer) {
      try {
         DataOutput data = new ByteBufOutputStream(buffer);
         BaseUtils.writeBlockPos(data, this.blockPos);
         AxisAlignedBB box = this.getEntityBoundingBox();
         data.writeDouble(box.minX);
         data.writeDouble(box.minY);
         data.writeDouble(box.minZ);
         data.writeDouble(box.maxX);
         data.writeDouble(box.maxY);
         data.writeDouble(box.maxZ);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void readSpawnData(ByteBuf buffer) {
      try {
         DataInput data = new ByteBufInputStream(buffer);
         BlockPos pos = BaseUtils.readBlockPos(data);
         double minX = data.readDouble();
         double minY = data.readDouble();
         double minZ = data.readDouble();
         double maxX = data.readDouble();
         double maxY = data.readDouble();
         double maxZ = data.readDouble();
         AxisAlignedBB box = BaseUtils.newAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
         this.init(pos, box);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
