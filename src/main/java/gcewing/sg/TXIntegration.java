package gcewing.sg;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TXIntegration extends BaseSubsystem<SGCraft, SGCraftClient> {
   public void registerRecipes() {
      Item frame = GameRegistry.findItem("ThermalExpansion", "Frame");
      Item coil = GameRegistry.findItem("ThermalExpansion", "material");
      ItemStack hardenedEnergyFrame = new ItemStack(frame, 1, 4);
      ItemStack receptionCoil = new ItemStack(coil, 1, 1);
      ItemStack transmissionCoil = new ItemStack(coil, 1, 2);
      SGCraft var10000 = this.mod;
      SGCraft var10001 = this.mod;
      var10000.newRecipe(SGCraft.rfPowerUnit, 1, new Object[]{"ttt", "hrh", "ici", 't', transmissionCoil, 'h', hardenedEnergyFrame, 'r', receptionCoil, 'i', "ingotInvar", 'c', "ingotCopper"});
   }
}
