package gcewing.sg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import gcewing.sg.gui.DHDFuelContainer;
import gcewing.sg.oc.OCIntegration;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

@Mod(
   modid = "SGCraft",
   name = "SG Craft",
   version = "2.46.0",
   acceptableRemoteVersions = "[2.46,2.47)"
)
public class SGCraft extends BaseMod<SGCraftClient> {
   public static final Material machineMaterial;
   public static SGCraft mod;
   public static SGChannel channel;
   public static BaseTEChunkManager chunkManager;
   public static int DimID;
   public static SGBaseBlock0 sgBaseBlock0;
   public static SGBaseBlock1 sgBaseBlock1;
   public static SGRingBlock0 sgRingBlock0;
   public static SGRingBlock1 sgRingBlock1;
   public static SGBaseBlock2 sgBaseBlock2;
   public static SGBaseBlock3 sgBaseBlock3;
   public static SGBaseBlock4 sgBaseBlock4;
   public static SGRingBlock2 sgRingBlock2;
   public static SGRingBlock3 sgRingBlock3;
   public static SGRingBlock4 sgRingBlock4;
   public static SGBaseBlock0Big sgBaseBlock0Big;
   public static SGBaseBlock1Big sgBaseBlock1Big;
   public static SGBaseBlock2Big sgBaseBlock2Big;
   public static SGBaseBlock3Big sgBaseBlock3Big;
   public static SGBaseBlock4Big sgBaseBlock4Big;
   public static DHDBlock0 sgControllerBlock0;
   public static DHDBlock1 sgControllerBlock1;
   public static DHDBlock2 sgControllerBlock2;
   public static DHDBlock3 sgControllerBlock3;
   public static DHDBlock4 sgControllerBlock4;
   public static RingBase ringBaseBlock;
   public static RingBaseUp ringBaseBlockUp;
   public static RingBaseFlat ringBaseBlockFlat;
   public static RingControl ringControllerBlock;
   public static Block naquadahBlock;
   public static Block naquadahOre;
   public static Item naquadah;
   public static Item naquadahIngot;
   public static Item sgCoreCrystal;
   public static Item sgControllerCrystal;
   public static Item sgChevronUpgrade;
   public static Item sgIrisUpgrade;
   public static Item sgShieldUpgrade;
   public static Item sgIrisBlade;
   public static Item remoteDialer;
   public static Block ic2PowerUnit;
   public static Item ic2Capacitor;
   public static Block rfPowerUnit;
   public static boolean addOresToExistingWorlds;
   public static NaquadahOreWorldGen naquadahOreGenerator;
   public static int tokraVillagerID;
   public static BaseSubsystem ic2Integration;
   public static BaseSubsystem rfIntegration;
   public static BaseSubsystem txIntegration;
   public static BaseSubsystem ccIntegration;
   public static OCIntegration ocIntegration;

   public SGCraft() {
      mod = this;
      this.blockDomain = this.itemDomain = "gcewing_sg";
      this.creativeTab = new CreativeTabs("gcewing_sg:sgcraft") {
         public Item getTabIconItem() {
            return Item.getItemFromBlock(SGCraft.sgBaseBlock0);
         }
      };
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      FMLCommonHandler.instance().bus().register(this);
      ic2Integration = this.integrateWithMod("IC2", "gcewing.sg.ic2.IC2Integration");
      rfIntegration = this.integrateWithMod("CoFHCore", "gcewing.sg.rf.RFIntegration");
      txIntegration = this.integrateWithMod("ThermalExpansion", "gcewing.sg.TXIntegration");
      ccIntegration = this.integrateWithMod("ComputerCraft", "gcewing.sg.cc.CCIntegration");
      ocIntegration = (OCIntegration)this.integrateWithMod("OpenComputers", "gcewing.sg.oc.OCIntegration");
      super.preInit(e);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
      super.init(e);
      this.configure();
      channel = new SGChannel("SGCraft");
      chunkManager = new BaseTEChunkManager(this);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      super.postInit(e);
   }

   protected SGCraftClient initClient() {
      return new SGCraftClient(this);
   }

