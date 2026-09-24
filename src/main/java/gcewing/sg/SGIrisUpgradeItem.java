package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SGIrisUpgradeItem extends BaseItem {
   private final BarrierKind kind;

   public SGIrisUpgradeItem() {
      this(BarrierKind.IRIS);
   }

   public SGIrisUpgradeItem(BarrierKind kind) {
      this.kind = kind;
   }

   public int getColorFromItemStack(ItemStack stack, int pass) {
      return this.kind == BarrierKind.SHIELD ? 0x7bc9ed : 0xffffff;
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      Block block = BaseBlockUtils.getWorldBlock(world, pos);
      if (block instanceof ISGBlock) {
         SGBaseTE te = ((ISGBlock)block).getBaseTE(world, pos);
         if (te != null) {
            return te.applyBarrierUpgrade(stack, player, this.kind);
         }
      }

      return false;
   }
}
