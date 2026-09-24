package gcewing.sg;

import cpw.mods.fml.common.registry.GameRegistry;
import gcewing.sg.te.RingTileEntityUp;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RingBaseUp extends RingBase {
   public RingBaseUp() {
      super(Material.iron);
      GameRegistry.registerTileEntity(RingTileEntityUp.class, "ringTileEntityUp");
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new RingTileEntityUp();
   }
}
