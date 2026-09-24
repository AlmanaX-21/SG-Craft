package gcewing.sg;

import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

public class SGDimensionMap extends WorldSavedData {
   protected List<Integer> indexToDimension = new ArrayList();
   protected Map<Integer, Integer> dimensionToIndex = new HashMap();

   public SGDimensionMap(String name) {
      super(name);
   }

   public static SGDimensionMap get() {
      WorldServer world = BaseUtils.getWorldForDimension(0);
      return (SGDimensionMap)BaseUtils.getWorldData(world, SGDimensionMap.class, "sgcraft:dimension_map");
   }

   public static Integer dimensionForIndex(int index) {
      return get().getDimensionForIndex(index);
   }

   protected Integer getDimensionForIndex(int index) {
      System.out.println("get dim for " + index + " | " + this.indexToDimension.size());
      Integer dimension = null;
      if (index >= 0 && index < this.indexToDimension.size()) {
         dimension = (Integer)this.indexToDimension.get(index);
         System.out.println(dimension);
      }

      return dimension;
   }

   public static Integer indexForDimension(int dimension) {
      return get().getIndexForDimension(dimension);
   }

   protected Integer getIndexForDimension(int dimension) {
      if (!this.dimensionToIndex.containsKey(dimension)) {
         int index = this.indexToDimension.size();
         this.indexToDimension.add(dimension);
         this.dimensionToIndex.put(dimension, index);
         this.markDirty();
         System.out.println("added new index: " + index + " | " + dimension);
         return index;
      } else {
         int index = (Integer)this.dimensionToIndex.get(dimension);
         System.out.println("get index from cache " + index + " | " + dimension);
         return index;
      }
   }

   public void readFromNBT(NBTTagCompound nbt) {
      int[] a = nbt.getIntArray("dimensions");

      for(int i = 0; i < a.length; ++i) {
         this.indexToDimension.add(a[i]);
         this.dimensionToIndex.put(a[i], i);
      }

   }

   public void writeToNBT(NBTTagCompound nbt) {
      int[] a = Ints.toArray(this.indexToDimension);
      nbt.setIntArray("dimensions", a);
   }
}
