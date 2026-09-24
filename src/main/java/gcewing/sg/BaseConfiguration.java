package gcewing.sg;

import cpw.mods.fml.common.registry.VillagerRegistry;
import java.io.File;
import java.util.Collection;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class BaseConfiguration extends Configuration {
   public boolean extended = false;
   int nextVillagerID = 100;

   public BaseConfiguration(File file) {
      super(file);
   }

   public boolean getBoolean(String category, String key, boolean defaultValue) {
      return this.get(category, key, defaultValue).getBoolean(defaultValue);
   }

   public int getInteger(String category, String key, int defaultValue) {
      return this.get(category, key, defaultValue).getInt(defaultValue);
   }

   public double getDouble(String category, String key, double defaultValue) {
      return this.get(category, key, defaultValue).getDouble(defaultValue);
   }

   public String getString(String category, String key, String defaultValue) {
      return this.get(category, key, defaultValue).getString();
   }

   public String[] getStringList(String category, String key, String... defaultValueList) {
      String defaultValue = BaseStringUtils.join(",", defaultValueList);
      String value = this.getString(category, key, defaultValue);
      return BaseStringUtils.split(",", value);
   }

   public int getVillager(String key) {
      VillagerRegistry reg = VillagerRegistry.instance();
      Property prop = this.get("villagers", key, -1);
      int id = prop.getInt();
      if (id == -1) {
         id = this.allocateVillagerId(reg);
         prop.set(id);
      }

      reg.registerVillagerId(id);
      return id;
   }

   int allocateVillagerId(VillagerRegistry reg) {
      Collection<Integer> inUse = VillagerRegistry.getRegisteredVillagers();

      int id;
      do {
         id = this.nextVillagerID++;
      } while(inUse.contains(id));

      return id;
   }

   public Property get(String category, String key, String defaultValue, String comment, Property.Type type) {
      if (!this.hasKey(category, key)) {
         this.extended = true;
      }

      return super.get(category, key, defaultValue, comment, type);
   }
}
