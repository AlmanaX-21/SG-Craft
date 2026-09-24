package gcewing.sg.rf;

import cofh.api.energy.IEnergyHandler;
import gcewing.sg.PowerTE;
import net.minecraftforge.common.util.ForgeDirection;

public class RFPowerTE extends PowerTE implements IEnergyHandler {
   boolean debugInput = false;
   static final int maxEnergyBuffer = 4000000;
   static final double rfPerSGEnergyUnit = (double)80.0F;

   public RFPowerTE() {
      super((double)4000000.0F, (double)80.0F);
   }

   public String getScreenTitle() {
      return "RF SGPU";
   }

   public String getUnitName() {
      return "RF";
   }

   public boolean canConnectEnergy(ForgeDirection dir) {
      return true;
   }

   public int receiveEnergy(ForgeDirection dir, int energy, boolean query) {
      int e = (int)Math.min(this.energyMax - this.energyBuffer, (double)energy);
      if (!query) {
         this.addEnergy(e);
      }

      return e;
   }

   public int extractEnergy(ForgeDirection dir, int energy, boolean query) {
      int e = (int)Math.min(this.energyBuffer, (double)energy);
      if (!query) {
         this.addEnergy(-e);
      }

      return e;
   }

   void addEnergy(int e) {
      this.energyBuffer += (double)e;
      this.markChanged();
   }

   public int getEnergyStored(ForgeDirection dir) {
      return (int)this.energyBuffer;
   }

   public int getMaxEnergyStored(ForgeDirection dir) {
      return (int)this.energyMax;
   }
}
