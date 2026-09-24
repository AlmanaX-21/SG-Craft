package gcewing.sg.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import gcewing.sg.BaseSubsystem;
import gcewing.sg.IIntegration;
import gcewing.sg.SGCraft;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class CCIntegration extends BaseSubsystem implements IIntegration {
   public static Block ccInterface;

   public void registerBlocks() {
      ccInterface = SGCraft.mod.newBlock("ccInterface", CCInterfaceBlock.class);
   }

   public void registerRecipes() {
      SGCraft.mod.newRecipe(ccInterface, 1, new Object[]{"SnS", "SrS", "SSS", 'S', Blocks.stone, 'n', "ingotNaquadahAlloy", 'r', Items.redstone});
   }

   public void registerOther() {
      ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
      CCMethodQueue.init();
   }

   public void onServerTick() {
      CCMethodQueue.onServerTick();
   }
}
