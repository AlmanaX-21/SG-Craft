package gcewing.sg;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class BaseEntity extends Entity {
   public BaseEntity(World world) {
      super(world);
   }

   public void setBoundingBox(AxisAlignedBB box) {
      this.boundingBox.setBounds(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.getCollisionBoundingBox();
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }
}
