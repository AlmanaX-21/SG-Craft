package gcewing.sg;

import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class NaquadahOreBlock extends BaseOreBlock {
   public NaquadahOreBlock() {
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setStepSound(soundTypeStone);
      this.setHarvestLevel("pickaxe", 3);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return SGCraft.naquadah;
   }

   public int quantityDropped(Random random) {
      return 2 + random.nextInt(5);
   }
}