   void configure() {
      DHDTE.configure(this.config);
      NaquadahOreWorldGen.configure(this.config);
      SGBaseBlock0.configure(this.config);
      SGBaseBlock1.configure(this.config);
      SGBaseTE.configure(this.config);
      SGChannel.remoteRange = Math.max(1, this.config.getInteger("remoteDialer", "range", 20));
      SGWormholeTravel.enabled = this.config.getBoolean("stargate", "wormholeVisual", true);
      FeatureGeneration.configure(this.config);
      addOresToExistingWorlds = this.config.getBoolean("options", "addOresToExistingWorlds", false);
   }

   protected void registerOther() {
      MinecraftForge.TERRAIN_GEN_BUS.register(this);
   }

   protected void registerBlocks() {
      sgRingBlock0 = (SGRingBlock0)this.newBlock("stargateRing", SGRingBlock0.class, SGRingItem0.class);
      sgRingBlock1 = (SGRingBlock1)this.newBlock("stargateRing1", SGRingBlock1.class, SGRingItem0.class);
      sgBaseBlock0 = (SGBaseBlock0)this.newBlock("stargateBase", SGBaseBlock0.class);
      sgBaseBlock1 = (SGBaseBlock1)this.newBlock("stargateBase1", SGBaseBlock1.class);
      sgControllerBlock0 = (DHDBlock0)this.newBlock("stargateController", DHDBlock0.class);
      sgControllerBlock1 = (DHDBlock1)this.newBlock("stargateController1", DHDBlock1.class);
      sgRingBlock2 = (SGRingBlock2)this.newBlock("stargateRing2", SGRingBlock2.class, SGRingItem0.class);
      sgRingBlock3 = (SGRingBlock3)this.newBlock("stargateRing3", SGRingBlock3.class, SGRingItem0.class);
      sgRingBlock4 = (SGRingBlock4)this.newBlock("stargateRing4", SGRingBlock4.class, SGRingItem0.class);
      sgBaseBlock2 = (SGBaseBlock2)this.newBlock("stargateBase2", SGBaseBlock2.class);
      sgBaseBlock3 = (SGBaseBlock3)this.newBlock("stargateBase3", SGBaseBlock3.class);
      sgBaseBlock4 = (SGBaseBlock4)this.newBlock("stargateBase4", SGBaseBlock4.class);
      sgBaseBlock0Big = (SGBaseBlock0Big)this.newBlock("stargateBase0Big", SGBaseBlock0Big.class);
      sgBaseBlock1Big = (SGBaseBlock1Big)this.newBlock("stargateBase1Big", SGBaseBlock1Big.class);
      sgBaseBlock2Big = (SGBaseBlock2Big)this.newBlock("stargateBase2Big", SGBaseBlock2Big.class);
      sgBaseBlock3Big = (SGBaseBlock3Big)this.newBlock("stargateBase3Big", SGBaseBlock3Big.class);
      sgBaseBlock4Big = (SGBaseBlock4Big)this.newBlock("stargateBase4Big", SGBaseBlock4Big.class);
      sgControllerBlock2 = (DHDBlock2)this.newBlock("stargateController2", DHDBlock2.class);
      sgControllerBlock3 = (DHDBlock3)this.newBlock("stargateController3", DHDBlock3.class);
      sgControllerBlock4 = (DHDBlock4)this.newBlock("stargateController4", DHDBlock4.class);
      naquadahBlock = this.newBlock("naquadahBlock", NaquadahBlock.class);
      naquadahOre = this.newBlock("naquadahOre", NaquadahOreBlock.class);
      ringBaseBlock = (RingBase)this.newBlock("ringBase", RingBase.class);
      ringBaseBlockUp = (RingBaseUp)this.newBlock("ringBaseUp", RingBaseUp.class);
      ringBaseBlockFlat = (RingBaseFlat)this.newBlock("ringBaseFlat", RingBaseFlat.class);
      ringControllerBlock = (RingControl)this.newBlock("ringController", RingControl.class);
   }

   protected void registerItems() {
      naquadah = this.newItem("naquadah");
      naquadahIngot = this.newItem("naquadahIngot");
      sgCoreCrystal = this.newItem("sgCoreCrystal");
      sgControllerCrystal = this.newItem("sgControllerCrystal");
      sgChevronUpgrade = this.addItem(new SGChevronUpgradeItem(), "sgChevronUpgrade");
      sgIrisUpgrade = this.addItem(new SGIrisUpgradeItem(), "sgIrisUpgrade");
      sgShieldUpgrade = this.addItem(new SGIrisUpgradeItem(BarrierKind.SHIELD), "sgShieldUpgrade");
      sgIrisBlade = this.newItem("sgIrisBlade");
      remoteDialer = this.addItem(new SGRemoteDialerItem(), "sgRemoteDialer");
      if (isModLoaded("IC2") || isModLoaded("CoFHCore") && !isModLoaded("ThermalExpansion")) {
         ic2Capacitor = this.newItem("ic2Capacitor");
      }

   }

