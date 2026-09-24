package gcewing.sg;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class SGBlock<TE extends TileEntity> extends BaseBlock<TE> implements ISGBlock {
   public SGBlock(Material material, Class<TE> teClass) {
      super(material, teClass);
   }

   public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
      if (player.capabilities.isCreativeMode && this.isConnected(world, new BlockPos(x, y, z))) {
         if (world.isRemote) {
            SGBaseTE.sendChatMessage(player, "Disconnect stargate before breaking");
         }

         return false;
      } else {
         return super.removedByPlayer(world, player, x, y, z);
      }
   }

   boolean isConnected(World world, BlockPos pos) {
      SGBaseTE bte = this.getBaseTE(world, pos);
      return bte != null && bte.isConnected();
   }
}
