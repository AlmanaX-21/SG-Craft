package gcewing.sg;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.util.EnumFacing;

public enum Plane implements Predicate<EnumFacing>, Iterable<EnumFacing> {
   HORIZONTAL,
   VERTICAL;

   private int[] axes = new int[]{1, 1, 0, 0, 0, 0};

   public EnumFacing[] facings() {
      switch (this) {
         case HORIZONTAL:
            return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case VERTICAL:
            return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
         default:
            throw new Error("Someone's been tampering with the universe!");
      }
   }

   public EnumFacing random(Random rand) {
      EnumFacing[] aenumfacing = this.facings();
      return aenumfacing[rand.nextInt(aenumfacing.length)];
   }

   public boolean apply(EnumFacing dir) {
      return dir != null && this.axes[dir.ordinal()] == this.ordinal();
   }

   public Iterator<EnumFacing> iterator() {
      return Iterators.forArray(this.facings());
   }
}
