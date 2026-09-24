package gcewing.sg;

import cpw.mods.fml.common.registry.GameRegistry;
import gcewing.sg.te.RingTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class RingBase extends BlockContainer {
   public RingBase() {
      super(Material.iron);
      GameRegistry.registerTileEntity(RingTileEntity.class, "ringTileEntity");
      this.setLightOpacity(15);
      this.setLightLevel(15.0F);
   }

   public RingBase(Material material) {
      super(material);
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderBlockPass() {
      return 1;
   }

   public boolean canRenderInPass(int pass) {
      ForgeHooksClient.setRenderPass(pass);
      return true;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public float getAmbientOcclusionLightValue() {
      return super.getAmbientOcclusionLightValue();
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new RingTileEntity();
   }

   public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
      if (!p_149727_1_.isRemote) {
         ((RingTileEntity)p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_)).startTeleportedFrom((RingTileEntity)null);
      }

      return super.onBlockActivated(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, p_149727_5_, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
   }
}
