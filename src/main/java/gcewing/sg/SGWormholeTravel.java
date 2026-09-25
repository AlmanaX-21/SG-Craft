package gcewing.sg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class SGWormholeTravel {
   static final int TICKS = 87;
   static boolean enabled = true;
   private static final Map<UUID, Transit> active = new HashMap<UUID, Transit>();

   static void begin(EntityPlayerMP player) {
      if (enabled && !active.containsKey(player.getUniqueID())) {
         active.put(player.getUniqueID(), new Transit(player));
         SGChannel.sendWormholeStart(player, TICKS);
      }
   }

   static void tick() {
      Iterator<Transit> iterator = active.values().iterator();
      while (iterator.hasNext()) {
         Transit transit = iterator.next();
         EntityPlayerMP player = transit.player;
         if (player.isDead || player.dimension != transit.dimension) {
            SGChannel.sendWormholeStop(player);
            iterator.remove();
         } else if (--transit.ticks <= 0) {
            iterator.remove();
         } else {
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
            player.fallDistance = 0;
            player.setPositionAndUpdate(transit.x, transit.y, transit.z);
         }
      }
   }

   static boolean protects(EntityPlayer player) {
      return active.containsKey(player.getUniqueID());
   }

   static void stop(EntityPlayer player) {
      active.remove(player.getUniqueID());
   }

   private static class Transit {
      final EntityPlayerMP player;
      final int dimension;
      final double x;
      final double y;
      final double z;
      int ticks = TICKS;

      Transit(EntityPlayerMP player) {
         this.player = player;
         this.dimension = player.dimension;
         this.x = player.posX;
         this.y = player.posY;
         this.z = player.posZ;
      }
   }
}
