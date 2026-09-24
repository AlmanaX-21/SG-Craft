package gcewing.sg;

import net.minecraft.tileentity.TileEntity;

public interface IComputerInterface {
   void postEvent(TileEntity var1, String var2, Object... var3);
}