   public static boolean isValidStargateUpgrade(Item item) {
      return item == sgChevronUpgrade || item == sgIrisUpgrade || item == sgShieldUpgrade;
   }

   protected void registerOres() {
      this.addOre("oreNaquadah", naquadahOre);
      this.addOre("naquadah", naquadah);
      this.addOre("ingotNaquadahAlloy", naquadahIngot);
   }

   protected void registerRecipes() {
      ItemStack chiselledSandstone = new ItemStack(Blocks.sandstone, 1, 1);
      ItemStack smoothSandstone = new ItemStack(Blocks.sandstone, 1, 2);
      ItemStack sgChevronBlock = new ItemStack(sgRingBlock0, 1, 1);
      ItemStack blueDye = new ItemStack(Items.dye, 1, 4);
      ItemStack orangeDye = new ItemStack(Items.dye, 1, 14);
      if (this.config.getBoolean("options", "allowCraftingNaquadah", false)) {
         this.newShapelessRecipe(naquadah, 1, new Object[]{Items.coal, Items.slime_ball, Items.blaze_powder});
      }

      this.newRecipe(sgRingBlock0, 1, new Object[]{"CCC", "NNN", "SSS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone});
      this.newRecipe(sgRingBlock1, 1, new Object[]{"CCC", "NNN", "SSS", 'S', new ItemStack(Items.dye, 1, 14), 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone});
      this.newRecipe(sgChevronBlock, new Object[]{"CgC", "NpN", "SrS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'g', Items.glowstone_dust, 'r', Items.redstone, 'p', Items.ender_pearl});
      this.newRecipe(sgBaseBlock0, 1, new Object[]{"CrC", "NeN", "ScS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'r', Items.redstone, 'e', Items.ender_eye, 'c', sgCoreCrystal});
      this.newRecipe(sgBaseBlock1, 1, new Object[]{"CrC", "NeN", "ScS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'r', Items.diamond, 'e', Items.ender_eye, 'c', sgCoreCrystal});
      this.newRecipe(sgControllerBlock0, 1, new Object[]{"bbb", "OpO", "OcO", 'b', Blocks.stone_button, 'O', Blocks.obsidian, 'p', Items.ender_pearl, 'r', Items.redstone, 'c', sgControllerCrystal});
      this.newShapelessRecipe(naquadahIngot, 1, new Object[]{"naquadah", Items.iron_ingot});
      this.newRecipe(naquadahBlock, 1, new Object[]{"NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy"});
      this.newRecipe(sgChevronUpgrade, 1, new Object[]{"g g", "pNp", "r r", 'N', "ingotNaquadahAlloy", 'g', Items.glowstone_dust, 'r', Items.redstone, 'p', Items.ender_pearl});
      this.newRecipe(naquadahIngot, 9, new Object[]{"B", 'B', naquadahBlock});
      this.newRecipe(sgIrisBlade, 1, new Object[]{" ii", "ic ", "i  ", 'i', Items.iron_ingot, 'c', new ItemStack(Items.coal, 1, 1)});
      this.newRecipe(sgIrisUpgrade, 1, new Object[]{"bbb", "brb", "bbb", 'b', sgIrisBlade, 'r', Items.redstone});
      this.newRecipe(sgShieldUpgrade, 1, new Object[]{"gng", "nrn", "gng", 'g', Items.glowstone_dust, 'n', naquadahIngot, 'r', Items.redstone});
      if (this.config.getBoolean("options", "allowCraftingCrystals", false)) {
         this.newRecipe(sgCoreCrystal, 1, new Object[]{"bbr", "rdb", "brb", 'b', blueDye, 'r', Items.redstone, 'd', Items.diamond});
         this.newRecipe(sgControllerCrystal, 1, new Object[]{"roo", "odr", "oor", 'o', orangeDye, 'r', Items.redstone, 'd', Items.diamond});
      }

      if (!isModLoaded("ThermalExpansion") && isModLoaded("CoFHCore")) {
         if (!isModLoaded("IC2")) {
            this.registerFallbackCapacitorRecipe();
         }

         this.registerFallbackPowerBlockRecipe();
      }

   }

   protected void registerFallbackCapacitorRecipe() {
      this.newRecipe(ic2Capacitor, 1, new Object[]{"iii", "ppp", "iii", 'i', Items.iron_ingot, 'p', Items.paper});
   }

   protected void registerFallbackPowerBlockRecipe() {
      this.newRecipe(rfPowerUnit, 1, new Object[]{"cgc", "gIg", "crc", 'c', ic2Capacitor, 'g', Items.gold_ingot, 'I', Blocks.iron_block, 'r', Items.redstone});
   }

   protected void registerContainers() {
      this.addContainer(SGGui.SGBase, SGBaseContainer.class);
      this.addContainer(SGGui.DHDFuel, DHDFuelContainer.class);
      this.addContainer(SGGui.PowerUnit, PowerContainer.class);
   }

   protected void registerRandomItems() {
      String[] categories = new String[]{"mineshaftCorridor", "pyramidDesertyChest", "pyramidJungleChest", "strongholdLibrary", "villageBlacksmith"};
      this.addRandomChestItem(new ItemStack(sgCoreCrystal), 1, 1, 2, categories);
      this.addRandomChestItem(new ItemStack(sgControllerCrystal), 1, 1, 1, categories);
   }

   protected void registerWorldGenerators() {
      if (this.config.getBoolean("options", "enableNaquadahOre", true)) {
         naquadahOreGenerator = new NaquadahOreWorldGen();
         GameRegistry.registerWorldGenerator(naquadahOreGenerator, 0);
      }

      this.registerStructureComponent(FeatureUnderDesertPyramid.class, "SGCraft:FeatureUnderDesertPyramid");
   }

   protected void registerVillagers() {
      tokraVillagerID = this.addVillager("tokra", this.resourceLocation("textures/skins/tokra.png"));
      this.addTradeHandler(tokraVillagerID, new SGTradeHandler());
   }

   protected void registerEntities() {
      this.addEntity(IrisEntity.class, "Stargate Iris", SGEntity.Iris, 1000000, false);
   }

   @SubscribeEvent
   public void onChunkLoad(ChunkDataEvent.Load e) {
      Chunk chunk = e.getChunk();
      SGChunkData.onChunkLoad(e);
   }

   @SubscribeEvent
   public void onChunkSave(ChunkDataEvent.Save e) {
      Chunk chunk = e.getChunk();
      SGChunkData.onChunkSave(e);
   }

   @SubscribeEvent
   public void onInitMapGen(InitMapGenEvent e) {
      FeatureGeneration.onInitMapGen(e);
   }

   @Override
   Container getGuiContainer(int id, EntityPlayer player, World world, BlockPos pos, int param) {
      ItemStack held = player.getCurrentEquippedItem();
      return id == SGGui.SG_REMOTE_DIALER.ordinal() && held != null && held.getItem() == remoteDialer ? new SGRemoteDialerContainer(player) : null;
   }

   @SubscribeEvent
   public void onServerTick(TickEvent.ServerTickEvent e) {
      switch (e.phase) {
         case START:
            SGChannel.processRemoteActions();
            SGWormholeTravel.tick();
            for(BaseSubsystem om : this.subsystems) {
               if (om instanceof IIntegration) {
                  ((IIntegration)om).onServerTick();
               }
            }
         default:
      }
   }

   @SubscribeEvent
   public void onWormholeDamage(LivingHurtEvent event) {
      if (event.entityLiving instanceof net.minecraft.entity.player.EntityPlayer && SGWormholeTravel.protects((net.minecraft.entity.player.EntityPlayer)event.entityLiving)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
      SGWormholeTravel.stop(event.player);
   }

   @SubscribeEvent
   public void onChunkUnload(ChunkEvent.Unload e) {
      Chunk chunk = e.getChunk();
      if (!BaseUtils.getChunkWorld(chunk).isRemote) {
         for(Object obj : BaseUtils.getChunkTileEntityMap(chunk).values()) {
            if (obj instanceof SGBaseTE) {
               SGBaseTE te = (SGBaseTE)obj;
               System.out.println("chunk unload");
            }
         }
      }

   }

   static {
      machineMaterial = new Material(MapColor.ironColor);
      DimID = 69;
   }
}
