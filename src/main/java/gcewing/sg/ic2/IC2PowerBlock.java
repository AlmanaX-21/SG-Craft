package gcewing.sg.ic2;

import gcewing.sg.BaseOrientation;
import gcewing.sg.PowerBlock;

public class IC2PowerBlock extends PowerBlock<IC2PowerTE> {
   public IC2PowerBlock() {
      super(IC2PowerTE.class, BaseOrientation.orient4WaysByState);
      this.setModelAndTextures("block/power.smeg", new String[]{"ic2PowerUnit-bottom", "ic2PowerUnit-top", "ic2PowerUnit-side"});
   }
}
