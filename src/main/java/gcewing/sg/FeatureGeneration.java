package gcewing.sg;

import java.lang.reflect.Field;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class FeatureGeneration {
   public static boolean augmentStructures = false;
   static Field structureMap = BaseUtils.getFieldDef(MapGenStructure.class, "structureMap", "structureMap");

   public static void configure(BaseConfiguration config) {
      augmentStructures = config.getBoolean("options", "augmentStructures", augmentStructures);
   }

   public static void onInitMapGen(InitMapGenEvent e) {
      if (augmentStructures) {
         switch (e.type) {
            case SCATTERED_FEATURE:
               if (e.newGen instanceof MapGenStructure) {
                  e.newGen = modifyScatteredFeatureGen((MapGenStructure)e.newGen);
               }
         }
      }

   }

   static MapGenStructure modifyScatteredFeatureGen(MapGenStructure gen) {
      BaseUtils.setField(gen, structureMap, new SGStructureMap());
      return gen;
   }
}
