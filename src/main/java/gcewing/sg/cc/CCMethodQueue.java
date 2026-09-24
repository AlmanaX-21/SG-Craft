package gcewing.sg.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CCMethodQueue {
   public static CCMethodQueue instance;
   Queue<CCCall> items = new ConcurrentLinkedQueue();

   public static void init() {
      instance = new CCMethodQueue();
   }

   public static void onServerTick() {
      if (instance != null) {
         instance.tick();
      }

   }

   public Object[] invoke(IComputerAccess cpu, ILuaContext ctx, Object target, CCMethod method, Object[] args) throws LuaException, InterruptedException {
      CCCall item = new CCCall(cpu, ctx, target, method, args);
      this.items.add(item);
      item.lock.acquire();
      if (item.exception == null) {
         return item.result;
      } else {
         throw item.exception;
      }
   }

   void tick() {
      CCCall item;
      for(int n = this.items.size(); n-- > 0; item.lock.release()) {
         item = (CCCall)this.items.poll();
         if (item == null) {
            return;
         }

         try {
            item.result = item.method.invoke(item.cpu, item.ctx, item.target, item.args);
         } catch (LuaException e) {
            item.exception = e;
         } catch (Exception e) {
            item.exception = new LuaException(e.getMessage());
         }
      }

   }
}
