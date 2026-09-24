package gcewing.sg;

import cpw.mods.fml.common.registry.GameRegistry;
import gcewing.sg.te.RingTileEntityFlat;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RingBaseFlat extends RingBase {
   public RingBaseFlat() {
      super(Material.iron);
      GameRegistry.registerTileEntity(RingTileEntityFlat.class, "ringTileEntityFlat");
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new RingTileEntityFlat();
   }
}
