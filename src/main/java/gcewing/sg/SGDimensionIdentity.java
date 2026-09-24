package gcewing.sg;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class SGDimensionIdentity extends WorldSavedData {
   static final String dataName = "sgcraft_dimension_identity";
   String worldId;
   String suffix;

   public SGDimensionIdentity(String name) {
      super(name);
   }

   static SGDimensionIdentity get(World world) {
      return BaseUtils.getWorldData(world, SGDimensionIdentity.class, dataName);
   }

   String bind(SGGateRegistry registry, int runtimeId) {
      if (this.worldId == null || this.worldId.isEmpty()) {
         this.worldId = UUID.randomUUID().toString();
      }
      String assigned = registry.registerDimension(this.worldId, runtimeId, this.suffix);
      if (!assigned.equals(this.suffix)) {
         this.suffix = assigned;
         this.markDirty();
      }
      return assigned;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.worldId = nbt.getString("worldId");
      this.suffix = nbt.getString("suffix");
      if (this.suffix.isEmpty()) {
         this.suffix = null;
      }
   }

   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setString("worldId", this.worldId != null ? this.worldId : "");
      nbt.setString("suffix", this.suffix != null ? this.suffix : "");
   }
}
