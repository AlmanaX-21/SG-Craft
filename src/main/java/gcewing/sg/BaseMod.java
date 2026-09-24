package gcewing.sg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public abstract class BaseMod<CLIENT extends BaseModClient<? extends BaseMod>> extends BaseSubsystem implements IGuiHandler {
   protected Map<ResourceLocation, BaseModClient.IModel> modelCache = new HashMap();
   public String modID;
   public BaseConfiguration config;
   public String modPackage;
   public String assetKey;
   public String blockDomain;
   public String itemDomain;
   public String resourceDir;
   public URL resourceURL;
   public CLIENT client;
   public IGuiHandler proxy;
   public boolean serverSide;
   public boolean clientSide;
   public CreativeTabs creativeTab;
   public File cfgFile;
   public List<Block> registeredBlocks = new ArrayList();
   public List<Item> registeredItems = new ArrayList();
   public List<BaseSubsystem> subsystems = new ArrayList();
   public boolean debugGui = false;
   public boolean debugBlockRegistration = false;
   public boolean debugCreativeTabs = false;
   public List<VSBinding> registeredVillagers = new ArrayList();
   protected int nextGuiId = 1000;
   Map<Class<? extends Container>, Class<? extends TileEntity>> containerTEClasses = new HashMap();
   Map<Object, Integer> objectToGuiId = new HashMap();
   Map<Integer, Class<? extends Container>> containerClasses = new HashMap();

   public void setModOf(Object obj) {
      if (obj instanceof ISetMod) {
         ((ISetMod)obj).setMod(this);
      }

   }

   public String resourcePath(String fileName) {
      return this.resourceDir + fileName;
   }

   public BaseMod() {
      Class modClass = this.getClass();
      this.modPackage = modClass.getPackage().getName();
      this.modID = getModID(modClass);
      this.assetKey = this.modID.toLowerCase();
      this.blockDomain = this.assetKey;
      this.itemDomain = this.assetKey;
      String resourceRelDir = "assets/" + this.assetKey + "/";
      this.resourceDir = "/" + resourceRelDir;
      this.resourceURL = this.getClass().getClassLoader().getResource(resourceRelDir);
      this.subsystems.add(this);
      this.creativeTab = CreativeTabs.tabMisc;
   }

   static String getModID(Class cls) {
      Annotation ann = cls.getAnnotation(Mod.class);
      return ann instanceof Mod ? ((Mod)ann).modid() : "<unknown>";
   }

   public static boolean isModLoaded(String modid) {
      return Loader.isModLoaded(modid);
   }

   public void preInit(FMLPreInitializationEvent e) {
      this.serverSide = e.getSide().isServer();
      this.clientSide = e.getSide().isClient();
      if (this.clientSide) {
         this.client = this.initClient();
         this.proxy = this.client;
      }

      this.cfgFile = e.getSuggestedConfigurationFile();
      this.loadConfig();
      this.configure();

      for(BaseSubsystem sub : this.subsystems) {
         if (sub != this) {
            sub.preInit(e);
         }

         sub.configure(this.config);
         sub.registerBlocks();
         sub.registerTileEntities();
         sub.registerItems();
         sub.registerOres();
         sub.registerWorldGenerators();
         sub.registerContainers();
         sub.registerEntities();
         sub.registerVillagers();
      }

      if (this.client != null) {
         this.client.preInit(e);
      }

   }

   public void init(FMLInitializationEvent e) {
      MinecraftForge.EVENT_BUS.register(this);
      FMLCommonHandler.instance().bus().register(this);
      if (this.client != null) {
         this.client.init(e);
      }

      for(BaseSubsystem sub : this.subsystems) {
         if (sub != this) {
            sub.init(e);
         }
      }

   }

   public void postInit(FMLPostInitializationEvent e) {
      for(BaseSubsystem sub : this.subsystems) {
         if (sub != this) {
            sub.postInit(e);
         }

         sub.registerRecipes();
         sub.registerRandomItems();
         sub.registerOther();
      }

      if (this.client != null) {
         this.client.postInit(e);
      }

      if (this.proxy == null) {
         this.proxy = this;
      }

      NetworkRegistry.INSTANCE.registerGuiHandler(this, this.proxy);
      this.saveConfig();
   }

   void loadConfig() {
      this.config = new BaseConfiguration(this.cfgFile);
   }

   void saveConfig() {
      if (this.config.extended) {
         this.config.save();
      }

   }

   String qualifiedName(String name) {
      return this.modPackage + "." + name;
   }

   protected void registerScreens() {
      if (this.client != null) {
         this.client.registerScreens();
      }

   }

   protected void registerBlockRenderers() {
      if (this.client != null) {
         this.client.registerBlockRenderers();
      }

   }

   protected void registerItemRenderers() {
      if (this.client != null) {
         this.client.registerItemRenderers();
      }

   }

   protected void registerEntityRenderers() {
      if (this.client != null) {
         this.client.registerEntityRenderers();
      }

   }

   protected void registerTileEntityRenderers() {
      if (this.client != null) {
         this.client.registerTileEntityRenderers();
      }

   }

   protected void registerOtherClient() {
      if (this.client != null) {
         this.client.registerOther();
      }

   }

   void configure() {
   }

   CLIENT initClient() {
      return (CLIENT)(new BaseModClient(this));
   }

   public BaseSubsystem integrateWithMod(String modId, String subsystemClassName) {
      return isModLoaded(modId) ? this.loadSubsystem(subsystemClassName) : null;
   }

   public BaseSubsystem integrateWithClass(String className, String subsystemClassName) {
      return classAvailable(className) ? this.loadSubsystem(subsystemClassName) : null;
   }

   public BaseSubsystem loadSubsystem(String className) {
      BaseSubsystem sub = this.newSubsystem(className);
      sub.mod = this;
      sub.client = this.client;
      this.subsystems.add(sub);
      return sub;
   }

   protected BaseSubsystem newSubsystem(String className) {
      try {
         return (BaseSubsystem)Class.forName(className).newInstance();
      } catch (Exception exc) {
         throw new RuntimeException(exc);
      }
   }

   public static boolean classAvailable(String name) {
      try {
         Class.forName(name);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public Item newItem(String name) {
      return this.newItem(name, Item.class);
   }

   public <ITEM extends Item> ITEM newItem(String name, Class<ITEM> cls) {
      ITEM item;
      try {
         Constructor<ITEM> ctor = cls.getConstructor();
         item = (ITEM)(ctor.newInstance());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      return (ITEM)this.addItem(item, name);
   }

   public <ITEM extends Item> ITEM addItem(ITEM item, String name) {
      String qualName = this.itemDomain + ":" + name;
      item.setUnlocalizedName(qualName);
      item.setTextureName(this.assetKey + ":" + name);
      GameRegistry.registerItem(item, name);
      if (this.creativeTab != null) {
         item.setCreativeTab(this.creativeTab);
      }

      this.registeredItems.add(item);
      return item;
   }

   public Block newBlock(String name) {
      return this.newBlock(name, Block.class);
   }

   public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls) {
      return (BLOCK)this.newBlock(name, cls, (Class)null);
   }

   public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, Class itemClass) {
      BLOCK block;
      try {
         Constructor<BLOCK> ctor = cls.getConstructor();
         block = (BLOCK)(ctor.newInstance());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      return (BLOCK)this.addBlock(block, name, itemClass);
   }

   public <BLOCK extends Block> BLOCK addBlock(String name, BLOCK block) {
      return (BLOCK)this.addBlock(block, name);
   }

   public <BLOCK extends Block> BLOCK addBlock(BLOCK block, String name) {
      return (BLOCK)this.addBlock(block, name, (Class)null);
   }

   public <BLOCK extends Block> BLOCK addBlock(BLOCK block, String name, Class itemClass) {
      String qualName = this.blockDomain + ":" + name;
      block.setBlockName(qualName);
      block.setBlockTextureName(this.assetKey + ":" + name);
      itemClass = this.getItemClassForBlock(block, itemClass);
      GameRegistry.registerBlock(block, itemClass, name);
      if (this.creativeTab != null) {
         block.setCreativeTab(this.creativeTab);
      }

      if (block instanceof BaseBlock) {
         ((BaseBlock)block).mod = this;
      }

      this.registeredBlocks.add(block);
      return block;
   }

   protected Class defaultItemClassForBlock(Block block) {
      return block instanceof IBlock ? ((IBlock)block).getDefaultItemClass() : ItemBlock.class;
   }

   protected Class getItemClassForBlock(Block block, Class suppliedClass) {
      Class baseClass = this.defaultItemClassForBlock(block);
      if (suppliedClass == null) {
         return baseClass;
      } else if (!baseClass.isAssignableFrom(suppliedClass)) {
         throw new RuntimeException(String.format("Block item class %s for %s does not extend %s\n", suppliedClass.getName(), block.getUnlocalizedName(), baseClass.getName()));
      } else {
         return suppliedClass;
      }
   }

   public void addOre(String name, Block block) {
      OreDictionary.registerOre(name, new ItemStack(block));
   }

   public void addOre(String name, Item item) {
      OreDictionary.registerOre(name, item);
   }

   public void addOre(String name, ItemStack stack) {
      OreDictionary.registerOre(name, stack);
   }

   public static boolean blockMatchesOre(Block block, String name) {
      return stackMatchesOre(new ItemStack(block), name);
   }

   public static boolean itemMatchesOre(Item item, String name) {
      return stackMatchesOre(new ItemStack(item), name);
   }

   public static boolean stackMatchesOre(ItemStack stack, String name) {
      int id2 = OreDictionary.getOreID(name);

      for(int id1 : OreDictionary.getOreIDs(stack)) {
         if (id1 == id2) {
            return true;
         }
      }

      return false;
   }

   public void newRecipe(Item product, int qty, Object... params) {
      this.newRecipe(new ItemStack(product, qty), params);
   }

   public void newRecipe(Block product, int qty, Object... params) {
      this.newRecipe(new ItemStack(product, qty), params);
   }

   public void newRecipe(ItemStack product, Object... params) {
      GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
   }

   public void newShapelessRecipe(Block product, int qty, Object... params) {
      this.newShapelessRecipe(new ItemStack(product, qty), params);
   }

   public void newShapelessRecipe(Item product, int qty, Object... params) {
      this.newShapelessRecipe(new ItemStack(product, qty), params);
   }

   public void newShapelessRecipe(ItemStack product, Object... params) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
   }

   public void newSmeltingRecipe(Item product, int qty, Item input) {
      this.newSmeltingRecipe(product, qty, (Item)input, 0);
   }

   public void newSmeltingRecipe(Item product, int qty, Item input, int xp) {
      GameRegistry.addSmelting(input, new ItemStack(product, qty), (float)xp);
   }

   public void newSmeltingRecipe(Item product, int qty, Block input) {
      this.newSmeltingRecipe(product, qty, (Block)input, 0);
   }

   public void newSmeltingRecipe(Item product, int qty, Block input, int xp) {
      GameRegistry.addSmelting(input, new ItemStack(product, qty), (float)xp);
   }

   public void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
      WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);

      for(int i = 0; i < category.length; ++i) {
         ChestGenHooks.addItem(category[i], item);
      }

   }

   public void addEntity(Class<? extends Entity> cls, String name, Enum id) {
      this.addEntity(cls, name, id.ordinal());
   }

   public void addEntity(Class<? extends Entity> cls, String name, int id) {
      this.addEntity(cls, name, id, 1, true);
   }

   public void addEntity(Class<? extends Entity> cls, String name, Enum id, int updateFrequency, boolean sendVelocityUpdates) {
      this.addEntity(cls, name, id.ordinal(), updateFrequency, sendVelocityUpdates);
   }

   public void addEntity(Class<? extends Entity> cls, String name, int id, int updateFrequency, boolean sendVelocityUpdates) {
      EntityRegistry.registerModEntity(cls, name, id, this, 256, updateFrequency, sendVelocityUpdates);
   }

   int addVillager(String name, ResourceLocation skin) {
      int id = this.config.getVillager(name);
      VSBinding b = new VSBinding();
      b.id = id;
      b.object = skin;
      this.registeredVillagers.add(b);
      return id;
   }

   void addTradeHandler(int villagerID, VillagerRegistry.IVillageTradeHandler handler) {
      VillagerRegistry.instance().registerVillageTradeHandler(villagerID, handler);
   }

   public void registerStructureComponent(Class cls, String name) {
      MapGenStructureIO.func_143031_a(cls, name);
   }

   public ResourceLocation resourceLocation(String path) {
      return path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(this.assetKey, path);
   }

   public String soundName(String name) {
      return this.assetKey + ":" + name;
   }

   public ResourceLocation textureLocation(String path) {
      return this.resourceLocation("textures/" + path);
   }

   public ResourceLocation modelLocation(String path) {
      return this.resourceLocation("models/" + path);
   }

   public BaseModClient.IModel getModel(String name) {
      ResourceLocation loc = this.modelLocation(name);
      BaseModClient.IModel model = (BaseModClient.IModel)this.modelCache.get(loc);
      if (model == null) {
         model = BaseModel.fromResource(loc);
         this.modelCache.put(loc, model);
      }

      return model;
   }

   public static void sendTileEntityUpdate(TileEntity te) {
      Packet packet = te.getDescriptionPacket();
      if (packet != null) {
         int x = te.xCoord >> 4;
         int z = te.zCoord >> 4;
         WorldServer world = (WorldServer)te.getWorldObj();
         ServerConfigurationManager cm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
         PlayerManager pm = world.getPlayerManager();

          for(Object value : cm.playerEntityList) {
             EntityPlayerMP player = (EntityPlayerMP)value;
            if (pm.isPlayerWatchingChunk(player, x, z)) {
               player.playerNetServerHandler.sendPacket(packet);
            }
         }
      }

   }

   protected void registerContainers() {
   }

   public int getGuiId(Object obj) {
      Integer id = (Integer)this.objectToGuiId.get(obj);
      return id != null ? id : -1;
   }

   public void addContainer(Enum id, Class<? extends Container> cls) {
      this.addContainer(id.ordinal(), cls);
   }

   public int addContainer(Class<? extends Container> cls) {
      return this.addContainer((Class)cls, (Class)null);
   }

   public int addContainer(Class<? extends Container> cls, Class<? extends TileEntity> teCls) {
      int id = this.nextGuiId++;
      this.addContainer(id, cls, teCls);
      return id;
   }

   public void addContainer(int id, Class<? extends Container> cls) {
      this.addContainer(id, cls, (Class)null);
   }

   public void addContainer(int id, Class<? extends Container> cls, Class<? extends TileEntity> teCls) {
      if (this.containerClasses.containsKey(id)) {
         throw new RuntimeException("Duplicate container registration with ID " + id);
      } else {
         this.containerClasses.put(id, cls);
         this.objectToGuiId.put(cls, id);
         if (teCls != null) {
            this.containerTEClasses.put(cls, teCls);
            this.objectToGuiId.put(teCls, id);
         }

      }
   }

   public void openGui(EntityPlayer player, Enum id, TileEntity te) {
      this.openGui(player, id, te, 0);
   }

   public void openGui(EntityPlayer player, Enum id, TileEntity te, int param) {
      this.openGui(player, id.ordinal(), te, param);
   }

   public void openGui(EntityPlayer player, TileEntity te) {
      this.openGui(player, -1, te, 0);
   }

   public void openGui(EntityPlayer player, int id, TileEntity te) {
      this.openGui(player, id, te, 0);
   }

   public void openGui(EntityPlayer player, TileEntity te, int param) {
      this.openGui(player, -1, te, param);
   }

   public void openGui(EntityPlayer player, int id, TileEntity te, int param) {
      if (id < 0) {
         id = this.getGuiId(te);
      }

      this.openGui(player, id, te.getWorldObj(), new BlockPos(te), param);
   }

   public void openGui(EntityPlayer player, Enum id, World world, BlockPos pos) {
      this.openGui(player, id, world, pos, 0);
   }

   public void openGui(EntityPlayer player, Enum id, World world, BlockPos pos, int param) {
      this.openGui(player, id.ordinal(), world, pos, param);
   }

   public void openGui(EntityPlayer player, int id, World world, BlockPos pos, int param) {
      this.openGui(player, id | param << 16, world, pos);
   }

   public void openGui(EntityPlayer player, int id, World world, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      player.openGui(this, id, world, x, y, z);
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      return this.getServerGuiElement(id, player, world, new BlockPos(x, y, z));
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, BlockPos pos) {
      int param = id >> 16;
      id &= 65535;
      Class cls = (Class)this.containerClasses.get(id);
      Object result;
      if (cls != null) {
         result = this.createGuiElement(cls, player, world, pos, param);
      } else {
         result = this.getGuiContainer(id, player, world, pos, param);
      }

      this.setModOf(result);
      return result;
   }

   Container getGuiContainer(int id, EntityPlayer player, World world, BlockPos pos, int param) {
      return null;
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   Object createGuiElement(Class cls, EntityPlayer player, World world, BlockPos pos, int param) {
      try {
         Method m = this.getMethod(cls, "create", EntityPlayer.class, World.class, BlockPos.class, Integer.TYPE);
         if (m != null) {
            return m.invoke((Object)null, player, world, pos, param);
         } else {
            m = this.getMethod(cls, "create", EntityPlayer.class, World.class, BlockPos.class);
            if (m != null) {
               return m.invoke((Object)null, player, world, pos);
            } else {
               Constructor c = this.getConstructor(cls, EntityPlayer.class, World.class, BlockPos.class, Integer.TYPE);
               if (c != null) {
                  return c.newInstance(player, world, pos, param);
               } else {
                  c = this.getConstructor(cls, EntityPlayer.class, World.class, BlockPos.class);
                  if (c != null) {
                     return c.newInstance(player, world, pos);
                  } else {
                     Class<? extends TileEntity> teCls = (Class)this.containerTEClasses.get(cls);
                     if (teCls != null) {
                        TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
                        if (te != null) {
                           c = this.getConstructor(cls, EntityPlayer.class, teCls, Integer.TYPE);
                           if (c != null) {
                              return c.newInstance(player, te, param);
                           }

                           c = this.getConstructor(cls, EntityPlayer.class, teCls);
                           if (c != null) {
                              return c.newInstance(player, te);
                           }
                        }
                     }

                     throw new RuntimeException(String.format("%s: No suitable gui element constructor found for %s\n", this.modID, cls));
                  }
               }
            }
         }
      } catch (Exception e) {
         reportExceptionCause(e);
         return null;
      }
   }

   Method getMethod(Class cls, String name, Class... argTypes) {
      try {
         return cls.getMethod(name, argTypes);
      } catch (NoSuchMethodException var5) {
         return null;
      }
   }

   Constructor getConstructor(Class cls, Class... argTypes) {
      try {
         return cls.getConstructor(argTypes);
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static void reportExceptionCause(Exception e) {
      Throwable cause = e.getCause();
      if (cause != null) {
         cause.printStackTrace();
      } else {
         e.printStackTrace();
      }

   }

   static class IDBinding<T> {
      public int id;
      public T object;
   }

   public static class ModelSpec {
      public String modelName;
      public String[] textureNames;
      public Vector3 origin;

      public ModelSpec(String model, String... textures) {
         this(model, Vector3.zero, textures);
      }

      public ModelSpec(String model, Vector3 origin, String... textures) {
         this.modelName = model;
         this.textureNames = textures;
         this.origin = origin;
      }
   }

   static class VSBinding extends IDBinding<ResourceLocation> {
   }

   public interface IBlock extends ITextureConsumer {
      void setRenderType(int var1);

      String getQualifiedRendererClassName();

      ModelSpec getModelSpec(IBlockState var1);

      int getNumSubtypes();

      Trans3 localToGlobalTransformation(IBlockAccess var1, BlockPos var2, IBlockState var3, Vector3 var4);

      Class getDefaultItemClass();
   }

   interface IItem extends ITextureConsumer {
      ModelSpec getModelSpec(ItemStack var1);

      int getNumSubtypes();
   }

   interface ISetMod {
      void setMod(BaseMod var1);
   }

   interface ITextureConsumer {
      String[] getTextureNames();
   }

   interface ITileEntity {
      void onAddedToWorld();
   }
}
