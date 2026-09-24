package gcewing.sg.cc;

import dan200.computercraft.api.peripheral.IComputerAccess;
import gcewing.sg.IComputerInterface;
import gcewing.sg.SGInterfaceTE;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;

public class CCInterfaceTE extends SGInterfaceTE implements IComputerInterface {
   Set<IComputerAccess> attachedComputers = new HashSet();

   public void postEvent(TileEntity source, String name, Object... args) {
      for(IComputerAccess cpu : this.attachedComputers) {
         cpu.queueEvent(name, prependArgs(new Object[]{cpu.getAttachmentName(), args}));
      }

   }
}
