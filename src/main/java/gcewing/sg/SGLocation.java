package gcewing.sg;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SGLocation {
   public int dimension;
   public BlockPos pos;

   public SGLocation(TileEntity te) {
      this(BaseUtils.getWorldDimensionId(BaseBlockUtils.getTileEntityWorld(te)), BaseBlockUtils.getTileEntityPos(te));
   }

   public SGLocation(int dimension, BlockPos pos) {
      this.dimension = dimension;
      this.pos = pos;
   }

   public SGLocation(NBTTagCompound nbt) {
      this.dimension = nbt.getInteger("dimension");
      int x = nbt.getInteger("x");
      int y = nbt.getInteger("y");
      int z = nbt.getInteger("z");
      this.pos = new BlockPos(x, y, z);
   }

   NBTTagCompound toNBT() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("dimension", this.dimension);
      nbt.setInteger("x", this.pos.getX());
      nbt.setInteger("y", this.pos.getY());
      nbt.setInteger("z", this.pos.getZ());
      return nbt;
   }

   SGBaseTE getStargateTE() {
      World world = SGAddressing.getWorld(this.dimension);
      if (world == null) {
         return null;
      } else {
         TileEntity te = BaseBlockUtils.getWorldTileEntity(world, this.pos);
         return te instanceof SGBaseTE ? (SGBaseTE)te : null;
      }
   }
}
