package gcewing.sg;

import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;

public class SGChunkData {
   static HashMap<ChunkCoordIntPair, SGChunkData> map = new HashMap();
   public boolean oresGenerated;

   public static SGChunkData forChunk(Chunk chunk) {
      return forChunk(chunk, (NBTTagCompound)null);
   }

   public static SGChunkData forChunk(Chunk chunk, NBTTagCompound nbt) {
      ChunkCoordIntPair coords = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
      SGChunkData data = (SGChunkData)map.get(coords);
      if (data == null) {
         data = new SGChunkData();
         if (nbt != null) {
            data.readFromNBT(nbt);
         }

         map.put(coords, data);
      }

      return data;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.oresGenerated = nbt.getBoolean("gcewing.sg.oresGenerated");
   }

   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("gcewing.sg.oresGenerated", this.oresGenerated);
   }

   public static void onChunkLoad(ChunkDataEvent.Load e) {
      Chunk chunk = e.getChunk();
      SGChunkData data = forChunk(chunk, e.getData());
      if (!data.oresGenerated && SGCraft.addOresToExistingWorlds) {
         SGCraft.naquadahOreGenerator.regenerate(chunk);
      }

   }

   public static void onChunkSave(ChunkDataEvent.Save e) {
      Chunk chunk = e.getChunk();
      SGChunkData data = forChunk(chunk);
      data.writeToNBT(e.getData());
   }
}
