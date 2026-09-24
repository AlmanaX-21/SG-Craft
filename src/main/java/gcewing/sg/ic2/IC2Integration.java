package gcewing.sg.ic2;

import gcewing.sg.BaseSubsystem;
import gcewing.sg.SGCraft;
import gcewing.sg.SGCraftClient;
import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class IC2Integration extends BaseSubsystem<SGCraft, SGCraftClient> {
   public static ItemStack getIC2Item(String name) {
      return IC2Items.getItem(name);
   }

   public void registerBlocks() {
      SGCraft var10000 = this.mod;
      SGCraft.ic2PowerUnit = ((SGCraft)this.mod).newBlock("ic2PowerUnit", IC2PowerBlock.class, IC2PowerItem.class);
   }

   public void registerRecipes() {
      ItemStack rubber = getIC2Item("rubber");
      ItemStack copperPlate = getIC2Item("platecopper");
      ItemStack machine = getIC2Item("machine");
      ItemStack wire = getIC2Item("copperCableItem");
      ItemStack circuit = getIC2Item("electronicCircuit");
      SGCraft var10000 = this.mod;
      SGCraft var10001 = this.mod;
      var10000.newRecipe(SGCraft.ic2Capacitor, 1, new Object[]{"ppp", "rrr", "ppp", 'p', copperPlate, 'r', rubber});
      var10000 = this.mod;
      var10001 = this.mod;
      Block var8 = SGCraft.ic2PowerUnit;
      Object[] var10003 = new Object[]{"cwc", "wMw", "cec", 'c', null, null, null, null, null, null, null};
      SGCraft var10006 = this.mod;
      var10003[4] = SGCraft.ic2Capacitor;
      var10003[5] = 'w';
      var10003[6] = wire;
      var10003[7] = 'M';
      var10003[8] = machine;
      var10003[9] = 'e';
      var10003[10] = circuit;
      var10000.newRecipe(var8, 1, var10003);
   }
}
