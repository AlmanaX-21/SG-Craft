package gcewing.sg;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.util.EnumFacing;

public class PropertyTurn extends PropertyEnum<EnumFacing> {
   protected static EnumFacing[] values;
   protected static Collection valueList;

   public PropertyTurn(String name) {
      super(name, EnumFacing.class, valueList);
   }

   static {
      values = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST};
      valueList = Arrays.asList(values);
   }
}
