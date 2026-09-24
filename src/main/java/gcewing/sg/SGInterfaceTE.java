package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class SGInterfaceTE extends BaseTileEntity {
   public SGBaseTE getBaseTE() {
      for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
         SGBaseTE te = SGBaseTE.get(this.worldObj, this.getPos().add(d.offsetX, d.offsetY, d.offsetZ));
         if (te != null && SGBaseTE.isAdjacentSlot(te.localToGlobalTransformation(), this.getPos())) {
            return te;
         }
      }
      return null;
   }

   public static Object[] prependArgs(Object... args) {
      int preLength = args.length - 1;
      Object[] post = (Object[])args[preLength];
      Object[] xargs = new Object[preLength + post.length];

      for(int i = 0; i < preLength; ++i) {
         xargs[i] = args[i];
      }

      for(int i = 0; i < post.length; ++i) {
         xargs[preLength + i] = post[i];
      }

      return xargs;
   }

   public void rebroadcastNetworkPacket(Object packet) {
   }

   public SGBaseTE requireBaseTE() {
      SGBaseTE te = this.getBaseTE();
      if (te != null && te.isMerged) {
         return te;
      } else {
         throw new IllegalArgumentException("No stargate connected to interface");
      }
   }

   public SGBaseTE requireIrisTE() {
      SGBaseTE te = this.requireBaseTE();
      if (te != null && te.hasIrisUpgrade) {
         return te;
      } else {
         throw new IllegalArgumentException("No iris fitted to stargate");
      }
   }

   String directionDescription(SGBaseTE te) {
      if (te.isConnected()) {
         return te.isInitiator ? "Outgoing" : "Incoming";
      } else {
         return "";
      }
   }

   public CIStargateState ciStargateState() {
      SGBaseTE te = this.getBaseTE();
      return te != null ? new CIStargateState(te.sgStateDescription(), te.numEngagedChevrons, this.directionDescription(te)) : new CIStargateState("Offline", 0, "");
   }

   public double ciEnergyAvailable() {
      SGBaseTE te = this.getBaseTE();
      return te != null ? te.availableEnergy() : (double)0.0F;
   }

   public double ciEnergyToDial(String address) {
      SGBaseTE te = this.requireBaseTE();

      try {
         address = SGAddressing.normalizeAddress(address);
         SGBaseTE dte = SGAddressing.findAddressedStargate(address, BaseBlockUtils.getTileEntityWorld(te));
         if (dte == null) {
            throw new IllegalArgumentException("No stargate at address " + address);
         } else {
            double distanceFactor = SGBaseTE.distanceFactorForCoordDifference(te, dte);
            return SGBaseTE.energyToOpen * distanceFactor;
         }
      } catch (SGAddressing.AddressingError e) {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   public String ciLocalAddress() {
      SGBaseTE te = this.getBaseTE();

      try {
         return te != null ? te.getHomeAddress() : "";
      } catch (SGAddressing.AddressingError e) {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   public String ciRemoteAddress() {
      SGBaseTE te = this.requireBaseTE();

      try {
         return te.connectedLocation != null ? SGAddressing.addressForLocation(te.connectedLocation) : "";
      } catch (SGAddressing.AddressingError e) {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   public void ciDial(String address) {
      SGBaseTE te = this.requireBaseTE();
      address = SGAddressing.normalizeAddress(address);
      String error = te.connect(address, (EntityPlayer)null);
      if (error != null) {
         throw new IllegalArgumentException(error);
      }
   }

   public void ciDisconnect() {
      SGBaseTE te = this.requireBaseTE();
      String error = te.attemptToDisconnect((EntityPlayer)null);
      if (error != null) {
         throw new IllegalArgumentException(error);
      }
   }

   public String ciIrisState() {
      SGBaseTE te = this.getBaseTE();
      return te != null && te.hasIrisUpgrade ? te.irisStateDescription() : "Offline";
   }

   public void ciOpenIris() {
      this.requireIrisTE().openIris();
   }

   public void ciCloseIris() {
      this.requireIrisTE().closeIris();
   }

   public void ciSendMessage(Object[] args) {
      SGBaseTE te = this.requireBaseTE();
      String error = te.sendMessage(args);
      if (error != null) {
         throw new IllegalArgumentException(error);
      }
   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
   }

   public static class CIStargateState {
      public String state;
      public int chevrons;
      public String direction;

      public CIStargateState(String state, int chevrons, String direction) {
         this.state = state;
         this.chevrons = chevrons;
         this.direction = direction;
      }
   }
}
