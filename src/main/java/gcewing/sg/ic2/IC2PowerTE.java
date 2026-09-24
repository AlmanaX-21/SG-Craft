package gcewing.sg.ic2;

import gcewing.sg.ITickable;
import gcewing.sg.PowerTE;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class IC2PowerTE extends PowerTE implements IEnergySink, ITickable {
   static final int maxSafeInput = 2048;
   static final int maxEnergyBuffer = 1000000;
   static final double euPerSGEnergyUnit = (double)20.0F;
   boolean loaded = false;

   public IC2PowerTE() {
      super((double)1000000.0F, (double)20.0F);
   }

   public String getScreenTitle() {
      return "IC2 SGPU";
   }

   public String getUnitName() {
      return "EU";
   }

   public void update() {
      this.load();
   }

   public void invalidate() {
      this.unload();
      super.invalidate();
   }

   public void onChunkUnload() {
      this.unload();
      super.onChunkUnload();
   }

   void load() {
      if (!this.worldObj.isRemote && !this.loaded) {
         this.loaded = true;
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
      }

   }

   void unload() {
      if (!this.worldObj.isRemote && this.loaded) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.loaded = false;
      }

   }

   public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
      return true;
   }

   public double getDemandedEnergy() {
      double eu = Math.min((double)1000000.0F - this.energyBuffer, (double)2048.0F);
      return eu;
   }

   public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
      this.energyBuffer += amount;
      this.markChanged();
      return (double)0.0F;
   }

   public int getSinkTier() {
      return 3;
   }
}
