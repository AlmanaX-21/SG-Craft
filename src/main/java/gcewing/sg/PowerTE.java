package gcewing.sg;

import net.minecraft.nbt.NBTTagCompound;

public abstract class PowerTE extends BaseTileEntity implements ISGEnergySource {
   public double energyBuffer = (double)0.0F;
   public double energyMax;
   double energyPerSGEnergyUnit;

   public PowerTE(double energyMax, double energyPerSGEnergyUnit) {
      this.energyMax = energyMax;
      this.energyPerSGEnergyUnit = energyPerSGEnergyUnit;
   }

   public abstract String getScreenTitle();

   public abstract String getUnitName();

   public void readContentsFromNBT(NBTTagCompound nbt) {
      this.energyBuffer = nbt.getDouble("energyBuffer");
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      nbt.setDouble("energyBuffer", this.energyBuffer);
   }

   public double availableEnergy() {
      double available = this.energyBuffer / this.energyPerSGEnergyUnit;
      return available;
   }

   public double drawEnergy(double request) {
      double available = this.energyBuffer / this.energyPerSGEnergyUnit;
      double supply = Math.min(request, available);
      this.energyBuffer -= supply * this.energyPerSGEnergyUnit;
      this.markChanged();
      return supply;
   }
}
