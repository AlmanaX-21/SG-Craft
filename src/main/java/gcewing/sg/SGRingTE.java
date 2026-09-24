package gcewing.sg;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class SGRingTE extends BaseTileEntity {
   public boolean isMerged;
   public BlockPos basePos = new BlockPos(0, 0, 0);

   public void readContentsFromNBT(NBTTagCompound nbt) {
      this.isMerged = nbt.getBoolean("isMerged");
      int baseX = nbt.getInteger("baseX");
      int baseY = nbt.getInteger("baseY");
      int baseZ = nbt.getInteger("baseZ");
      this.basePos = new BlockPos(baseX, baseY, baseZ);
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("isMerged", this.isMerged);
      nbt.setInteger("baseX", this.basePos.getX());
      nbt.setInteger("baseY", this.basePos.getY());
      nbt.setInteger("baseZ", this.basePos.getZ());
   }

   public SGBaseTE getBaseTE() {
      if (this.isMerged) {
         TileEntity bte = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.basePos);
         if (bte instanceof SGBaseTE) {
            return (SGBaseTE)bte;
         }
      }

      return null;
   }
}
