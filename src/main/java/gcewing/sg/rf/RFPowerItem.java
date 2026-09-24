package gcewing.sg.rf;

import gcewing.sg.PowerItem;
import net.minecraft.block.Block;

public class RFPowerItem extends PowerItem {
   public RFPowerItem(Block block) {
      super(block, "RF", (double)4000000.0F);
   }
}
