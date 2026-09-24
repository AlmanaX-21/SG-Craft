package gcewing.sg.oc;

import gcewing.sg.BaseGui;

public class OCInterfaceScreen extends BaseGui.Screen {
   static final int bgUSize = 256;
   static final int bgVSize = 128;

   public OCInterfaceScreen(OCInterfaceContainer container) {
      super(container);
   }

   protected void drawBackgroundLayer() {
      this.bindTexture("gui/oc_sg_interface_gui.png", 256, 128);
      this.drawTexturedRect((double)0.0F, (double)0.0F, (double)this.xSize, (double)this.ySize, (double)0.0F, (double)0.0F);
      this.drawCenteredString("OC Stargate Interface", this.xSize / 2, 5);
   }
}
