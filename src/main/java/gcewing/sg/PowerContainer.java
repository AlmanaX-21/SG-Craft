package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PowerContainer extends BaseContainer {
   PowerTE te;

   public static PowerContainer create(EntityPlayer player, World world, BlockPos pos) {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
      return te instanceof PowerTE ? new PowerContainer(player, (PowerTE)te) : null;
   }

   public PowerContainer(EntityPlayer player, PowerTE te) {
      super(128, 64);
      this.te = te;
   }
}
