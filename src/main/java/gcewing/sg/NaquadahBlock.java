package gcewing.sg;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class NaquadahBlock extends BaseBlock {
   public NaquadahBlock() {
      super(Material.rock);
      this.mapColor = MapColor.greenColor;
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setStepSound(soundTypeMetal);
   }
}
