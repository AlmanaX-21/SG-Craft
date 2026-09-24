package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;

public class SGRemoteDialerContainer extends BaseContainer {
   public SGRemoteDialerContainer(EntityPlayer player) {
      super(300, 236);
      addPlayerSlots(player, -1000, -1000);
   }
}
