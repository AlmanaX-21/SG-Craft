package gcewing.sg;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

class BlockRef {
   public IBlockAccess worldObj;
   BlockPos pos;

   public BlockRef(TileEntity te) {
      this(BaseBlockUtils.getTileEntityWorld(te), BaseBlockUtils.getTileEntityPos(te));
   }

   public BlockRef(IBlockAccess world, BlockPos pos) {
      this.worldObj = world;
      this.pos = pos;
   }

   public TileEntity getTileEntity() {
      return BaseBlockUtils.getWorldTileEntity(this.worldObj, this.pos);
   }
}
