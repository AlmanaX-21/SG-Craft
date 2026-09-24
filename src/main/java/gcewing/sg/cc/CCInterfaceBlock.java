package gcewing.sg.cc;

import gcewing.sg.SGCraft;
import gcewing.sg.SGInterfaceBlock;

public class CCInterfaceBlock extends SGInterfaceBlock<CCInterfaceTE> {
   public CCInterfaceBlock() {
      super(SGCraft.machineMaterial, CCInterfaceTE.class);
      this.setModelAndTextures("block/interface.smeg", new String[]{"ccInterface-bottom", "ccInterface-top", "ccInterface-front", "ccInterface-side"});
   }
}
