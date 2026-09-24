package gcewing.sg.oc;

import gcewing.sg.BaseConfiguration;
import gcewing.sg.IntegrationBase;
import gcewing.sg.SGBaseTE;
import gcewing.sg.SGCraftClient;
import gcewing.sg.SGGui;
import li.cil.oc.api.Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class OCIntegration extends IntegrationBase {
   public static Block ocInterface;
   public static ItemStack networkCard;

   static ItemStack ocItem(String name) {
      return Items.get(name).createItemStack(1);
   }

   public void configure(BaseConfiguration config) {
      OCWirelessEndpoint.configure(config);
   }

   public void registerBlocks() {
      ocInterface = this.mod.newBlock("ocInterface", OCInterfaceBlock.class);
   }

   public void registerItems() {
      networkCard = ocItem("lanCard");
   }

   public void registerRecipes() {
      ItemStack cable = ocItem("cable");
      ItemStack microchip1 = ocItem("chip1");
      ItemStack pcb = ocItem("printedCircuitBoard");
      this.mod.newRecipe(ocInterface, 1, new Object[]{"ini", "cmc", "ibi", 'i', net.minecraft.init.Items.iron_ingot, 'n', "ingotNaquadahAlloy", 'c', cable, 'm', microchip1, 'b', pcb});
   }

   public void registerContainers() {
      this.mod.addContainer(SGGui.OCInterface, OCInterfaceContainer.class);
   }

   public void registerScreens() {
      ((SGCraftClient)this.mod.client).addScreen(SGGui.OCInterface, OCInterfaceScreen.class);
   }

   public void onSGBaseTEAdded(SGBaseTE te) {
      te.ocWirelessEndpoint = new OCWirelessEndpoint(te);
   }
}
