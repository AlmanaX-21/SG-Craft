package gcewing.sg;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

public abstract class BaseTileEntity extends TileEntity implements BaseMod.ITileEntity {
   public byte side;
   public byte turn;
   public ForgeChunkManager.Ticket chunkTicket;
   protected boolean updateChunk;
   protected static Method getOrCreateChunkWatcher;
   protected static Field flagsYAreasToUpdate;

   public BlockPos getPos() {
      return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
   }

   public int getX() {
      return this.xCoord;
   }

   public int getY() {
      return this.yCoord;
   }

   public int getZ() {
      return this.zCoord;
   }

   public void setSide(int side) {
      this.side = (byte)side;
   }

   public void setTurn(int turn) {
      this.turn = (byte)turn;
   }

   public Trans3 localToGlobalRotation() {
      return this.localToGlobalTransformation(Vector3.zero);
   }

   public Trans3 localToGlobalTransformation() {
      return this.localToGlobalTransformation(Vector3.blockCenter((double)this.xCoord, (double)this.yCoord, (double)this.zCoord));
   }

   public Trans3 localToGlobalTransformation(Vector3 origin) {
      BlockPos pos = this.getPos();
      IBlockState state = BaseBlockUtils.getWorldBlockState(this.worldObj, pos);
      Block block = state.getBlock();
      return block instanceof BaseMod.IBlock ? ((BaseMod.IBlock)block).localToGlobalTransformation(this.worldObj, pos, state, origin) : new Trans3(origin);
   }

   public Packet getDescriptionPacket() {
      if (this.syncWithClient()) {
         NBTTagCompound nbt = new NBTTagCompound();
         super.writeToNBT(nbt);
         this.writeClientStateToNBT(nbt);
         if (this.updateChunk) {
            nbt.setBoolean("updateChunk", true);
            this.updateChunk = false;
         }

         return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
      } else {
         return null;
      }
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      NBTTagCompound nbt = pkt.func_148857_g();
      super.readFromNBT(nbt);
      this.readClientStateFromNBT(nbt);
      if (nbt.getBoolean("updateChunk")) {
         this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
      }

   }

   boolean syncWithClient() {
      return true;
   }

   public void markBlockForUpdate() {
      this.updateChunk = true;
      this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
   }

   public void markForUpdate() {
      if (!this.worldObj.isRemote) {
         PlayerManager pm = ((WorldServer)this.worldObj).getPlayerManager();
         Object watcher = BaseUtils.invokeMethod(pm, getOrCreateChunkWatcher, this.xCoord >> 4, this.zCoord >> 4, false);
         if (watcher != null) {
            int oldFlags = BaseUtils.getIntField(watcher, flagsYAreasToUpdate);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            BaseUtils.setIntField(watcher, flagsYAreasToUpdate, oldFlags);
         }
      }

   }

   public void playSoundEffect(String name, float volume, float pitch) {
      this.worldObj.playSoundEffect((double)this.xCoord + (double)0.5F, (double)this.yCoord + (double)0.5F, (double)this.zCoord + (double)0.5F, name, volume, pitch);
   }

   public void onAddedToWorld() {
   }

   public final void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.readPersistentStateFromNBT(nbt);
   }

   protected void readPersistentStateFromNBT(NBTTagCompound nbt) {
      this.side = nbt.getByte("side");
      this.turn = nbt.getByte("turn");
      this.readContentsFromNBT(nbt);
   }

   public void readFromItemStack(ItemStack stack) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt != null) {
         this.readFromItemStackNBT(nbt);
      }

   }

   public void readFromItemStackNBT(NBTTagCompound nbt) {
      this.readContentsFromNBT(nbt);
   }

   protected void readClientStateFromNBT(NBTTagCompound nbt) {
      this.readPersistentStateFromNBT(nbt);
   }

   public abstract void readContentsFromNBT(NBTTagCompound var1);

   public final void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      this.writePersistentStateToNBT(nbt);
   }

   protected void writePersistentStateToNBT(NBTTagCompound nbt) {
      if (this.side != 0) {
         nbt.setByte("side", this.side);
      }

      if (this.turn != 0) {
         nbt.setByte("turn", this.turn);
      }

      this.writeContentsToNBT(nbt);
   }

   public void writeToItemStackNBT(NBTTagCompound nbt) {
      this.writeContentsToNBT(nbt);
   }

   protected void writeClientStateToNBT(NBTTagCompound nbt) {
      this.writePersistentStateToNBT(nbt);
   }

   public abstract void writeContentsToNBT(NBTTagCompound var1);

   public void markChanged() {
      this.markDirty();
      this.markForUpdate();
   }

   public void markBlockChanged() {
      this.markDirty();
      this.markBlockForUpdate();
   }

   public void invalidate() {
      this.releaseChunkTicket();
      super.invalidate();
   }

   public void releaseChunkTicket() {
      if (this.chunkTicket != null) {
         ForgeChunkManager.releaseTicket(this.chunkTicket);
         this.chunkTicket = null;
      }

   }

   public static ItemStack blockStackWithTileEntity(Block block, int size, BaseTileEntity te) {
      return blockStackWithTileEntity(block, size, 0, te);
   }

   public static ItemStack blockStackWithTileEntity(Block block, int size, int meta, BaseTileEntity te) {
      ItemStack stack = new ItemStack(block, size, meta);
      if (te != null) {
         NBTTagCompound tag = new NBTTagCompound();
         te.writeToItemStackNBT(tag);
         stack.setTagCompound(tag);
      }

      return stack;
   }

   public ItemStack newItemStack(int size) {
      return blockStackWithTileEntity(this.getBlockType(), size, this);
   }

   public boolean canUpdate() {
      return this instanceof ITickable;
   }

   public void updateEntity() {
      ((ITickable)this).update();
   }

   static {
      getOrCreateChunkWatcher = BaseUtils.getMethodDef(PlayerManager.class, "getOrCreateChunkWatcher", "func_72690_a", Integer.TYPE, Integer.TYPE, Boolean.TYPE);
      flagsYAreasToUpdate = BaseUtils.getFieldDef(BaseUtils.classForName("net.minecraft.server.management.PlayerManager$PlayerInstance"), "flagsYAreasToUpdate", "field_73260_f");
   }
}
