package gcewing.sg.oc;

import gcewing.sg.IComputerInterface;
import gcewing.sg.ITickable;
import gcewing.sg.SGBaseTE;
import gcewing.sg.SGInterfaceTE;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class OCInterfaceTE extends SGInterfaceTE implements IComputerInterface, Environment, IInventory, ITickable {
   static final int numUpgradeSlots = 1;
   IInventory inventory = new InventoryBasic("", false, 1);
   protected static Object[] success = new Object[]{true};
   protected Node node;
   protected boolean addedToNetwork = false;

   public OCInterfaceTE() {
      this.node = Network.newNode(this, Visibility.Network).withComponent("stargate", Visibility.Network).create();
   }

   protected IInventory getInventory() {
      return this.inventory;
   }

   boolean hasNetworkCard() {
      return isNetworkCard(this.getStackInSlot(0));
   }

   static boolean isNetworkCard(ItemStack stack) {
      return stack != null && OCIntegration.networkCard.isItemEqual(stack);
   }

   void forwardNetworkPacket(Packet packet) {
      if (packet.ttl() > 0) {
         SGBaseTE te = this.getBaseTE();
         if (te != null) {
            te.forwardNetworkPacket(packet.hop());
         }
      }

   }

   public void rebroadcastNetworkPacket(Object packet) {
      if (packet instanceof Packet && this.hasNetworkCard() && this.node != null) {
         this.node.sendToReachable("network.message", new Object[]{packet});
      }

   }

   protected static Object[] failure(Exception e) {
      return new Object[]{null, e.getMessage()};
   }

   @Callback
   public Object[] stargateState(Context ctx, Arguments args) {
      try {
         SGInterfaceTE.CIStargateState result = this.ciStargateState();
         return new Object[]{result.state, result.chevrons, result.direction};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] energyAvailable(Context ctx, Arguments args) {
      try {
         return new Object[]{this.ciEnergyAvailable()};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] energyToDial(Context ctx, Arguments args) {
      try {
         return new Object[]{this.ciEnergyToDial(args.checkString(0))};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] localAddress(Context ctx, Arguments args) {
      try {
         return new Object[]{this.ciLocalAddress()};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] remoteAddress(Context ctx, Arguments args) {
      try {
         return new Object[]{this.ciRemoteAddress()};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] dial(Context ctx, Arguments args) {
      try {
         this.ciDial(args.checkString(0));
         return success;
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] disconnect(Context ctx, Arguments args) {
      try {
         this.ciDisconnect();
         return success;
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] irisState(Context ctx, Arguments args) {
      try {
         return new Object[]{this.ciIrisState()};
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] openIris(Context ctx, Arguments args) {
      try {
         this.ciOpenIris();
         return success;
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] closeIris(Context ctx, Arguments args) {
      try {
         this.ciCloseIris();
         return success;
      } catch (Exception e) {
         return failure(e);
      }
   }

   @Callback
   public Object[] sendMessage(Context ctx, Arguments args) {
      try {
         int n = args.count();
         Object[] objs = new Object[n];

         for(int i = 0; i < n; ++i) {
            objs[i] = args.checkAny(i);
         }

         this.ciSendMessage(objs);
         return success;
      } catch (Exception e) {
         return failure(e);
      }
   }

   public Node node() {
      return this.node;
   }

   public void onConnect(Node node) {
   }

   public void onDisconnect(Node node) {
   }

   public void onMessage(Message msg) {
      if (msg.name().equals("network.message") && this.hasNetworkCard()) {
         this.forwardNetworkPacket((Packet)msg.data()[0]);
      }

   }

   public void update() {
      if (!this.addedToNetwork) {
         this.addedToNetwork = true;
         Network.joinOrCreateNetwork(this);
      }

   }

   public void onChunkUnload() {
      super.onChunkUnload();
      this.onRemoved();
   }

   public void invalidate() {
      super.invalidate();
      this.onRemoved();
   }

   void onRemoved() {
      if (this.node != null) {
         this.node.remove();
      }

   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
      super.readContentsFromNBT(nbt);
      if (this.node != null && this.node.host() == this) {
         this.node.load(nbt.getCompoundTag("oc:node"));
      }

   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      super.writeContentsToNBT(nbt);
      if (this.node != null && this.node.host() == this) {
         NBTTagCompound nodeNbt = new NBTTagCompound();
         this.node.save(nodeNbt);
         nbt.setTag("oc:node", nodeNbt);
      }

   }

   public void postEvent(TileEntity source, String name, Object... args) {
      if (this.node != null) {
         this.node.sendToReachable("computer.signal", prependArgs(new Object[]{name, args}));
      }

   }

   void onInventoryChanged(int slot) {
      this.markDirty();
   }

   public int getSizeInventory() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getSizeInventory() : 0;
   }

   public ItemStack getStackInSlot(int slot) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getStackInSlot(slot) : null;
   }

   public ItemStack decrStackSize(int slot, int amount) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         ItemStack result = inventory.decrStackSize(slot, amount);
         this.onInventoryChanged(slot);
         return result;
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         ItemStack result = inventory.getStackInSlotOnClosing(slot);
         this.onInventoryChanged(slot);
         return result;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.setInventorySlotContents(slot, stack);
         this.onInventoryChanged(slot);
      }

   }

   public String getInventoryName() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getInventoryName() : "";
   }

   public int getInventoryStackLimit() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.getInventoryStackLimit() : 0;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.isUseableByPlayer(player) : true;
   }

   public void openInventory() {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.openInventory();
      }

   }

   public void closeInventory() {
      IInventory inventory = this.getInventory();
      if (inventory != null) {
         inventory.closeInventory();
      }

   }

   public boolean isItemValidForSlot(int slot, ItemStack stack) {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.isItemValidForSlot(slot, stack) : false;
   }

   public boolean hasCustomInventoryName() {
      IInventory inventory = this.getInventory();
      return inventory != null ? inventory.hasCustomInventoryName() : false;
   }
}
