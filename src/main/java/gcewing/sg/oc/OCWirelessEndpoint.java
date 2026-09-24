package gcewing.sg.oc;

import gcewing.sg.BaseConfiguration;
import gcewing.sg.SGBaseTE;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import net.minecraft.world.World;

public class OCWirelessEndpoint implements WirelessEndpoint {
   public static double forwardingStrength = (double)50.0F;
   SGBaseTE te;

   public static void configure(BaseConfiguration config) {
      forwardingStrength = config.getDouble("opencomputers", "wirelessRebroadcastStrength", forwardingStrength);
   }

   public OCWirelessEndpoint(SGBaseTE te) {
      this.te = te;
      Network.joinWirelessNetwork(this);
   }

   public void remove() {
      Network.leaveWirelessNetwork(this);
   }

   public int x() {
      return this.te.xCoord;
   }

   public int y() {
      return this.te.yCoord;
   }

   public int z() {
      return this.te.zCoord;
   }

   public World world() {
      return this.te.getWorldObj();
   }

   public void receivePacket(Packet packet, WirelessEndpoint sender) {
      if (packet.ttl() > 0) {
         SGBaseTE dte = this.te.getConnectedStargateTE();
         if (dte != null && dte.ocWirelessEndpoint != null) {
            Network.sendWirelessPacket(dte.ocWirelessEndpoint, forwardingStrength, packet.hop());
         }
      }

   }
}
