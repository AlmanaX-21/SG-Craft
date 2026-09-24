package gcewing.sg.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import gcewing.sg.SGInterfaceTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CCSGPeripheral implements IPeripheral {
   static CCMethod[] methods = new CCMethod[]{new SGMethod("stargateState") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         SGInterfaceTE.CIStargateState result = te.ciStargateState();
         return new Object[]{result.state, result.chevrons, result.direction};
      }
   }, new SGMethod("energyAvailable") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         return new Object[]{te.ciEnergyAvailable()};
      }
   }, new SGMethod("energyToDial", 1) {
      Object[] call(SGInterfaceTE te, Object[] args) {
         return new Object[]{te.ciEnergyToDial((String)args[0])};
      }
   }, new SGMethod("localAddress") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         return new Object[]{te.ciLocalAddress()};
      }
   }, new SGMethod("remoteAddress") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         return new Object[]{te.ciRemoteAddress()};
      }
   }, new SGMethod("dial", 1) {
      Object[] call(SGInterfaceTE te, Object[] args) {
         te.ciDial((String)args[0]);
         return null;
      }
   }, new SGMethod("disconnect") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         te.ciDisconnect();
         return null;
      }
   }, new SGMethod("irisState") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         return new Object[]{te.ciIrisState()};
      }
   }, new SGMethod("openIris") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         te.ciOpenIris();
         return null;
      }
   }, new SGMethod("closeIris") {
      Object[] call(SGInterfaceTE te, Object[] args) {
         te.ciCloseIris();
         return null;
      }
   }, new SGMethod("sendMessage", -1) {
      Object[] call(SGInterfaceTE te, Object[] args) {
         te.ciSendMessage(args);
         return null;
      }
   }};
   World worldObj;
   int xCoord;
   int yCoord;
   int zCoord;

   public CCSGPeripheral(TileEntity te) {
      this.worldObj = te.getWorldObj();
      this.xCoord = te.xCoord;
      this.yCoord = te.yCoord;
      this.zCoord = te.zCoord;
   }

   CCInterfaceTE getInterfaceTE() {
      TileEntity te = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
      return te instanceof CCInterfaceTE ? (CCInterfaceTE)te : null;
   }

   public String getType() {
      return "stargate";
   }

   public String[] getMethodNames() {
      String[] result = new String[methods.length];

      for(int i = 0; i < methods.length; ++i) {
         result[i] = methods[i].name;
      }

      return result;
   }

   public Object[] callMethod(IComputerAccess cpu, ILuaContext ctx, int method, Object[] args) throws LuaException, InterruptedException {
      if (method >= 0 && method < methods.length) {
         return CCMethodQueue.instance.invoke(cpu, ctx, this, methods[method], args);
      } else {
         throw new LuaException(String.format("Invalid method index %s", method));
      }
   }

   public void attach(IComputerAccess cpu) {
      CCInterfaceTE te = this.getInterfaceTE();
      if (te != null) {
         te.attachedComputers.add(cpu);
      }

   }

   public void detach(IComputerAccess cpu) {
      CCInterfaceTE te = this.getInterfaceTE();
      if (te != null) {
         te.attachedComputers.remove(cpu);
      }

   }

   public boolean equals(IPeripheral other) {
      return this == other;
   }
}
