package gcewing.sg;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class FeatureUnderDesertPyramid extends StructureComponent {
   StructureComponent base;

   protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
   }

   protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
   }

   public FeatureUnderDesertPyramid() {
   }

   public FeatureUnderDesertPyramid(StructureComponent base) {
      super(0);
      this.base = base;
      StructureBoundingBox baseBox = base.getBoundingBox();
      int cx = baseBox.getCenterX();
      int cz = baseBox.getCenterZ();
      int bottom = baseBox.minY - 7;
      this.boundingBox = new StructureBoundingBox(cx - 5, bottom, cz - 5, cx + 5, bottom + 7, cz + 8);
      this.coordBaseMode = 0;
   }

   public boolean addComponentParts(World world, Random rand, StructureBoundingBox clip) {
      if (this.base == null) {
         return false;
      } else {
         StructureBoundingBox box = this.getBoundingBox();
         Block air = Blocks.air;
         Block sandstone = Blocks.sandstone;
         int smoothSandstone = 2;
         Block wool = Blocks.wool;
         int orange = 1;
         Block stairs = Blocks.sandstone_stairs;
         int stairsWest = 0;
         Block ladder = Blocks.ladder;
         int ladderSouth = 2;
         Block dhd = SGCraft.sgControllerBlock0;
         int dhdNorth = 0;
         Block sgBase = SGCraft.sgBaseBlock0;
         int sgBaseNorth = 0;
         Block sgRing = SGCraft.sgRingBlock0;
         this.fillWithBlocks(world, clip, 0, 0, 0, 10, 7, 10, sandstone, air, false);
         this.fillWithBlocks(world, clip, 4, 0, 11, 13, 7, 13, sandstone, air, false);
         this.fillWithAir(world, clip, 12, 7, 12, 12, 9, 12);
         this.fillWithAir(world, clip, 5, 1, 10, 5, 2, 11);
         this.placeBlockAtCurrentPosition(world, sandstone, 0, 12, 4, 12, clip);

         for(int i = 0; i < 4; ++i) {
            this.placeBlockAtCurrentPosition(world, stairs, stairsWest, 8 + i, 1 + i, 12, clip);
         }

         for(int i = 0; i < 3; ++i) {
            this.placeBlockAtCurrentPosition(world, ladder, ladderSouth, 12, 5 + i, 12, clip);
         }

         this.fillWithMetadataBlocks(world, clip, 0, 3, 0, 10, 3, 10, wool, orange, air, 0, true);
         this.fillWithMetadataBlocks(world, clip, 3, 4, 10, 7, 4, 10, wool, orange, air, 0, true);
         this.fillWithMetadataBlocks(world, clip, 3, 0, 4, 3, 0, 6, wool, orange, air, 0, true);
         this.fillWithMetadataBlocks(world, clip, 7, 0, 4, 7, 0, 6, wool, orange, air, 0, true);
         this.fillWithMetadataBlocks(world, clip, 4, 0, 3, 6, 0, 3, wool, orange, air, 0, true);
         this.fillWithMetadataBlocks(world, clip, 4, 0, 7, 6, 0, 7, wool, orange, air, 0, true);
         this.placeBlockAtCurrentPosition(world, wool, orange, 5, 0, 5, clip);
         this.fillWithMetadataBlocks(world, clip, 4, 1, 10, 6, 3, 10, sandstone, smoothSandstone, air, 0, true);

         for(int i = -2; i <= 2; ++i) {
            for(int j = 0; j <= 4; ++j) {
               Block id;
               int data;
               if (i == 0 && j == 0) {
                  id = sgBase;
                  data = sgBaseNorth;
               } else if (i != -2 && i != 2 && j != 0 && j != 4) {
                  id = air;
                  data = 0;
               } else {
                  id = sgRing;
                  data = i + j + 1 & 1;
               }

               this.placeBlockAtCurrentPosition(world, id, data, 5 + i, 1 + j, 2, clip);
            }
         }

         int baseX = box.minX + 5;
         int baseY = box.minY + 1;
         int baseZ = box.minZ + 2;
         SGBaseTE te = (SGBaseTE)world.getTileEntity(baseX, baseY, baseZ);
         if (te != null) {
            te.hasChevronUpgrade = true;
         }

         this.placeBlockAtCurrentPosition(world, dhd, dhdNorth, 5, 1, 7, clip);
         return true;
      }
   }
}
