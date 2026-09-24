package gcewing.sg;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class BaseModClient<MOD extends BaseMod<? extends BaseModClient>> implements IGuiHandler {
   MOD base;
   boolean customRenderingRequired;
   Map<Integer, Class<? extends GuiScreen>> screenClasses = new HashMap();
   protected Map<BaseMod.IBlock, ICustomRenderer> blockRenderers = new HashMap();
   protected Map<Item, ICustomRenderer> itemRenderers = new HashMap();
   protected Map<IBlockState, ICustomRenderer> stateRendererCache = new HashMap();
   protected TextureCache[] textureCaches = new TextureCache[2];
   public static EnumWorldBlockLayer[][] passLayers;
   protected BaseModClient<MOD>.BlockRenderDispatcher blockRenderDispatcher;
   protected BaseModClient<MOD>.ItemRenderDispatcher itemRenderDispatcher;
   protected static BaseGLRenderTarget glTarget;
   protected static Trans3 entityTrans;
   protected static Trans3 equippedTrans;
   protected static Trans3 firstPersonTrans;
   protected static Trans3 inventoryTrans;

   public BaseModClient(MOD mod) {
      for(int i = 0; i < 2; ++i) {
         this.textureCaches[i] = new TextureCache();
      }

      this.base = mod;
      MinecraftForge.EVENT_BUS.register(this);
      FMLCommonHandler.instance().bus().register(this);
   }

   public void preInit(FMLPreInitializationEvent e) {
      this.registerSavedVillagerSkins();

      for(BaseSubsystem sub : this.base.subsystems) {
         sub.registerBlockRenderers();
         sub.registerItemRenderers();
      }

      this.registerDefaultRenderers();
      this.removeUnusedDefaultTextureNames();
   }

   public void init(FMLInitializationEvent e) {
   }

   public void postInit(FMLPostInitializationEvent e) {
      for(BaseSubsystem sub : this.base.subsystems) {
         sub.registerModelLocations();
         sub.registerTileEntityRenderers();
         sub.registerEntityRenderers();
         sub.registerScreens();
         sub.registerOtherClient();
      }

   }

   void registerSavedVillagerSkins() {
      VillagerRegistry reg = VillagerRegistry.instance();

      for(BaseMod.VSBinding b : this.base.registeredVillagers) {
         reg.registerVillagerSkin(b.id, b.object);
      }

   }

   void registerOther() {
   }

   void registerScreens() {
   }

   public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
      this.addScreen(id.ordinal(), cls);
   }

   public void addScreen(int id, Class<? extends GuiScreen> cls) {
      if (this.screenClasses.containsKey(id)) {
         throw new RuntimeException("Duplicate screen registration with ID " + id);
      } else {
         this.screenClasses.put(id, cls);
      }
   }

   protected void registerBlockRenderers() {
   }

   protected void registerItemRenderers() {
   }

   protected void registerEntityRenderers() {
   }

   protected void registerTileEntityRenderers() {
   }

   public void addTileEntityRenderer(Class<? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
      ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
   }

   public void addEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
      RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
   }

   public void addEntityRenderer(Class<? extends Entity> entityClass, Class<? extends Render> rendererClass) {
      Render renderer;
      try {
         renderer = (Render)rendererClass.newInstance();
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException(e);
      }

      this.addEntityRenderer(entityClass, renderer);
   }

   protected void registerDefaultRenderers() {
      for(Block block : this.base.registeredBlocks) {
         Item item = Item.getItemFromBlock(block);
         if (block instanceof BaseMod.IBlock) {
            if (!this.blockRenderers.containsKey(block)) {
               String name = ((BaseMod.IBlock)block).getQualifiedRendererClassName();
               if (name != null) {
                  try {
                     Class cls = Class.forName(name);
                     this.addBlockRenderer((BaseMod.IBlock)block, (ICustomRenderer)cls.newInstance());
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }

            if (this.blockNeedsCustomRendering(block)) {
               this.installCustomBlockRenderDispatcher((BaseMod.IBlock)block);
               this.installCustomItemRenderDispatcher(item);
            }
         }

         if (this.itemNeedsCustomRendering(item)) {
            this.installCustomItemRenderDispatcher(item);
         }
      }

      for(Item item : this.base.registeredItems) {
         if (this.itemNeedsCustomRendering(item)) {
            this.installCustomItemRenderDispatcher(item);
         }
      }

   }

   protected void installCustomBlockRenderDispatcher(BaseMod.IBlock block) {
      block.setRenderType(this.getCustomBlockRenderType());
   }

   protected void installCustomItemRenderDispatcher(Item item) {
      if (item != null) {
         MinecraftForgeClient.registerItemRenderer(item, this.getItemRenderDispatcher());
      }

   }

   protected void removeUnusedDefaultTextureNames() {
      for(Block block : this.base.registeredBlocks) {
         if (this.blockNeedsCustomRendering(block)) {
            block.setBlockTextureName("minecraft:stone");
         }
      }

      for(Item item : this.base.registeredItems) {
         if (this.itemNeedsCustomRendering(item)) {
            item.setTextureName("minecraft:apple");
         }
      }

   }

   public static void openClientGui(GuiScreen gui) {
      FMLClientHandler.instance().getClient().displayGuiScreen(gui);
   }

   public ResourceLocation textureLocation(String path) {
      return this.base.resourceLocation("textures/" + path);
   }

   public void bindTexture(String path) {
      bindTexture(this.textureLocation(path));
   }

   public static void bindTexture(ResourceLocation rsrc) {
      TextureManager tm = Minecraft.getMinecraft().getTextureManager();
      tm.bindTexture(rsrc);
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      return this.base.getServerGuiElement(id, player, world, x, y, z);
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      return this.getClientGuiElement(id, player, world, new BlockPos(x, y, z));
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, BlockPos pos) {
      int param = id >> 16;
      id &= 65535;
      Object result = null;
      Class scrnCls = (Class)this.screenClasses.get(id);
      if (scrnCls != null) {
         Class contCls = (Class)this.base.containerClasses.get(id);
         if (contCls != null) {
            try {
               Constructor ctor = scrnCls.getConstructor(contCls);
               Object cont = this.base.createGuiElement(contCls, player, world, pos, param);
               if (cont != null) {
                  try {
                     result = ctor.newInstance(cont);
                  } catch (Exception e) {
                     BaseMod var10000 = this.base;
                     BaseMod.reportExceptionCause(e);
                     return null;
                  }
               }
            } catch (NoSuchMethodException var13) {
            }
         }

         if (result == null) {
            result = this.base.createGuiElement(scrnCls, player, world, pos, param);
         }
      } else {
         result = this.getGuiScreen(id, player, world, pos, param);
      }

      this.base.setModOf(result);
      return result;
   }

   GuiScreen getGuiScreen(int id, EntityPlayer player, World world, BlockPos pos, int param) {
      return null;
   }

   public void addBlockRenderer(BaseMod.IBlock block, ICustomRenderer renderer) {
      this.blockRenderers.put(block, renderer);
      this.customRenderingRequired = true;
      Item item = Item.getItemFromBlock((Block)block);
      if (item != null && !this.itemRenderers.containsKey(item)) {
         this.addItemRenderer(item, renderer);
      }

   }

   public void addItemRenderer(Item item, ICustomRenderer renderer) {
      this.itemRenderers.put(item, renderer);
   }

   protected boolean blockNeedsCustomRendering(Block block) {
      return this.blockRenderers.containsKey(block) || this.specifiesTextures(block);
   }

   protected boolean itemNeedsCustomRendering(Item item) {
      return this.itemRenderers.containsKey(item) || this.specifiesTextures(item);
   }

   protected boolean specifiesTextures(Object obj) {
      return obj instanceof BaseMod.ITextureConsumer && ((BaseMod.ITextureConsumer)obj).getTextureNames() != null;
   }

   protected int getCustomBlockRenderType() {
      return this.getBlockRenderDispatcher().renderID;
   }

   protected BaseModClient<MOD>.BlockRenderDispatcher getBlockRenderDispatcher() {
      if (this.blockRenderDispatcher == null) {
         this.blockRenderDispatcher = new BlockRenderDispatcher();
      }

      return this.blockRenderDispatcher;
   }

   protected BaseModClient<MOD>.ItemRenderDispatcher getItemRenderDispatcher() {
      if (this.itemRenderDispatcher == null) {
         this.itemRenderDispatcher = new ItemRenderDispatcher();
      }

      return this.itemRenderDispatcher;
   }

   protected ICustomRenderer getCustomBlockRenderer(IBlockAccess world, BlockPos pos, IBlockState state) {
      BaseBlock block = (BaseBlock)state.getBlock();
      ICustomRenderer rend = (ICustomRenderer)this.blockRenderers.get(block);
      if (rend == null && block instanceof BaseMod.IBlock) {
         IBlockState astate = block.getActualState(state, world, pos);
         rend = this.getModelRendererForState(astate);
      }

      return rend;
   }

   protected ICustomRenderer getModelRendererForSpec(BaseMod.ModelSpec spec, int textureType) {
      IModel model = this.getModel(spec.modelName);
      ITexture[] textures = new ITexture[spec.textureNames.length];

      for(int i = 0; i < textures.length; ++i) {
         textures[i] = this.getTexture(textureType, spec.textureNames[i]);
      }

      return new BaseModelRenderer(model, spec.origin, textures);
   }

   protected ICustomRenderer getModelRendererForState(IBlockState astate) {
      ICustomRenderer rend = (ICustomRenderer)this.stateRendererCache.get(astate);
      if (rend == null) {
         Block block = astate.getBlock();
         if (block instanceof BaseMod.IBlock) {
            BaseMod.ModelSpec spec = ((BaseMod.IBlock)block).getModelSpec(astate);
            if (spec != null) {
               rend = this.getModelRendererForSpec(spec, 0);
               this.stateRendererCache.put(astate, rend);
            }
         }
      }

      return rend;
   }

   protected ICustomRenderer getModelRendererForItemStack(ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof BaseMod.IItem) {
         BaseMod.ModelSpec spec = ((BaseMod.IItem)item).getModelSpec(stack);
         if (spec != null) {
            return this.getModelRendererForSpec(spec, 1);
         }
      }

      if (item instanceof ItemBlock) {
         Block block = ((ItemBlock)item).field_150939_a;
         if (block instanceof BaseBlock) {
            IBlockState state = BaseBlockUtils.getBlockStateFromItemStack(stack);
            BaseMod.ModelSpec spec = ((BaseMod.IBlock)block).getModelSpec(state);
            return this.getModelRendererForSpec(spec, 0);
         }
      }

      return null;
   }

   public void renderBlockUsingModelSpec(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target, EnumWorldBlockLayer layer, Trans3 t) {
      ICustomRenderer rend = this.getModelRendererForState(state);
      if (rend != null) {
         rend.renderBlock(world, pos, state, target, layer, t);
      }

   }

   public void renderItemStackUsingModelSpec(ItemStack stack, IRenderTarget target, Trans3 t) {
      ICustomRenderer rend = this.getModelRendererForItemStack(stack);
      if (rend != null) {
         rend.renderItemStack(stack, target, t);
      }

   }

   public IModel getModel(String name) {
      return this.base.getModel(name);
   }

   public ITexture getTexture(int type, String name) {
      ResourceLocation loc = this.base.resourceLocation(name);
      return (ITexture)this.textureCaches[type].get(loc);
   }

   public IIcon getIcon(int type, String name) {
      return ((BaseTexture.Sprite)this.getTexture(type, name)).icon;
   }

   @SubscribeEvent
   public void onTextureStitchEventPre(TextureStitchEvent.Pre e) {
      int type = e.map.getTextureType();
      if (type >= 0 && type <= 1) {
         TextureCache cache = this.textureCaches[type];
         cache.clear();
         switch (type) {
            case 0:
               for(Block block : this.base.registeredBlocks) {
                  this.registerSprites(e.map, cache, block);
               }
               break;
            case 1:
               for(Item item : this.base.registeredItems) {
                  this.registerSprites(e.map, cache, item);
               }
         }
      }

   }

   protected void registerSprites(TextureMap reg, TextureCache cache, Object obj) {
      if (obj instanceof BaseMod.ITextureConsumer) {
         String[] names = ((BaseMod.ITextureConsumer)obj).getTextureNames();
         if (names != null) {
            this.customRenderingRequired = true;

            for(String name : names) {
               ResourceLocation loc = this.base.resourceLocation(name);
               if (cache.get(loc) == null) {
                  IIcon icon = reg.registerIcon(loc.toString());
                  ITexture texture = BaseTexture.fromSprite(icon);
                  cache.put(loc, texture);
               }
            }
         }
      }

   }

   static {
      passLayers = new EnumWorldBlockLayer[][]{{EnumWorldBlockLayer.SOLID, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.CUTOUT, EnumWorldBlockLayer.TRANSLUCENT}, {EnumWorldBlockLayer.SOLID, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.CUTOUT}, {EnumWorldBlockLayer.TRANSLUCENT}};
      glTarget = new BaseGLRenderTarget();
      entityTrans = Trans3.blockCenter;
      equippedTrans = Trans3.blockCenter;
      firstPersonTrans = Trans3.blockCenterSideTurn(0, 3);
      inventoryTrans = Trans3.blockCenter;
   }

   public static class TextureCache extends HashMap<ResourceLocation, ITexture> {
   }

   protected class BlockRenderDispatcher implements ISimpleBlockRenderingHandler {
      protected int renderID = RenderingRegistry.getNextAvailableRenderId();

      public BlockRenderDispatcher() {
         RenderingRegistry.registerBlockHandler(this.renderID, this);
      }

      public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
         boolean result = false;
         BlockPos pos = new BlockPos(x, y, z);
         int meta = world.getBlockMetadata(x, y, z);
         BaseBlock baseBlock = (BaseBlock)block;
         IBlockState state = baseBlock.getStateFromMeta(meta);
         ICustomRenderer renderer = BaseModClient.this.getCustomBlockRenderer(world, pos, state);
         if (renderer != null) {
            int pass = ForgeHooksClient.getWorldRenderPass();

            for(EnumWorldBlockLayer layer : BaseModClient.passLayers[pass + 1]) {
               if (baseBlock.canRenderInLayer(layer)) {
                  BaseWorldRenderTarget target = new BaseWorldRenderTarget(world, pos, Tessellator.instance, rb.overrideBlockTexture);
                  Trans3 t = Trans3.blockCenter(pos);
                  renderer.renderBlock(world, pos, state, target, layer, t);
                  if (target.end()) {
                     result = true;
                  }
               }
            }
         }

         return result;
      }

      public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
      }

      public boolean shouldRender3DInInventory(int modelId) {
         return true;
      }

      public int getRenderId() {
         return this.renderID;
      }
   }

   protected class ItemRenderDispatcher implements IItemRenderer {
      public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
         return type != ItemRenderType.FIRST_PERSON_MAP;
      }

      public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
         return true;
      }

      public void renderItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data) {
         ICustomRenderer renderer = (ICustomRenderer)BaseModClient.this.itemRenderers.get(stack.getItem());
         if (renderer == null) {
            renderer = BaseModClient.this.getModelRendererForItemStack(stack);
         }

         if (renderer != null) {
            Trans3 t;
            switch (type) {
               case ENTITY:
                  t = BaseModClient.entityTrans;
                  break;
               case EQUIPPED:
                  t = BaseModClient.equippedTrans;
                  break;
               case EQUIPPED_FIRST_PERSON:
                  t = BaseModClient.firstPersonTrans;
                  break;
               case INVENTORY:
                  t = BaseModClient.inventoryTrans;
                  GL11.glEnable(3042);
                  GL11.glEnable(2884);
                  OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                  break;
               default:
                  return;
            }

            BaseModClient.glTarget.start(false);
            renderer.renderItemStack(stack, BaseModClient.glTarget, t);
            BaseModClient.glTarget.finish();
            switch (type) {
               case INVENTORY:
                  GL11.glDisable(3042);
                  GL11.glDisable(2884);
            }
         }

      }
   }

   public interface ICustomRenderer {
      void renderBlock(IBlockAccess var1, BlockPos var2, IBlockState var3, IRenderTarget var4, EnumWorldBlockLayer var5, Trans3 var6);

      void renderItemStack(ItemStack var1, IRenderTarget var2, Trans3 var3);
   }

   public interface IModel {
      AxisAlignedBB getBounds();

      void addBoxesToList(Trans3 var1, List var2);

      void render(Trans3 var1, IRenderTarget var2, ITexture... var3);
   }

   public interface IRenderTarget {
      boolean isRenderingBreakEffects();

      void setTexture(ITexture var1);

      void setColor(double var1, double var3, double var5, double var7);

      void setNormal(Vector3 var1);

      void beginTriangle();

      void beginQuad();

      void addVertex(Vector3 var1, double var2, double var4);

      void addProjectedVertex(Vector3 var1, EnumFacing var2);

      void endFace();
   }

   public interface ITexture {
      ResourceLocation location();

      int tintIndex();

      double red();

      double green();

      double blue();

      double interpolateU(double var1);

      double interpolateV(double var1);

      boolean isEmissive();

      boolean isProjected();

      boolean isSolid();

      ITexture tinted(int var1);

      ITexture colored(double var1, double var3, double var5);

      ITexture projected();

      ITexture emissive();

      ITiledTexture tiled(int var1, int var2);
   }

   public interface ITiledTexture extends ITexture {
      ITexture tile(int var1, int var2);
   }
}
