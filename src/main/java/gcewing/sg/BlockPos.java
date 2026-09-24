package gcewing.sg;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class BlockPos {
   public int x;
   public int y;
   public int z;

   public BlockPos(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public BlockPos(TileEntity te) {
      this(te.xCoord, te.yCoord, te.zCoord);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public BlockPos offset(EnumFacing dir) {
      return new BlockPos(this.x + dir.getFrontOffsetX(), this.y + dir.getFrontOffsetY(), this.z + dir.getFrontOffsetZ());
   }

   public boolean equals(BlockPos other) {
      return this.x == other.x && this.y == other.y && this.z == other.z;
   }

   public String toString() {
      return String.format("(%s,%s,%s)", this.x, this.y, this.z);
   }
}
