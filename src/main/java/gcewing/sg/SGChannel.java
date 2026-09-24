package gcewing.sg;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class SGChannel extends BaseDataChannel {
   protected static BaseDataChannel channel;
   static int remoteRange = 20;
   private static final Queue<Runnable> remoteActions = new ConcurrentLinkedQueue<Runnable>();
   private static final Queue<RemoteUpdate> remoteUpdates = new ConcurrentLinkedQueue<RemoteUpdate>();
   private static final Queue<Integer> wormholeStarts = new ConcurrentLinkedQueue<Integer>();

   public SGChannel(String name) {
      super(name);
      channel = this;
   }

   public static void sendRemoteAction(String action, String address, String name) {
      BaseDataChannel.ChannelOutput data = channel.openServer("RemoteAction");
      data.writeUTF(action);
      data.writeUTF(address);
      data.writeUTF(name);
      data.close();
   }

   @BaseDataChannel.ServerMessageHandler("RemoteAction")
   public void handleRemoteAction(final EntityPlayer player, BaseDataChannel.ChannelInput data) {
      final String action = data.readUTF();
      final String address = data.readUTF();
      final String name = data.readUTF();
      if (action.length() > 16 || address.length() > 16 || name.length() > 32) {
         return;
      }
      remoteActions.add(new Runnable() {
         public void run() {
            processRemoteAction(player, action, address, name);
         }
      });
   }

   public static void processRemoteActions() {
      Runnable action;
      while ((action = remoteActions.poll()) != null) {
         action.run();
      }
   }

   static void processRemoteAction(EntityPlayer player, String action, String address, String name) {
      ItemStack held = player.getCurrentEquippedItem();
      if (held == null || held.getItem() != SGCraft.remoteDialer) {
         sendRemoteStatus(player, "Hold the Remote Dialer");
         return;
      }
      if (action.equals("snapshot")) {
         sendAddressBook(player);
         return;
      }
      NBTTagCompound book = SGPlayerAddresses.forPlayer(player);
      try {
         if (action.equals("save")) {
            SGPlayerAddresses.save(book, name, address);
            sendAddressBook(player);
            sendRemoteStatus(player, "Address saved");
            return;
         }
         if (action.equals("rename")) {
            SGPlayerAddresses.rename(book, address, name);
            sendAddressBook(player);
            sendRemoteStatus(player, "Address renamed");
            return;
         }
         if (action.equals("delete")) {
            SGPlayerAddresses.delete(book, address);
            sendAddressBook(player);
            sendRemoteStatus(player, "Address deleted");
            return;
         }
      } catch (IllegalArgumentException e) {
         sendRemoteStatus(player, e.getMessage());
         return;
      }
      if (!action.equals("dial") && !action.equals("disconnect") && !action.equals("barrier")) {
         sendRemoteStatus(player, "Unknown Remote Dialer action");
         return;
      }
      List<SGBaseTE> gates = new ArrayList<SGBaseTE>();
      for (Object tile : player.worldObj.loadedTileEntityList) {
         if (tile instanceof SGBaseTE) {
            gates.add((SGBaseTE)tile);
         }
      }
      SGBaseTE gate = nearestGate(gates, player.posX, player.posY, player.posZ, remoteRange);
      if (gate == null) {
         sendRemoteStatus(player, "No assembled Stargate within " + remoteRange + " blocks");
      } else if (action.equals("dial")) {
         try {
            String result = gate.connect(SGPlayerAddresses.validAddress(address), player);
            sendRemoteStatus(player, result == null ? "Dialing " + address : result);
         } catch (IllegalArgumentException e) {
            sendRemoteStatus(player, e.getMessage());
         }
      } else if (action.equals("disconnect")) {
         String result = gate.attemptToDisconnect(player);
         sendRemoteStatus(player, result == null ? "Disconnecting" : result);
      } else if (!gate.hasIrisUpgrade) {
         sendRemoteStatus(player, "This Stargate has no iris or shield");
      } else {
         boolean close = gate.irisState == IrisState.Open || gate.irisState == IrisState.Opening;
         if (close) {
            gate.closeIris();
         } else {
            gate.openIris();
         }
         sendRemoteStatus(player, (close ? "Closing " : "Opening ") + (gate.barrierKind == BarrierKind.IRIS ? "iris" : "shield"));
      }
   }

   static SGBaseTE nearestGate(Iterable<SGBaseTE> gates, double x, double y, double z, int range) {
      SGBaseTE nearest = null;
      double best = (double)range * range;
      for (SGBaseTE gate : gates) {
         if (!gate.isMerged || gate.isInvalid()) {
            continue;
         }
         double dx = gate.xCoord + 0.5 - x;
         double dy = gate.yCoord + 0.5 - y;
         double dz = gate.zCoord + 0.5 - z;
         double distance = dx * dx + dy * dy + dz * dz;
         if (distance < best || distance == best && (nearest == null || gate.xCoord < nearest.xCoord || gate.xCoord == nearest.xCoord && (gate.yCoord < nearest.yCoord || gate.yCoord == nearest.yCoord && gate.zCoord < nearest.zCoord))) {
            nearest = gate;
            best = distance;
         }
      }
      return nearest;
   }

   static void sendAddressBook(EntityPlayer player) {
      List<SGPlayerAddresses.Entry> entries = SGPlayerAddresses.list(SGPlayerAddresses.forPlayer(player));
      BaseDataChannel.ChannelOutput data = channel.openPlayer(player, "RemoteBook");
      data.writeInt(entries.size());
      for (SGPlayerAddresses.Entry entry : entries) {
         data.writeUTF(entry.name);
         data.writeUTF(entry.address);
      }
      data.close();
   }

   static void sendRemoteStatus(EntityPlayer player, String status) {
      BaseDataChannel.ChannelOutput data = channel.openPlayer(player, "RemoteStatus");
      data.writeUTF(status);
      data.close();
   }

   @BaseDataChannel.ClientMessageHandler("RemoteBook")
   public void receiveAddressBook(BaseDataChannel.ChannelInput data) {
      int size = data.readInt();
      if (size < 0 || size > 100) {
         return;
      }
      List<SGPlayerAddresses.Entry> entries = new ArrayList<SGPlayerAddresses.Entry>();
      for (int i = 0; i < size; i++) {
         entries.add(new SGPlayerAddresses.Entry(data.readUTF(), data.readUTF()));
      }
      remoteUpdates.add(new RemoteUpdate(entries, null));
   }

   @BaseDataChannel.ClientMessageHandler("RemoteStatus")
   public void receiveRemoteStatus(BaseDataChannel.ChannelInput data) {
      remoteUpdates.add(new RemoteUpdate(null, data.readUTF()));
   }

   public static RemoteUpdate pollRemoteUpdate() {
      return remoteUpdates.poll();
   }

   public static void clearRemoteUpdates() {
      remoteUpdates.clear();
   }

   static void sendWormholeStart(EntityPlayer player, int ticks) {
      BaseDataChannel.ChannelOutput data = channel.openPlayer(player, "WormholeStart");
      data.writeInt(ticks);
      data.close();
   }

   static void sendWormholeStop(EntityPlayer player) {
      BaseDataChannel.ChannelOutput data = channel.openPlayer(player, "WormholeStop");
      data.close();
   }

   @BaseDataChannel.ClientMessageHandler("WormholeStop")
   public void receiveWormholeStop(BaseDataChannel.ChannelInput data) {
      wormholeStarts.add(0);
   }

   @BaseDataChannel.ClientMessageHandler("WormholeStart")
   public void receiveWormholeStart(BaseDataChannel.ChannelInput data) {
      int ticks = data.readInt();
      if (ticks > 0 && ticks <= 60) {
         wormholeStarts.add(ticks);
      }
   }

   public static Integer pollWormholeStart() {
      return wormholeStarts.poll();
   }

   public static class RemoteUpdate {
      public final List<SGPlayerAddresses.Entry> entries;
      public final String status;

      RemoteUpdate(List<SGPlayerAddresses.Entry> entries, String status) {
         this.entries = entries;
         this.status = status;
      }
   }

   public static void sendConnectOrDisconnectToServer(SGBaseTE te, String address) {
      BaseDataChannel.ChannelOutput data = channel.openServer("ConnectOrDisconnect");
      writeCoords(data, te);
      data.writeUTF(address);
      data.close();
   }

   public static void sendQuickDial(SGBaseTE te, boolean qd) {
      BaseDataChannel.ChannelOutput data = channel.openServer("QuickDial");
      writeCoords(data, te);
      System.out.println("LEEL : " + qd);
      data.writeUTF("" + qd);
      data.close();
   }

   @BaseDataChannel.ServerMessageHandler("ConnectOrDisconnect")
   public void handleConnectOrDisconnectFromClient(EntityPlayer player, BaseDataChannel.ChannelInput data) {
      BlockPos pos = this.readCoords(data);
      String address = data.readUTF();
      SGBaseTE te = SGBaseTE.at(player.worldObj, (BlockPos)pos);
      DHDTE dhd = te != null ? te.getLinkedControllerTE() : null;
      if (dhd != null && dhd.isLinkedToStargate && pos.equals(dhd.linkedPos) && withinReach(player, dhd)) {
         te.connectOrDisconnect(address, player);
      }

   }

   static boolean withinReach(EntityPlayer player, TileEntity te) {
      return player.getDistanceSq(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5) <= 64.0;
   }

   @BaseDataChannel.ServerMessageHandler("QuickDial")
   public void handleQuickDialFromClient(EntityPlayer player, BaseDataChannel.ChannelInput data) {
      BlockPos pos = this.readCoords(data);
      String qd = data.readUTF();
      SGBaseTE te = SGBaseTE.at(player.worldObj, (BlockPos)pos);
      if (te != null && te.state == SGState.Idle && withinReach(player, te)) {
         te.qd = Boolean.parseBoolean(qd);
      }

   }

   public static void sendEnteredAddressToServer(DHDTE te, String address) {
      BaseDataChannel.ChannelOutput data = channel.openServer("EnteredAddress");
      writeCoords(data, te);
      data.writeUTF(address);
      data.close();
   }

   @BaseDataChannel.ServerMessageHandler("EnteredAddress")
   public void handleEnteredAddressFromClient(EntityPlayer player, BaseDataChannel.ChannelInput data) {
      BlockPos pos = this.readCoords(data);
      String address = data.readUTF();
      DHDTE te = DHDTE.at(player.worldObj, (BlockPos)pos);
      if (te != null && address.length() <= SGAddressing.maxAddressLength && SGAddressing.validSymbols(address) && withinReach(player, te)) {
         te.setEnteredAddress(address);
      }

   }

   public static void writeCoords(BaseDataChannel.ChannelOutput data, TileEntity te) {
      BaseUtils.writeBlockPos(data, BaseBlockUtils.getTileEntityPos(te));
   }

   public BlockPos readCoords(BaseDataChannel.ChannelInput data) {
      return BaseUtils.readBlockPos(data);
   }
}
