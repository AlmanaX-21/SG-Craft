package gcewing.sg;

import java.util.HashMap;
import java.util.LinkedList;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

class SGStructureMap extends HashMap {
   public Object put(Object key, Object value) {
      if (value instanceof StructureStart) {
         this.augmentStructureStart((StructureStart)value);
      }

      return super.put(key, value);
   }

   void augmentStructureStart(StructureStart start) {
      LinkedList oldComponents = start.getComponents();
      LinkedList newComponents = new LinkedList();

      for(Object comp : oldComponents) {
         if (comp instanceof ComponentScatteredFeaturePieces.DesertPyramid) {
            StructureBoundingBox box = ((StructureComponent)comp).getBoundingBox();
            newComponents.add(new FeatureUnderDesertPyramid((ComponentScatteredFeaturePieces.DesertPyramid)comp));
         }
      }

      oldComponents.addAll(newComponents);
   }
}
