package gcewing.sg;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;

public class DHDTE extends BaseTileInventory implements ISGEnergySource {
   public static boolean debugLink = false;
   public static int linkRangeX = 5;
   public static int linkRangeY = 1;
   public static int linkRangeZ = 6;
   public static final int firstFuelSlot = 0;
   public static final int numFuelSlots = 4;
   public static final int numSlots = 4;
   public byte typ;
   public boolean isLinkedToStargate;
   public BlockPos linkedPos = new BlockPos(0, 0, 0);
   public String enteredAddress = "";
   IInventory inventory = new InventoryBasic("DHD", false, 4);
   static AxisAlignedBB bounds;
   public static double maxEnergyBuffer;
   protected double energyInBuffer;

   public static void configure(BaseConfiguration cfg) {
      linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
      linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
      linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
      maxEnergyBuffer = SGBaseTE.energyPerFuelItem;
   }

   public static DHDTE at(IBlockAccess world, BlockPos pos) {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
      return te instanceof DHDTE ? (DHDTE)te : null;
   }

   public static DHDTE at(IBlockAccess world, NBTTagCompound nbt) {
      BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
      return at(world, pos);
   }

   public void setEnteredAddress(String address) {
      this.enteredAddress = address;
      this.markChanged();
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return bounds.addCoord((double)this.getX() + (double)0.5F, (double)this.getY(), (double)this.getZ() + (double)0.5F);
   }

   public double getMaxRenderDistanceSquared() {
      return (double)32768.0F;
   }

   protected IInventory getInventory() {
      return this.inventory;
   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
      super.readContentsFromNBT(nbt);
      this.isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
      this.typ = nbt.getByte("typ");
      this.energyInBuffer = nbt.getDouble("energyInBuffer");
      int x = nbt.getInteger("linkedX");
      int y = nbt.getInteger("linkedY");
      int z = nbt.getInteger("linkedZ");
      this.linkedPos = new BlockPos(x, y, z);
      this.enteredAddress = nbt.getString("enteredAddress");
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      super.writeContentsToNBT(nbt);
      nbt.setBoolean("isLinkedToStargate", this.isLinkedToStargate);
      nbt.setDouble("energyInBuffer", this.energyInBuffer);
      nbt.setByte("typ", this.typ);
      nbt.setInteger("linkedX", this.linkedPos.getX());
      nbt.setInteger("linkedY", this.linkedPos.getY());
      nbt.setInteger("linkedZ", this.linkedPos.getZ());
      nbt.setString("enteredAddress", this.enteredAddress);
   }

   public SGBaseTE getLinkedStargateTE(byte wishedID) {
      if (this.isLinkedToStargate) {
         TileEntity gte = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.linkedPos);
         if (gte instanceof SGBaseTE && ((SGBaseTE)gte).getType() == wishedID) {
            return (SGBaseTE)gte;
         }
      }

      return null;
   }

   void checkForLink(byte id) {
      if (!this.isLinkedToStargate) {
         Trans3 t = this.localToGlobalTransformation();

         for(int i = -linkRangeX; i <= linkRangeX; ++i) {
            for(int j = -linkRangeY; j <= linkRangeY; ++j) {
               for(int k = 1; k <= linkRangeZ; ++k) {
                  Vector3 p = t.p((double)i, (double)j, (double)(-k));
                  BlockPos bp = new BlockPos(p.floorX(), p.floorY(), p.floorZ());
                  TileEntity te = BaseBlockUtils.getWorldTileEntity(this.worldObj, bp);
                  if (te instanceof SGBaseTE && ((SGBaseTE)te).getType() == id && this.linkToStargate((SGBaseTE)te)) {
                     return;
                  }
               }
            }
         }

         int range = linkRangeZ;
         for(int x = -range; x <= range; ++x) {
            for(int y = -range; y <= range; ++y) {
               for(int z = -range; z <= range; ++z) {
                  TileEntity tile = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.getPos().add(x, y, z));
                  if (tile instanceof SGBaseTE) {
                     SGBaseTE gate = (SGBaseTE)tile;
                     if (gate.isHorizontal() && gate.getType() == id && this.linkToStargate(gate)) {
                        return;
                     }
                  }
               }
            }
         }
      }

   }

   boolean linkToStargate(SGBaseTE gte) {
      if (!this.isLinkedToStargate && !gte.isLinkedToController && gte.isMerged) {
         this.linkedPos = gte.getPos();
         this.isLinkedToStargate = true;
         this.markChanged();
         gte.linkedPos = this.getPos();
         gte.isLinkedToController = true;
         gte.markChanged();
         return true;
      } else {
         return false;
      }
   }

   public void clearLinkToStargate() {
      this.isLinkedToStargate = false;
      this.markChanged();
   }

   public double availableEnergy() {
      double energy = this.energyInBuffer;

      for(int i = 0; i < 4; ++i) {
         ItemStack stack = this.fuelStackInSlot(i);
         if (stack != null) {
            energy += (double)stack.stackSize * SGBaseTE.energyPerFuelItem;
         }
      }

      return energy;
   }

   public double drawEnergy(double amount) {
      double energyDrawn;
      double e;
      for(energyDrawn = (double)0.0F; energyDrawn < amount && (this.energyInBuffer != (double)0.0F || this.useFuelItem()); this.energyInBuffer -= e) {
         e = Math.min(amount, this.energyInBuffer);
         energyDrawn += e;
      }

      this.markChanged();
      return energyDrawn;
   }

   boolean useFuelItem() {
      for(int i = 3; i >= 0; --i) {
         ItemStack stack = this.fuelStackInSlot(i);
         if (stack != null) {
            this.decrStackSize(i, 1);
            this.energyInBuffer += SGBaseTE.energyPerFuelItem;
            return true;
         }
      }

      return false;
   }

   ItemStack fuelStackInSlot(int i) {
      ItemStack stack = this.getStackInSlot(0 + i);
      return isValidFuelItem(stack) ? stack : null;
   }

   public static boolean isValidFuelItem(ItemStack stack) {
      return stack != null && stack.getItem() == SGCraft.naquadah && stack.stackSize > 0;
   }

   public boolean isItemValidForSlot(int slot, ItemStack stack) {
      return isValidFuelItem(stack);
   }

   public double getEnergyInBuffer() {
      return this.energyInBuffer;
   }

   public void setEnergyInBuffer(double energyInBuffer) {
      this.energyInBuffer = energyInBuffer;
   }
}
