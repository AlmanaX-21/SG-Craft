package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class SGRingItem0 extends BaseItemBlock {
   public SGRingItem0(Block block) {
      super(block);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int i) {
      return i;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return String.format("%s.%s", super.getUnlocalizedName(stack), stack.getItemDamage());
   }
}
