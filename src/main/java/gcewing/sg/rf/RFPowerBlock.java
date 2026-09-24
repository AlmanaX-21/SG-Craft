package gcewing.sg.rf;

import gcewing.sg.BaseBlock;
import gcewing.sg.PowerBlock;

public class RFPowerBlock extends PowerBlock<RFPowerTE> {
   public RFPowerBlock() {
      super(RFPowerTE.class, (BaseBlock.IOrientationHandler)null);
      this.setModelAndTextures("block/power.smeg", new String[]{"rfPowerUnit-bottom", "rfPowerUnit-top", "rfPowerUnit-side"});
   }
}
