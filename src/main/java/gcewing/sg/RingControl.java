package gcewing.sg;

import gcewing.sg.te.RingControllerTileEntity;
import gcewing.sg.te.RingTileEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class RingControl extends BaseBlock<RingControllerTileEntity> {
   public RingControl() {
      super(Material.iron, RingControllerTileEntity.class, "ringControllerTileEntity");
      this.setHardness(1.5F);
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderBlockPass() {
      return 1;
   }

   public BaseBlock.IOrientationHandler getOrientationHandler() {
      return BaseOrientation.orient4WaysByState;
   }

   public boolean canRenderInPass(int pass) {
      ForgeHooksClient.setRenderPass(pass);
      return true;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public int getRenderType() {
      return -1;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
      EnumFacing f = (EnumFacing)BaseBlockUtils.getWorldBlockState(world, new BlockPos(x, y, z)).getValue(BaseOrientation.Orient4WaysByState.FACING);
      switch (f) {
         case NORTH:
            this.setBlockBounds(0.3125F, 0.15625F, 0.0F, 0.6875F, 0.75F, 0.05F);
            break;
         case WEST:
            this.setBlockBounds(0.0F, 0.15625F, 0.3125F, 0.05F, 0.75F, 0.6875F);
            break;
         case SOUTH:
            this.setBlockBounds(0.3125F, 0.15625F, 0.95F, 0.6875F, 0.75F, 1.0F);
            break;
         case EAST:
            this.setBlockBounds(0.95F, 0.15625F, 0.3125F, 1.0F, 0.75F, 0.6875F);
            break;
         default:
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F);
      }

   }

   public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB clip, List result, Entity entity) {
      BlockPos pos = new BlockPos(x, y, z);
      EnumFacing f = (EnumFacing)BaseBlockUtils.getWorldBlockState(world, pos).getValue(BaseOrientation.Orient4WaysByState.FACING);
      switch (f) {
         case NORTH:
            this.setBlockBounds(0.3125F, 0.15625F, 0.0F, 0.6875F, 0.75F, 0.05F);
            break;
         case WEST:
            this.setBlockBounds(0.0F, 0.15625F, 0.3125F, 0.05F, 0.75F, 0.6875F);
            break;
         case SOUTH:
            this.setBlockBounds(0.3125F, 0.15625F, 0.95F, 0.6875F, 0.75F, 1.0F);
            break;
         case EAST:
            this.setBlockBounds(0.95F, 0.15625F, 0.3125F, 1.0F, 0.75F, 0.6875F);
            break;
         default:
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F);
      }

      AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, x, y, z);
      if (axisalignedbb1 != null && clip.intersectsWith(axisalignedbb1)) {
         result.add(axisalignedbb1);
      }

   }

   public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_) {
      this.setBlockBoundsBasedOnState(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
      p_149731_5_ = p_149731_5_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
      p_149731_6_ = p_149731_6_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
      Vec3 vec32 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.minX);
      Vec3 vec33 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.maxX);
      Vec3 vec34 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.minY);
      Vec3 vec35 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.maxY);
      Vec3 vec36 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.minZ);
      Vec3 vec37 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.maxZ);
      if (!this.isVecInsideYZBounds(vec32)) {
         vec32 = null;
      }

      if (!this.isVecInsideYZBounds(vec33)) {
         vec33 = null;
      }

      if (!this.isVecInsideXZBounds(vec34)) {
         vec34 = null;
      }

      if (!this.isVecInsideXZBounds(vec35)) {
         vec35 = null;
      }

      if (!this.isVecInsideXYBounds(vec36)) {
         vec36 = null;
      }

      if (!this.isVecInsideXYBounds(vec37)) {
         vec37 = null;
      }

      Vec3 vec38 = null;
      if (vec32 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec32) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec32;
      }

      if (vec33 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec33) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec33;
      }

      if (vec34 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec34) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec34;
      }

      if (vec35 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec35) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec35;
      }

      if (vec36 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec36) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec36;
      }

      if (vec37 != null && (vec38 == null || p_149731_5_.squareDistanceTo(vec37) < p_149731_5_.squareDistanceTo(vec38))) {
         vec38 = vec37;
      }

      if (vec38 == null) {
         return null;
      } else {
         byte b0 = -1;
         if (vec38 == vec32) {
            b0 = 4;
         }

         if (vec38 == vec33) {
            b0 = 5;
         }

         if (vec38 == vec34) {
            b0 = 0;
         }

         if (vec38 == vec35) {
            b0 = 1;
         }

         if (vec38 == vec36) {
            b0 = 2;
         }

         if (vec38 == vec37) {
            b0 = 3;
         }

         return new MovingObjectPosition(p_149731_2_, p_149731_3_, p_149731_4_, b0, vec38.addVector((double)p_149731_2_, (double)p_149731_3_, (double)p_149731_4_));
      }
   }

   private boolean isVecInsideYZBounds(Vec3 p_149654_1_) {
      return p_149654_1_ == null ? false : p_149654_1_.yCoord >= this.minY && p_149654_1_.yCoord <= this.maxY && p_149654_1_.zCoord >= this.minZ && p_149654_1_.zCoord <= this.maxZ;
   }

   private boolean isVecInsideXZBounds(Vec3 p_149687_1_) {
      return p_149687_1_ == null ? false : p_149687_1_.xCoord >= this.minX && p_149687_1_.xCoord <= this.maxX && p_149687_1_.zCoord >= this.minZ && p_149687_1_.zCoord <= this.maxZ;
   }

   private boolean isVecInsideXYBounds(Vec3 p_149661_1_) {
      return p_149661_1_ == null ? false : p_149661_1_.xCoord >= this.minX && p_149661_1_.xCoord <= this.maxX && p_149661_1_.yCoord >= this.minY && p_149661_1_.yCoord <= this.maxY;
   }

   public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_) {
      return false;
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new RingControllerTileEntity();
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p_149727_5_, int p_149727_6_, float hitX, float hitY, float hitZ) {
      if (!world.isRemote) {
         RingControllerTileEntity controller = (RingControllerTileEntity)world.getTileEntity(x, y, z);
         BlockPos pos = new BlockPos(x, y, z);
         EnumFacing facing = (EnumFacing)BaseBlockUtils.getWorldBlockState(world, pos).getValue(BaseOrientation.Orient4WaysByState.FACING);
         int button = this.getHitButton(facing, hitX, hitY, hitZ);
         RingTileEntity nearbyRing = this.getNearbyRing(world, x, y, z);
         if (nearbyRing != null && button == 0) {
            controller.startTeleportation(nearbyRing, nearbyRing.getNextRingUp());
         } else if (nearbyRing != null && button == 4) {
            controller.startTeleportation(nearbyRing, nearbyRing.getNextRingDown());
         } else if (nearbyRing != null && button == 1) {
            controller.startTeleportation(nearbyRing, nearbyRing.getLastRingUp());
         } else if (nearbyRing != null && button == 5) {
            controller.startTeleportation(nearbyRing, nearbyRing.getLastRingDown());
         } else if (button == 2) {
            controller.decreaseDelay();
         } else if (button == 3) {
            controller.increaseDelay();
         }

         if (button != -1) {
            world.playSoundEffect((double)x + (double)0.5F, (double)y + (double)0.5F, (double)z + (double)0.5F, "sgcraft:ring_button_" + (button % 2 == 0 ? 1 : 2), 3.0F, 1.0F);
         }
      }

      return super.onBlockActivated(world, x, y, z, p_149727_5_, p_149727_6_, hitX, hitY, hitZ);
   }

   protected int getHitButton(EnumFacing facing, float hitX, float hitY, float hitZ) {
      int column = -1;
      int row = -1;
      if (facing != EnumFacing.NORTH && facing != EnumFacing.SOUTH) {
         if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
            if ((double)hitZ >= 0.54159546 && (double)hitZ <= 0.6019592) {
               column = facing == EnumFacing.EAST ? 1 : 0;
            } else if ((double)hitZ >= 0.3907776 && (double)hitZ <= 0.4482727) {
               column = facing == EnumFacing.EAST ? 0 : 1;
            }
         }
      } else if ((double)hitX >= 0.54159546 && (double)hitX <= 0.6019592) {
         column = facing == EnumFacing.NORTH ? 1 : 0;
      } else if ((double)hitX >= 0.3907776 && (double)hitX <= 0.4482727) {
         column = facing == EnumFacing.NORTH ? 0 : 1;
      }

      if ((double)hitY >= 0.40430995 && (double)hitY <= 0.45151138) {
         row = 0;
      } else if ((double)hitY >= 0.32423588 && (double)hitY <= 0.4034006) {
         row = 1;
      } else if ((double)hitY >= 0.25311634 && (double)hitY <= 0.32804607) {
         row = 2;
      }

      return row != -1 && column != -1 ? row * 2 + column : -1;
   }

   protected RingTileEntity getNearbyRing(World world, int x, int y, int z) {
      HashMap<RingTileEntity, Double> foundRings = new HashMap();

      for(int ix = -10; ix <= 10; ++ix) {
         for(int iy = -4; iy <= 8; ++iy) {
            for(int iz = -10; iz <= 10; ++iz) {
               Block block = world.getBlock(x + ix, y + iy, z + iz);
               if (block instanceof RingBase) {
                  foundRings.put((RingTileEntity)world.getTileEntity(x + ix, y + iy, z + iz), Math.sqrt((double)(ix * ix + iy * iy + iz * iz)));
               }
            }
         }
      }

      double dist = Double.MAX_VALUE;
      RingTileEntity tile = null;

      for(Map.Entry<RingTileEntity, Double> entry : foundRings.entrySet()) {
         if ((Double)entry.getValue() < dist) {
            dist = (Double)entry.getValue();
            tile = (RingTileEntity)entry.getKey();
         }
      }

      return tile;
   }

   public BlockPos getPos(int x, int y, int z) {
      return new BlockPos(x, y, z);
   }

   public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
      return true;
   }
}
