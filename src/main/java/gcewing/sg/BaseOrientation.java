package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BaseOrientation {
   public static BaseBlock.IOrientationHandler orient4WaysByState = new Orient4WaysByState();
   public static BaseBlock.IOrientationHandler orient24WaysByTE = new Orient24WaysByTE();
   public static BaseBlock.IOrientationHandler orientStargateByState = new OrientStargateByState();

   public static Trans3 gateTransform(Vector3 origin, int orientation) {
      Trans3 transform = new Trans3(origin).turn(orientation & 3);
      if (orientation >= 8) {
         return transform.rotX(90);
      }
      return orientation >= 4 ? transform.rotX(-90) : transform;
   }

   public static int gateOrientation(EnumFacing side, int turn, boolean sneaking) {
      return sneaking && side == EnumFacing.UP ? 4 + turn : sneaking && side == EnumFacing.DOWN ? 8 + turn : turn;
   }

   public static class OrientStargateByState implements BaseBlock.IOrientationHandler {
      public static IProperty<Integer> ORIENTATION = PropertyInteger.create("orientation", 0, 11);

      public void defineProperties(BaseBlock block) {
         block.addProperty(ORIENTATION);
      }

      public IBlockState onBlockPlaced(Block block, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState baseState, EntityLivingBase placer) {
         EnumFacing facing = BaseUtils.horizontalFacings[BaseUtils.iround(placer.rotationYaw / 90.0) & 3];
         int turn = facing == EnumFacing.NORTH ? 0 : facing == EnumFacing.WEST ? 1 : facing == EnumFacing.SOUTH ? 2 : 3;
         int orientation = gateOrientation(side, turn, placer.isSneaking());
         return baseState.withProperty(ORIENTATION, orientation);
      }

      public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state, Vector3 origin) {
         return gateTransform(origin, state.getValue(ORIENTATION));
      }
   }

   public static class Orient4WaysByState implements BaseBlock.IOrientationHandler {
      public static IProperty FACING = new PropertyTurn("facing");

      public void defineProperties(BaseBlock block) {
         block.addProperty(FACING);
      }

      public IBlockState onBlockPlaced(Block block, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState baseState, EntityLivingBase placer) {
         EnumFacing dir = this.getHorizontalFacing(placer);
         return baseState.withProperty(FACING, dir);
      }

      protected EnumFacing getHorizontalFacing(Entity entity) {
         return BaseUtils.horizontalFacings[BaseUtils.iround((double)entity.rotationYaw / (double)90.0F) & 3];
      }

      public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state, Vector3 origin) {
         EnumFacing f = (EnumFacing)state.getValue(FACING);
         int i;
         switch (f) {
            case NORTH:
               i = 0;
               break;
            case WEST:
               i = 1;
               break;
            case SOUTH:
               i = 2;
               break;
            case EAST:
               i = 3;
               break;
            default:
               i = 0;
         }

         return (new Trans3(origin)).turn(i);
      }
   }

   public static class Orient24WaysByTE extends BaseBlock.Orient1Way {
      public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state, Vector3 origin) {
         TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
         if (te instanceof BaseTileEntity) {
            BaseTileEntity bte = (BaseTileEntity)te;
            return Trans3.sideTurn(origin, bte.side, bte.turn);
         } else {
            return super.localToGlobalTransformation(world, pos, state, origin);
         }
      }
   }
}
