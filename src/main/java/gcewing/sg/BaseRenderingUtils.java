package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class BaseRenderingUtils {
   protected static AltBlockAccess altBlockAccess = new AltBlockAccess();
   protected static RenderBlocks altRenderBlocks;

   public static void renderAlternateBlock(BaseMod mod, IBlockAccess world, BlockPos pos, IBlockState state, BaseModClient.IRenderTarget target) {
      Block block = state.getBlock();
      int meta = BaseBlockUtils.getMetaFromBlockState(state);
      renderAlternateBlock(world, pos.x, pos.y, pos.z, block, meta, target);
   }

   public static void renderAlternateBlock(IBlockAccess world, int x, int y, int z, Block block, int meta, BaseModClient.IRenderTarget target) {
      if (!block.hasTileEntity(meta)) {
         altBlockAccess.setup(world, x, y, z, meta);
         altRenderBlocks.renderBlockAllFaces(block, x, y, z);
         ((BaseWorldRenderTarget)target).setRenderingOccurred();
      }

   }

   static {
      altRenderBlocks = new RenderBlocks(altBlockAccess);
   }

   protected static class AltBlockAccess implements IBlockAccess {
      IBlockAccess base;
      int targetX;
      int targetY;
      int targetZ;
      int metadata;

      void setup(IBlockAccess base, int x, int y, int z, int data) {
         this.base = base;
         this.targetX = x;
         this.targetY = y;
         this.targetZ = z;
         this.metadata = data;
      }

      public Block getBlock(int x, int y, int z) {
         return this.base.getBlock(x, y, z);
      }

      public TileEntity getTileEntity(int x, int y, int z) {
         return this.base.getTileEntity(x, y, z);
      }

      public int getLightBrightnessForSkyBlocks(int x, int y, int z, int w) {
         return this.base.getLightBrightnessForSkyBlocks(x, y, z, w);
      }

      public int getBlockMetadata(int x, int y, int z) {
         return x == this.targetX && y == this.targetY && z == this.targetZ ? this.metadata : this.base.getBlockMetadata(x, y, z);
      }

      public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
         return this.base.isBlockProvidingPowerTo(x, y, z, side);
      }

      public boolean isAirBlock(int x, int y, int z) {
         return this.base.isAirBlock(x, y, z);
      }

      public BiomeGenBase getBiomeGenForCoords(int x, int z) {
         return this.base.getBiomeGenForCoords(x, z);
      }

      public int getHeight() {
         return this.base.getHeight();
      }

      public boolean extendedLevelsInChunkCache() {
         return this.base.extendedLevelsInChunkCache();
      }

      public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
         return this.base.isSideSolid(x, y, z, side, _default);
      }
   }
}
