package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SGBaseContainer extends BaseContainer {
   SGBaseTE te;

   public static SGBaseContainer create(EntityPlayer player, World world, BlockPos pos) {
      SGBaseTE te = SGBaseTE.at(world, (BlockPos)pos);
      return te != null ? new SGBaseContainer(player, te) : null;
   }

   public SGBaseContainer(EntityPlayer player, SGBaseTE te) {
      super(256, 256);
      this.te = te;
      this.addCamouflageSlots();
      this.addPlayerSlots(player);
   }

   void addCamouflageSlots() {
      this.addSlots(this.te, 0, 7, 48, 152, 1, CamouflageSlot.class);
      this.addSlots(this.te, 11, 1, 156, 62, 1, CamouflageSlot.class);
      this.addSlots(this.te, 10, 1, 156, 80, 1, CamouflageSlot.class);
      this.addSlots(this.te, 9, 1, 156, 98, 1, CamouflageSlot.class);
      this.addSlots(this.te, 8, 1, 156, 116, 1, CamouflageSlot.class);
      this.addSlots(this.te, 7, 1, 156, 134, 1, CamouflageSlot.class);
      this.addSlots(this.te, 16, 1, 48, 62, 1, CamouflageSlot.class);
      this.addSlots(this.te, 15, 1, 48, 80, 1, CamouflageSlot.class);
      this.addSlots(this.te, 14, 1, 48, 98, 1, CamouflageSlot.class);
      this.addSlots(this.te, 13, 1, 48, 116, 1, CamouflageSlot.class);
      this.addSlots(this.te, 12, 1, 48, 134, 1, CamouflageSlot.class);
      this.addSlots(this.te, 17, 7, 48, 44, 1, CamouflageSlot.class);
   }
}
