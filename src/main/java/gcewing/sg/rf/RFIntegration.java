package gcewing.sg.rf;

import gcewing.sg.BaseSubsystem;
import gcewing.sg.SGCraft;
import gcewing.sg.SGCraftClient;

public class RFIntegration extends BaseSubsystem<SGCraft, SGCraftClient> {
   public void registerBlocks() {
      SGCraft var10000 = this.mod;
      SGCraft.rfPowerUnit = ((SGCraft)this.mod).newBlock("rfPowerUnit", RFPowerBlock.class);
   }
}
