package gcewing.sg;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class NaquadahOreWorldGen implements IWorldGenerator {
   static int genUnderLavaOdds = 4;
   static int maxNodesUnderLava = 8;
   static int genIsolatedOdds = 8;
   static int maxIsolatedNodes = 4;
   Random random;
   World world;
   Chunk chunk;
   int x0;
   int z0;
   Block stone;
   Block lava;
   Block naquadah;

   public NaquadahOreWorldGen() {
      this.stone = Blocks.stone;
      this.lava = Blocks.lava;
      this.naquadah = SGCraft.naquadahOre;
   }

   public static void configure(BaseConfiguration cfg) {
      genUnderLavaOdds = cfg.getInteger("naquadah", "genUnderLavaOdds", genUnderLavaOdds);
      maxNodesUnderLava = cfg.getInteger("naquadah", "maxNodesUnderLava", maxNodesUnderLava);
      genIsolatedOdds = cfg.getInteger("naquadah", "genIsolatedOdds", genIsolatedOdds);
      maxIsolatedNodes = cfg.getInteger("naquadah", "maxIsolatedNodes", maxIsolatedNodes);
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
      this.random = random;
      this.world = world;
      this.x0 = chunkX * 16;
      this.z0 = chunkZ * 16;
      this.chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
      this.generateChunk();
   }

   public void regenerate(Chunk chunk) {
      this.chunk = chunk;
      this.world = chunk.worldObj;
      int chunkX = chunk.xPosition;
      int chunkZ = chunk.zPosition;
      long worldSeed = this.world.getSeed();
      this.random = new Random(worldSeed);
      long xSeed = this.random.nextLong() >> 3;
      long zSeed = this.random.nextLong() >> 3;
      this.random.setSeed(xSeed * (long)chunkX + zSeed * (long)chunkZ ^ worldSeed);
      this.x0 = chunkX * 16;
      this.z0 = chunkZ * 16;
      this.generateChunk();
   }

   Block getBlock(int x, int y, int z) {
      return this.chunk.getBlock(x, y, z);
   }

   void setBlock(int x, int y, int z, Block id) {
      this.chunk.func_150807_a(x, y, z, id, 0);
   }

   void generateNode(Block id, int x, int y, int z, int sx, int sy, int sz) {
      int dx = this.random.nextInt(sx);
      int dy = this.random.nextInt(sy);
      int dz = this.random.nextInt(sz);
      int h = this.world.getHeight();

      for(int i = x; i <= x + dx; ++i) {
         for(int j = y; j <= y + dy; ++j) {
            for(int k = z; k <= z + dz; ++k) {
               if (i < 16 && j < h && k < 16 && this.getBlock(i, j, k) == this.stone) {
                  this.setBlock(i, j, k, id);
               }
            }
         }
      }

   }

   boolean odds(int n) {
      return this.random.nextInt(n) == 0;
   }

   void generateChunk() {
      SGChunkData.forChunk(this.chunk).oresGenerated = true;
      if (this.odds(genUnderLavaOdds)) {
         int n = this.random.nextInt(maxNodesUnderLava) + 1;

         for(int i = 0; i < n; ++i) {
            int x = this.random.nextInt(16);
            int z = this.random.nextInt(16);

            for(int y = 0; y < 64; ++y) {
               if (this.getBlock(x, y, z) == this.stone && this.getBlock(x, y + 1, z) == this.lava) {
                  this.generateNode(this.naquadah, x, y, z, 3, 1, 3);
               }
            }
         }
      }

      if (this.odds(genIsolatedOdds)) {
         int n = this.random.nextInt(maxIsolatedNodes) + 1;

         for(int i = 0; i < n; ++i) {
            int x = this.random.nextInt(16);
            int y = this.random.nextInt(64);
            int z = this.random.nextInt(16);
            if (this.getBlock(x, y, z) == this.stone) {
               this.generateNode(this.naquadah, x, y, z, 2, 2, 2);
            }
         }
      }

   }
}
