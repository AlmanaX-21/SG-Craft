package gcewing.sg;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class SGGateRegistry extends WorldSavedData {
   static final String dataName = "sgcraft_gate_registry";
   static final long maxGateIds = 78364164096L;
   long nextGateId = 1;
   int nextSuffix;
   final Map<String, Dimension> dimensions = new HashMap();
   final Map<String, BlockPos> gates = new HashMap();

   public SGGateRegistry(String name) {
      super(name);
   }

   static SGGateRegistry get() {
      WorldServer overworld = BaseUtils.getWorldForDimension(0);
      return BaseUtils.getWorldData(overworld, SGGateRegistry.class, dataName);
   }

   static String addressFor(SGBaseTE gate) {
      SGGateRegistry registry = get();
      String suffix = SGDimensionIdentity.get(gate.getWorldObj()).bind(registry, gate.dimension());
      String address = registry.registerGate(gate.gateAddress, suffix, new SGLocation(gate));
      if (!address.equals(gate.gateAddress)) {
         gate.gateAddress = address;
         gate.homeAddress = address;
         gate.markChanged();
      }
      return address;
   }

   static void retire(SGBaseTE gate) {
      if (gate.gateAddress != null) {
         SGGateRegistry registry = get();
         SGDimensionIdentity.get(gate.getWorldObj()).bind(registry, gate.dimension());
         registry.retireGate(gate.gateAddress, new SGLocation(gate));
      }
   }

   static SGBaseTE resolve(String address) {
      SGGateRegistry registry = get();
      SGLocation location = registry.locationForAddress(address);
      WorldServer world = location != null ? loadedWorld(location.dimension) : null;
      if (!registry.matchesWorld(address, world)) {
         registry.scanRegisteredDimensions();
         location = registry.locationForAddress(address);
         world = location != null ? loadedWorld(location.dimension) : null;
      }
      if (!registry.matchesWorld(address, world)) {
         return null;
      }
      SGBaseTE gate = SGBaseTE.at(world, location.pos);
      return gate != null && gate.isMerged && address.equals(gate.gateAddress) ? gate : null;
   }

   static WorldServer loadedWorld(int dimension) {
      WorldServer world = DimensionManager.getWorld(dimension);
      if (world == null && DimensionManager.isDimensionRegistered(dimension)) {
         DimensionManager.initDimension(dimension);
         world = DimensionManager.getWorld(dimension);
      }
      return world;
   }

   boolean matchesWorld(String address, WorldServer world) {
      if (world == null) {
         return false;
      }
      String worldId = this.worldIdForSuffix(address.substring(7));
      return worldId != null && worldId.equals(SGDimensionIdentity.get(world).worldId);
   }

   void scanRegisteredDimensions() {
      for(Integer dimension : DimensionManager.getStaticDimensionIDs()) {
         WorldServer world = loadedWorld(dimension);
         if (world != null) {
            SGDimensionIdentity.get(world).bind(this, dimension);
         }
      }
   }

   String registerDimension(String worldId, int runtimeId, String preferredSuffix) {
      for(Map.Entry<String, Dimension> entry : this.dimensions.entrySet()) {
         if (entry.getValue().worldId.equals(worldId)) {
            if (preferredSuffix != null && !preferredSuffix.equals(entry.getKey())) {
               throw new IllegalStateException("Conflicting Stargate dimension identity");
            }
            if (entry.getValue().runtimeId != runtimeId) {
               WorldServer previous = DimensionManager.getWorld(entry.getValue().runtimeId);
               if (previous != null && worldId.equals(SGDimensionIdentity.get(previous).worldId)) {
                  throw new IllegalStateException("Duplicate Stargate dimension identity");
               }
            }
            entry.getValue().runtimeId = runtimeId;
            this.markDirty();
            return entry.getKey();
         }
      }

      String suffix = preferredSuffix;
      if (suffix == null) {
         if (this.nextSuffix >= 1296) {
            throw new IllegalStateException("No Stargate dimension suffixes remain");
         }
         suffix = SGAddressing.intToSymbols(this.nextSuffix++, 2);
      } else {
         if (suffix.length() != 2 || !SGAddressing.validSymbols(suffix) || this.dimensions.containsKey(suffix)) {
            throw new IllegalStateException("Duplicate Stargate dimension suffix");
         }
         this.nextSuffix = Math.max(this.nextSuffix, SGAddressing.intFromSymbols(suffix) + 1);
      }

      this.dimensions.put(suffix, new Dimension(worldId, runtimeId));
      this.markDirty();
      return suffix;
   }

   String registerGate(String oldAddress, String suffix, SGLocation location) {
      if (!this.dimensions.containsKey(suffix)) {
         throw new IllegalStateException("Unknown Stargate dimension suffix");
      }
      BlockPos oldPos = this.gates.get(oldAddress);
      if (oldPos != null && oldAddress.endsWith(suffix) && oldPos.equals(location.pos)) {
         return oldAddress;
      }
      if (this.nextGateId >= maxGateIds) {
         throw new IllegalStateException("No Stargate addresses remain");
      }

      String address = SGAddressing.longToSymbols(this.nextGateId++, 7) + suffix;
      this.gates.put(address, new BlockPos(location.pos.x, location.pos.y, location.pos.z));
      this.markDirty();
      return address;
   }

   SGLocation locationForAddress(String address) {
      if (address.length() != 9) {
         return null;
      }
      BlockPos pos = this.gates.get(address);
      Dimension dimension = this.dimensions.get(address.substring(7));
      return pos != null && dimension != null ? new SGLocation(dimension.runtimeId, pos) : null;
   }

   void retireGate(String address, SGLocation location) {
      BlockPos pos = this.gates.get(address);
      Dimension dimension = this.dimensions.get(address.substring(7));
      if (pos != null && dimension != null && pos.equals(location.pos) && dimension.runtimeId == location.dimension) {
         this.gates.remove(address);
         this.markDirty();
      }
   }

   String worldIdForSuffix(String suffix) {
      Dimension dimension = this.dimensions.get(suffix);
      return dimension != null ? dimension.worldId : null;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.nextGateId = Math.max(1, nbt.getLong("nextGateId"));
      this.nextSuffix = nbt.getInteger("nextSuffix");
      this.dimensions.clear();
      this.gates.clear();
      NBTTagList dimensionTags = nbt.getTagList("dimensions", 10);
      for(int i = 0; i < dimensionTags.tagCount(); i++) {
         NBTTagCompound tag = dimensionTags.getCompoundTagAt(i);
         this.dimensions.put(tag.getString("suffix"), new Dimension(tag.getString("worldId"), tag.getInteger("runtimeId")));
      }
      NBTTagList gateTags = nbt.getTagList("gates", 10);
      for(int i = 0; i < gateTags.tagCount(); i++) {
         NBTTagCompound tag = gateTags.getCompoundTagAt(i);
         this.gates.put(tag.getString("address"), new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
      }
   }

   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setLong("nextGateId", this.nextGateId);
      nbt.setInteger("nextSuffix", this.nextSuffix);
      NBTTagList dimensionTags = new NBTTagList();
      for(Map.Entry<String, Dimension> entry : this.dimensions.entrySet()) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setString("suffix", entry.getKey());
         tag.setString("worldId", entry.getValue().worldId);
         tag.setInteger("runtimeId", entry.getValue().runtimeId);
         dimensionTags.appendTag(tag);
      }
      nbt.setTag("dimensions", dimensionTags);
      NBTTagList gateTags = new NBTTagList();
      for(Map.Entry<String, BlockPos> entry : this.gates.entrySet()) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setString("address", entry.getKey());
         tag.setInteger("x", entry.getValue().x);
         tag.setInteger("y", entry.getValue().y);
         tag.setInteger("z", entry.getValue().z);
         gateTags.appendTag(tag);
      }
      nbt.setTag("gates", gateTags);
   }

   static class Dimension {
      final String worldId;
      int runtimeId;

      Dimension(String worldId, int runtimeId) {
         this.worldId = worldId;
         this.runtimeId = runtimeId;
      }
   }
}
