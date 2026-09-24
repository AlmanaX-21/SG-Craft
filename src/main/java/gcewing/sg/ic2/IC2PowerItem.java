package gcewing.sg.ic2;

import gcewing.sg.PowerItem;
import net.minecraft.block.Block;

public class IC2PowerItem extends PowerItem {
   public IC2PowerItem(Block block) {
      super(block, "EU", (double)1000000.0F);
   }
}
