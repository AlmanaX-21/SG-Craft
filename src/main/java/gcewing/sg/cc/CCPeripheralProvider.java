package gcewing.sg.cc;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CCPeripheralProvider implements IPeripheralProvider {
   public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
      TileEntity te = world.getTileEntity(x, y, z);
      return te instanceof CCInterfaceTE ? new CCSGPeripheral((CCInterfaceTE)te) : null;
   }
}
