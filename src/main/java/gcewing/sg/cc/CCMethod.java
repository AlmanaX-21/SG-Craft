package gcewing.sg.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class CCMethod {
   public String name;
   public int nargs;

   abstract Object[] call(IComputerAccess var1, ILuaContext var2, Object var3, Object[] var4);

   public CCMethod(String name) {
      this(name, 0);
   }

   public CCMethod(String name, int nargs) {
      this.name = name;
      this.nargs = nargs;
   }

   public Object[] invoke(IComputerAccess cpu, ILuaContext ctx, Object target, Object[] args) throws LuaException {
      if (this.nargs >= 0 && args.length != this.nargs) {
         throw new LuaException(String.format("Wrong number of arguments to %s, expected %s, got %s", this.name, this.nargs, args.length));
      } else {
         return this.call(cpu, ctx, target, args);
      }
   }
}
