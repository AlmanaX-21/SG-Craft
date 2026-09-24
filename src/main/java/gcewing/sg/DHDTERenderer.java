package gcewing.sg;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

class DHDTERenderer extends BaseTileEntityRenderer {
   BaseModClient.IModel model;
   BaseModClient.ITexture[] buttonTextures;
   BaseModClient.ITexture[] buttonTextures2;
   BaseModClient.ITexture[] buttonTextures3;
   BaseModClient.ITexture[] buttonTextures4;
   BaseModClient.ITexture[] buttonTextures5;
   BaseModClient.ITexture[] textures;
   BaseModClient.ITexture[] textures2;
   BaseModClient.ITexture[] textures3;
   BaseModClient.ITexture[] textures4;
   BaseModClient.ITexture[] textures5;
   static final int buttonTextureIndex = 3;
   protected static final IModelCustom dhdModel;

   public DHDTERenderer() {
      SGCraft mod = SGCraft.mod;
      ResourceLocation ttLoc2 = mod.textureLocation("tileentity/dhd_top1.png");
      ResourceLocation stLoc2 = mod.textureLocation("tileentity/dhd_side1.png");
      ResourceLocation dtLoc2 = mod.textureLocation("tileentity/dhd_detail1.png");
      BaseModClient.ITiledTexture detail2 = (new BaseTexture.Image(dtLoc2)).tiled(2, 2);
      this.textures2 = new BaseModClient.ITexture[]{new BaseTexture.Image(ttLoc2), new BaseTexture.Image(stLoc2), detail2.tile(1, 1), null};
      ResourceLocation ttLoc3 = mod.textureLocation("tileentity/dhd_top2.png");
      ResourceLocation stLoc3 = mod.textureLocation("tileentity/dhd_side2.png");
      ResourceLocation dtLoc3 = mod.textureLocation("tileentity/dhd_detail2.png");
      BaseModClient.ITiledTexture detail3 = (new BaseTexture.Image(dtLoc3)).tiled(2, 2);
      this.textures3 = new BaseModClient.ITexture[]{new BaseTexture.Image(ttLoc3), new BaseTexture.Image(stLoc3), detail3.tile(1, 1), null};
      ResourceLocation ttLoc4 = mod.textureLocation("tileentity/dhd_top3.png");
      ResourceLocation stLoc4 = mod.textureLocation("tileentity/dhd_side3.png");
      ResourceLocation dtLoc4 = mod.textureLocation("tileentity/dhd_detail3.png");
      BaseModClient.ITiledTexture detail4 = (new BaseTexture.Image(dtLoc4)).tiled(2, 2);
      this.textures4 = new BaseModClient.ITexture[]{new BaseTexture.Image(ttLoc4), new BaseTexture.Image(stLoc4), detail4.tile(1, 1), null};
      ResourceLocation ttLoc5 = mod.textureLocation("tileentity/dhd_top4.png");
      ResourceLocation stLoc5 = mod.textureLocation("tileentity/dhd_side4.png");
      ResourceLocation dtLoc5 = mod.textureLocation("tileentity/dhd_detail4.png");
      BaseModClient.ITiledTexture detail5 = (new BaseTexture.Image(dtLoc5)).tiled(2, 2);
      this.textures5 = new BaseModClient.ITexture[]{new BaseTexture.Image(ttLoc5), new BaseTexture.Image(stLoc5), detail5.tile(1, 1), null};
      ResourceLocation ttLoc = mod.textureLocation("tileentity/dhd_top0.png");
      ResourceLocation stLoc = mod.textureLocation("tileentity/dhd_side0.png");
      ResourceLocation dtLoc = mod.textureLocation("tileentity/dhd_detail0.png");
      BaseModClient.ITiledTexture detail = (new BaseTexture.Image(dtLoc)).tiled(2, 2);
      this.textures = new BaseModClient.ITexture[]{new BaseTexture.Image(ttLoc), new BaseTexture.Image(stLoc), detail.tile(1, 1), null};
      BaseModClient.ITexture button5 = detail5.tile(0, 0);
      this.buttonTextures5 = new BaseModClient.ITexture[]{button5.colored(0.3, 0.3, 0.3), button5.colored(0.09, 0.57, 0.62), button5.colored((double)1.0F, (double)1.0F, (double)1.0F).emissive()};
      BaseModClient.ITexture button4 = detail4.tile(0, 0);
      this.buttonTextures4 = new BaseModClient.ITexture[]{button4.colored(0.3, 0.3, 0.3), button4.colored((double)0.5F, (double)0.5F, (double)0.5F), button4.colored((double)1.0F, (double)1.0F, (double)1.0F).emissive()};
      BaseModClient.ITexture button3 = detail3.tile(0, 0);
      this.buttonTextures3 = new BaseModClient.ITexture[]{button3.colored(0.3, 0.3, 0.3), button3.colored(0.46, 0.55, 0.55), button3.colored(0.63, 0.81, 0.81).emissive()};
      BaseModClient.ITexture button2 = detail2.tile(0, 0);
      this.buttonTextures2 = new BaseModClient.ITexture[]{button2.colored(0.3, 0.3, 0.3), button2.colored(0.13, 0.14, 0.4), button2.colored((double)0.25F, 0.28, 0.8).emissive()};
      BaseModClient.ITexture button = detail.tile(0, 0);
      this.buttonTextures = new BaseModClient.ITexture[]{button.colored(0.3, 0.3, 0.3), button.colored((double)0.5F, (double)0.25F, (double)0.0F), button.colored((double)1.0F, (double)0.5F, (double)0.0F).emissive()};
      this.model = BaseModel.fromResource(mod.resourceLocation("models/dhd.smeg"));
      DHDTE.bounds = this.model.getBounds();
   }

   public void render(BaseTileEntity te, float dt, int destroyStage, Trans3 t, BaseModClient.IRenderTarget target) {
      DHDTE dte = (DHDTE)te;
      SGBaseTE gte = dte.getLinkedStargateTE(dte.typ);
      int i;
      if (gte == null) {
         i = 0;
      } else if (gte.isActive()) {
         i = 2;
      } else {
         i = 1;
      }

      if (dte.typ == 0) {
         this.textures[3] = this.buttonTextures[i];
         this.model.render(t.translate((double)0.0F, (double)-0.5F, (double)0.0F).scale(1.3), target, this.textures);
      } else if (dte.typ == 1) {
         this.textures2[3] = this.buttonTextures2[i];
         this.model.render(t.translate((double)0.0F, (double)-0.5F, (double)0.0F).scale(1.3), target, this.textures2);
      } else if (dte.typ == 2) {
         this.textures3[3] = this.buttonTextures3[i];
         this.model.render(t.translate((double)0.0F, (double)-0.5F, (double)0.0F).scale(1.3), target, this.textures3);
      } else if (dte.typ == 3) {
         this.textures4[3] = this.buttonTextures4[i];
         this.model.render(t.translate((double)0.0F, (double)-0.5F, (double)0.0F).scale(1.3), target, this.textures4);
      } else if (dte.typ == 4) {
         this.textures5[3] = this.buttonTextures5[i];
         this.model.render(t.translate((double)0.0F, (double)-0.5F, (double)0.0F).scale(1.3), target, this.textures5);
      }

   }

   static {
      dhdModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/block/DHD.obj"));
   }
}
