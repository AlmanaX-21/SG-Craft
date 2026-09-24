package gcewing.sg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.relauncher.Side;
import gcewing.sg.oc.OCWirelessEndpoint;
import io.netty.channel.ChannelFutureListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage;

public class SGBaseTE extends BaseTileInventory {
   private byte type = 0;
   public boolean qd = true;
   public boolean hardlyChanged = false;
   public static final String symbolChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   public static final int[] numRingSymbols = new int[]{39, 36, 39, 39, 39};
   public static final double[] ringSymbolAngle;
   public static final double irisZPosition = 0.1;
   public static final double irisThickness = 0.2;
   public static final DamageSource irisDamageSource;
   public static final float irisDamageAmount = 1000000.0F;
   public static final DamageSource portalDamageSource;
   public static final float portalDamageAmount = 1000000.0F;
   static final int diallingTime2 = 4;
   static final int interDiallingTime2 = 1;
   static final int[] diallingTime;
   static final int interDiallingTime = 10;
   static final String diallingSound = "sgcraft:sg_dial7";
   static final String diallingSound1 = "sgcraft:sg_dial71";
   static final String diallingSound2 = "sgcraft:sg_dial72";
   static final String diallingSound3 = "sgcraft:sg_dial73";
   static final String diallingSound4 = "sgcraft:sg_dial74";
   static final String diallingSoundQD = "sgcraft:sg_dial7QD";
   static final String diallingSoundQD1 = "sgcraft:sg_dial7QD1";
   static final String diallingSoundQD2 = "sgcraft:sg_dial7QD2";
   static final String diallingSoundQD3 = "sgcraft:sg_dial7QD3";
   static final String diallingSoundQD4 = "sgcraft:sg_dial7QD4";
   static final int transientDuration = 20;
   static final int disconnectTime = 30;
   static final double openingTransientIntensity = 1.3;
   static final double openingTransientRandomness = (double)0.25F;
   static final double closingTransientRandomness = (double)0.25F;
   static final double transientDamageRate = (double)50.0F;
   static final int[] maxIrisPhase;
   static final int firstCamouflageSlot = 0;
   static final int numCamouflageSlots = 24;
   static final int numInventorySlots = 24;
   static float defaultChevronAngle;
   static float[][] chevronAngles;
   static final int[][] chevronSequences = new int[][]{{6, 0, 1, 2, 7, 8, 3, 4, 5}, {7, 0, 1, 2, 6, 8, 3, 4, 5}, {8, 0, 1, 2, 6, 7, 3, 4, 5}};
   static double maxEnergyBuffer;
   static double energyPerFuelItem;
   static double distanceFactorMultiplier;
   static double interDimensionMultiplier;
   static int gateOpeningsPerFuelItem;
   static int minutesOpenPerFuelItem;
   static int secondsToStayOpen;
   static boolean fuelFree;
   static boolean requireChevronUpgrade = true;
   static boolean oneWayTravel;
   static int crossTypeFailurePercent;
   static boolean closeFromEitherEnd;
   static int chunkLoadingRange;
   static boolean preserveInventory;
   static float soundVolume;
   static boolean variableChevronPositions;
   public static double energyToOpen;
   static double energyUsePerTick;
   static int ticksToStayOpen;
   public static boolean transparency;
   static Random random;
   static DamageSource transientDamage;
   public boolean isMerged;
   public SGState state;
   public double ringAngle;
   public double lastRingAngle;
   public double targetRingAngle;
   public int numEngagedChevrons;
   public int diallingChevronCount;
   public String dialledAddress;
   public boolean isLinkedToController;
   public BlockPos linkedPos;
   public boolean hasChevronUpgrade;
   public boolean hasIrisUpgrade;
   public BarrierKind barrierKind = BarrierKind.NONE;
   public IrisState irisState;
   public int irisPhase;
   public int lastIrisPhase;
   public OCWirelessEndpoint ocWirelessEndpoint;
   public int connectedTime;
   SGLocation connectedLocation;
   boolean isInitiator;
   int timeout;
   double energyInBuffer;
   double distanceFactor;
   boolean redstoneInput;
   boolean loaded;
   public String homeAddress;
   String gateAddress;
   public String addressError;
   IInventory inventory;
   protected float[][][] ehGrid;
   protected int ehTime;
   public float chevronMovingStartTime;
   public int lastNumChevrons;
   public int interDialingDelay;
   public float irisHitTime;
   public float irisHitEntitySize;
   public float irisHitOffsetX;
   public int interDialingCounter;
   public float[] chevronEngagingTimes;
   public boolean[] chevronEngaged;
   public boolean newChevronEngaged;
   public boolean angleDirection;
   public int ehResolution;
   public float transientStartTime;
   List<TrackedEntity> trackedEntities;

   public SGBaseTE() {
      this.state = SGState.Idle;
      this.dialledAddress = "";
      this.linkedPos = new BlockPos(0, 0, 0);
      this.irisState = IrisState.Open;
      this.irisPhase = maxIrisPhase[0];
      this.lastIrisPhase = maxIrisPhase[0];
      this.connectedTime = 0;
      this.inventory = new InventoryBasic("Stargate", false, 24);
      this.ehTime = -1;
      this.chevronMovingStartTime = -1.0F;
      this.lastNumChevrons = -1;
      this.interDialingDelay = 25;
      this.irisHitTime = -2.0F;
      this.irisHitEntitySize = 0.0F;
      this.irisHitOffsetX = 0.0F;
      this.interDialingCounter = 0;
      this.chevronEngagingTimes = new float[10];
      this.chevronEngaged = new boolean[10];
      this.newChevronEngaged = false;
      this.angleDirection = false;
      this.ehResolution = 10;
      this.transientStartTime = -1.0F;
      this.trackedEntities = new ArrayList();
   }

   public static void configure(BaseConfiguration cfg) {
      energyPerFuelItem = cfg.getDouble("stargate", "energyPerFuelItem", energyPerFuelItem);
      gateOpeningsPerFuelItem = cfg.getInteger("stargate", "gateOpeningsPerFuelItem", gateOpeningsPerFuelItem);
      minutesOpenPerFuelItem = cfg.getInteger("stargate", "minutesOpenPerFuelItem", minutesOpenPerFuelItem);
      secondsToStayOpen = cfg.getInteger("stargate", "secondsToStayOpen", secondsToStayOpen);
      fuelFree = cfg.getBoolean("stargate", "fuelFree", fuelFree);
      requireChevronUpgrade = cfg.getBoolean("stargate", "requireChevronUpgrade", requireChevronUpgrade);
      oneWayTravel = cfg.getBoolean("stargate", "oneWayTravel", oneWayTravel);
      crossTypeFailurePercent = cfg.getInteger("stargate", "crossTypeFailurePercent", crossTypeFailurePercent);
      closeFromEitherEnd = cfg.getBoolean("stargate", "closeFromEitherEnd", closeFromEitherEnd);
      maxEnergyBuffer = cfg.getDouble("stargate", "maxEnergyBuffer", maxEnergyBuffer);
      energyToOpen = energyPerFuelItem / (double)gateOpeningsPerFuelItem;
      energyUsePerTick = energyPerFuelItem / (double)(minutesOpenPerFuelItem * 60 * 20);
      distanceFactorMultiplier = cfg.getDouble("stargate", "distanceFactorMultiplier", distanceFactorMultiplier);
      interDimensionMultiplier = cfg.getDouble("stargate", "interDimensionMultiplier", interDimensionMultiplier);
      ticksToStayOpen = 20 * secondsToStayOpen;
      chunkLoadingRange = cfg.getInteger("options", "chunkLoadingRange", chunkLoadingRange);
      transparency = cfg.getBoolean("stargate", "transparency", transparency);
      preserveInventory = cfg.getBoolean("iris", "preserveInventory", preserveInventory);
      soundVolume = (float)cfg.getDouble("stargate", "soundVolume", (double)soundVolume);
      variableChevronPositions = cfg.getBoolean("stargate", "variableChevronPositions", variableChevronPositions);
   }

   ItemStack getCamouflageStack(BlockPos cpos) {
      Trans3 t = this.localToGlobalTransformation();
      Vector3 p = t.ip(Vector3.blockCenter(cpos));
      if (p.y <= (double)6.0F) {
         int i = 3 + p.roundX();
         if (i >= 0 && i < 7) {
            if (p.y != (double)0.0F && p.y != (double)6.0F) {
               if (i == 6) {
                  i = 1;
               }

               i = (int)((double)(i + 7) + (double)2.0F * (p.y - (double)1.0F));
               return this.getStackInSlot(i);
            }

            i = (int)((double)i + (double)2.75F * p.y);
            return this.getStackInSlot(i);
         }
      }

      return null;
   }

   public boolean shouldRenderInPass(int pass) {
      return pass == 0;
   }

   public static SGBaseTE get(IBlockAccess world, BlockPos pos) {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
      if (te instanceof SGBaseTE) {
         return (SGBaseTE)te;
      } else {
         return te instanceof SGRingTE ? ((SGRingTE)te).getBaseTE() : null;
      }
   }

   public String toString() {
      return String.format("SGBaseTE(pos=%s,dim=%s)", this.getPos(), BaseUtils.getWorldDimensionId(this.worldObj));
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.localToGlobalTransformation().box(new Vector3(-3, 0, -1), new Vector3(3, 7, 1));
   }

   public boolean isHorizontal() {
      return this.gateOrientation() >= 4;
   }

   public int gateOrientation() {
      return BaseBlockUtils.getWorldBlockState(this.worldObj, this.getPos()).getValue(BaseOrientation.OrientStargateByState.ORIENTATION);
   }

   public double getMaxRenderDistanceSquared() {
      return (double)32768.0F;
   }

   public void onAddedToWorld() {
      this.updateChunkLoadingStatus();
   }

   public void setType(byte t) {
      this.type = t;
      if (this.irisPhase > 0) {
         this.irisPhase = maxIrisPhase[this.type];
         this.lastIrisPhase = maxIrisPhase[this.type];
      }

   }

   public byte getType() {
      return this.type;
   }

   void updateChunkLoadingStatus() {
      if (this.state != SGState.Idle) {
         int n = chunkLoadingRange;
         if (n >= 0) {
            SGCraft.chunkManager.setForcedChunkRange(this, -n, -n, n, n);
         }
      } else {
         SGCraft.chunkManager.clearForcedChunkRange(this);
      }

   }

   public static SGBaseTE at(IBlockAccess world, BlockPos pos) {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
      return te instanceof SGBaseTE ? (SGBaseTE)te : null;
   }

   public static SGBaseTE at(SGLocation loc) {
      if (loc != null) {
         World world = SGAddressing.getWorld(loc.dimension);
         if (world != null) {
            return at(world, (BlockPos)loc.pos);
         }
      }

      return null;
   }

   public static SGBaseTE at(IBlockAccess world, NBTTagCompound nbt) {
      BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
      return at(world, pos);
   }

   void setMerged(boolean state) {
      if (this.isMerged != state) {
         this.isMerged = state;
         this.markBlockChanged();
         this.updateIrisEntity();
      }

   }

   String tryToGetHomeAddress() {
      try {
         return this.getHomeAddress();
      } catch (SGAddressing.AddressingError var2) {
         return null;
      }
   }

   public int dimension() {
      return this.worldObj != null ? BaseUtils.getWorldDimensionId(this.worldObj) : -999;
   }

   public void readContentsFromNBT(NBTTagCompound nbt) {
      super.readContentsFromNBT(nbt);
      this.isMerged = nbt.getBoolean("isMerged");
      if (nbt.hasKey("qd")) {
         this.qd = nbt.getBoolean("qd");
      }

      if (nbt.hasKey("hardlyChanged")) {
         this.hardlyChanged = nbt.getBoolean("hardlyChanged");
      }

      this.state = SGState.valueOf(nbt.getInteger("state"));
      this.targetRingAngle = nbt.getDouble("targetRingAngle");
      this.type = nbt.getByte("typ");
      this.numEngagedChevrons = nbt.getInteger("numEngagedChevrons");
      this.lastNumChevrons = nbt.getInteger("lastNumChevrons");
      this.newChevronEngaged = nbt.getBoolean("newChevronEngaged");
      this.dialledAddress = nbt.getString("dialledAddress");
      this.diallingChevronCount = nbt.hasKey("diallingChevronCount") ? nbt.getInteger("diallingChevronCount") : this.dialledAddress.length();
      this.isLinkedToController = nbt.getBoolean("isLinkedToController");
      int x = nbt.getInteger("linkedX");
      int y = nbt.getInteger("linkedY");
      int z = nbt.getInteger("linkedZ");
      this.linkedPos = new BlockPos(x, y, z);
      this.hasChevronUpgrade = nbt.getBoolean("hasChevronUpgrade");
      if (nbt.hasKey("connectedLocation")) {
         this.connectedLocation = new SGLocation(nbt.getCompoundTag("connectedLocation"));
      }

      this.isInitiator = nbt.getBoolean("isInitiator");
      this.timeout = nbt.getInteger("timeout");
      if (nbt.hasKey("energyInBuffer")) {
         this.energyInBuffer = nbt.getDouble("energyInBuffer");
      } else {
         this.energyInBuffer = (double)nbt.getInteger("fuelBuffer");
      }

      this.distanceFactor = nbt.getDouble("distanceFactor");
      this.hasIrisUpgrade = nbt.getBoolean("hasIrisUpgrade");
      this.barrierKind = nbt.hasKey("barrierKind") ? BarrierKind.valueOf(nbt.getString("barrierKind")) : this.hasIrisUpgrade ? this.type == 0 ? BarrierKind.IRIS : BarrierKind.SHIELD : BarrierKind.NONE;
      this.hasIrisUpgrade = this.barrierKind != BarrierKind.NONE;
      this.irisState = IrisState.valueOf(nbt.getInteger("irisState"));
      this.irisPhase = nbt.getInteger("irisPhase");
      this.redstoneInput = nbt.getBoolean("redstoneInput");
      this.homeAddress = this.getStringOrNull(nbt, "address");
      this.gateAddress = this.getStringOrNull(nbt, "gateAddressV2");
      this.addressError = nbt.getString("addressError");
      this.irisHitTime = nbt.getFloat("irisHitTime");
      this.irisHitEntitySize = nbt.getFloat("irisHitEntitySize");
      this.irisHitOffsetX = nbt.getFloat("irisHitOffsetX");
   }

   protected String getStringOrNull(NBTTagCompound nbt, String name) {
      return nbt.hasKey(name) ? nbt.getString(name) : null;
   }

   public void writeContentsToNBT(NBTTagCompound nbt) {
      super.writeContentsToNBT(nbt);
      nbt.setBoolean("isMerged", this.isMerged);
      nbt.setInteger("state", this.state.ordinal());
      nbt.setDouble("targetRingAngle", this.targetRingAngle);
      nbt.setByte("typ", this.type);
      nbt.setInteger("numEngagedChevrons", this.numEngagedChevrons);
      nbt.setInteger("diallingChevronCount", this.diallingChevronCount);
      nbt.setInteger("lastNumChevrons", this.lastNumChevrons);
      nbt.setBoolean("newChevronEngaged", this.newChevronEngaged);
      this.newChevronEngaged = false;
      nbt.setString("dialledAddress", this.dialledAddress);
      nbt.setBoolean("qd", this.qd);
      nbt.setBoolean("hardlyChanged", this.hardlyChanged);
      nbt.setBoolean("isLinkedToController", this.isLinkedToController);
      nbt.setInteger("linkedX", this.linkedPos.getX());
      nbt.setInteger("linkedY", this.linkedPos.getY());
      nbt.setInteger("linkedZ", this.linkedPos.getZ());
      nbt.setBoolean("hasChevronUpgrade", this.hasChevronUpgrade);
      if (this.connectedLocation != null) {
         nbt.setTag("connectedLocation", this.connectedLocation.toNBT());
      }

      nbt.setBoolean("isInitiator", this.isInitiator);
      nbt.setInteger("timeout", this.timeout);
      nbt.setDouble("energyInBuffer", this.energyInBuffer);
      nbt.setDouble("distanceFactor", this.distanceFactor);
      nbt.setBoolean("hasIrisUpgrade", this.hasIrisUpgrade);
      nbt.setString("barrierKind", this.barrierKind.name());
      nbt.setInteger("irisState", this.irisState.ordinal());
      nbt.setInteger("irisPhase", this.irisPhase);
      nbt.setBoolean("redstoneInput", this.redstoneInput);
      if (this.homeAddress != null) {
         nbt.setString("address", this.homeAddress);
      }
      if (this.gateAddress != null) {
         nbt.setString("gateAddressV2", this.gateAddress);
      }

      if (this.addressError != null) {
         nbt.setString("addressError", this.addressError);
      }

      nbt.setFloat("irisHitTime", this.irisHitTime);
      this.irisHitTime = -2.0F;
      nbt.setFloat("irisHitEntitySize", this.irisHitEntitySize);
      nbt.setFloat("irisHitOffsetX", this.irisHitOffsetX);
   }

   public boolean isActive() {
      return this.state != SGState.Idle && this.state != SGState.Disconnecting;
   }

   static boolean isValidSymbolChar(String c) {
      return SGAddressing.isValidSymbolChar(c);
   }

   public static char symbolToChar(int i) {
      return SGAddressing.symbolToChar(i);
   }

   static int charToSymbol(char c) {
      return SGAddressing.charToSymbol(c);
   }

   static int charToSymbol(String c) {
      return SGAddressing.charToSymbol(c);
   }

   public boolean applyChevronUpgrade(ItemStack stack, EntityPlayer player) {
      if (!this.worldObj.isRemote && !this.hasChevronUpgrade && stack.stackSize > 0) {
         this.hasChevronUpgrade = true;
         --stack.stackSize;
         this.markChanged();
      }

      return true;
   }

   public boolean applyIrisUpgrade(ItemStack stack, EntityPlayer player) {
      return this.applyBarrierUpgrade(stack, player, BarrierKind.IRIS);
   }

   public boolean applyBarrierUpgrade(ItemStack stack, EntityPlayer player, BarrierKind kind) {
      if (!this.worldObj.isRemote && this.barrierKind != kind && stack.stackSize > 0) {
         Item oldItem = this.barrierKind == BarrierKind.IRIS ? SGCraft.sgIrisUpgrade : this.barrierKind == BarrierKind.SHIELD ? SGCraft.sgShieldUpgrade : null;
         --stack.stackSize;
         this.barrierKind = kind;
         this.hasIrisUpgrade = true;
         this.markChanged();
         this.updateIrisEntity();
         if (oldItem != null) {
            ItemStack returned = new ItemStack(oldItem);
            if (!player.inventory.addItemStackToInventory(returned)) {
               player.dropPlayerItemWithRandomChoice(returned, false);
            }
         }
      }
      return true;
   }

   public int getNumChevrons() {
      return !requireChevronUpgrade || this.hasChevronUpgrade ? 9 : 7;
   }

   // Top chevron locks last
   public static int[] chevronSequence(int count) {
      return chevronSequences[count <= 7 ? 0 : count == 8 ? 1 : 2];
   }

   static int requiredChevrons(int sourceType, int destinationType) {
      if (sourceType == destinationType) {
         return 7;
      }
      return sourceType >= 3 || destinationType >= 3 ? 9 : 8;
   }

   boolean canDialWithChevrons(int count) {
      return count <= 7 || !requireChevronUpgrade || this.hasChevronUpgrade;
   }

   public boolean chevronIsEngaged(int i) {
      return i < this.numEngagedChevrons;
   }

   public float angleBetweenChevrons() {
      if (variableChevronPositions) {
         int c9 = this.getNumChevrons() > 7 ? 1 : 0;
         int bc = this.baseCornerCamouflage();
         return chevronAngles[c9][bc];
      } else {
         return defaultChevronAngle;
      }
   }

   Item getItemInSlot(int slot) {
      ItemStack stack = this.getStackInSlot(slot);
      return stack != null ? stack.getItem() : null;
   }

   public String getHomeAddress() throws SGAddressing.AddressingError {
      if (this.worldObj.isRemote) {
         return this.homeAddress;
      }
      try {
         return SGGateRegistry.addressFor(this);
      } catch (IllegalStateException e) {
         throw new SGAddressing.AddressingError(e.getMessage());
      }
   }

   public SGBaseBlock0 getBlock() {
      return (SGBaseBlock0)this.getBlockType();
   }

   public double interpolatedRingAngle(double t) {
      return this.type == 1 ? this.lastRingAngle + (this.ringAngle - this.lastRingAngle) * t : Utils.interpolateAngle(this.lastRingAngle, this.ringAngle, t);
   }

   public boolean canUpdate() {
      return true;
   }

   public void updateEntity() {
      this.tick();
   }

   public void tick() {
      if (this.state.equals(SGState.Connected)) {
         if (this.connectedTime % 100 == 0) {
            this.playSoundEffect("sgcraft:sg_wormhole", 3.0F, 1.0F);
         }

         ++this.connectedTime;
      }

      if (this.worldObj.isRemote) {
         this.clientUpdate();
         if (!this.state.equals(SGState.InterDialling) && this.state.equals(SGState.Dialling)) {
         }
      } else {
         this.serverUpdate();
         this.checkForEntitiesInPortal(this.type);
      }

      this.irisUpdate();
   }

   public void invalidate() {
      super.invalidate();
      if (!this.worldObj.isRemote && this.ocWirelessEndpoint != null) {
         this.ocWirelessEndpoint.remove();
      }

   }

   String side() {
      return this.worldObj.isRemote ? "Client" : "Server";
   }

   void enterState(SGState newState, int newTimeout) {
      SGState oldState = this.state;
      this.state = newState;
      this.timeout = newTimeout;
      this.markChanged();
      if (oldState == SGState.Idle != (newState == SGState.Idle)) {
         this.updateChunkLoadingStatus();
         BaseBlockUtils.notifyWorldNeighborsOfStateChange(this.worldObj, this.getPos(), this.getBlockType());
      }

      String oldDesc = sgStateDescription(oldState);
      String newDesc = sgStateDescription(newState);
      if (!oldDesc.equals(newDesc)) {
         this.postEvent("sgStargateStateChange", newDesc, oldDesc);
      }

   }

   public boolean isConnected() {
      return this.state == SGState.Transient || this.state == SGState.Connected || this.state == SGState.Disconnecting;
   }

   DHDTE getLinkedControllerTE() {
      if (this.isLinkedToController) {
         TileEntity cte = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.linkedPos);
         if (cte instanceof DHDTE) {
            return (DHDTE)cte;
         }
      }

      return null;
   }

   void checkForLink(byte id) {
      int rangeXY = this.isHorizontal() ? DHDTE.linkRangeZ : Math.max(DHDTE.linkRangeX, DHDTE.linkRangeY);
      int rangeZ = DHDTE.linkRangeZ;

      for(int i = -rangeXY; i <= rangeXY; ++i) {
         for(int j = -rangeZ; j <= rangeZ; ++j) {
            for(int k = -rangeXY; k <= rangeXY; ++k) {
               TileEntity te = BaseBlockUtils.getWorldTileEntity(this.worldObj, this.getPos().add(i, j, k));
               if (te instanceof DHDTE) {
                  ((DHDTE)te).checkForLink(id);
               }
            }
         }
      }

   }

   public void unlinkFromController() {
      if (this.isLinkedToController) {
         DHDTE cte = this.getLinkedControllerTE();
         if (cte != null) {
            cte.clearLinkToStargate();
         }

         this.clearLinkToController();
      }

   }

   public void clearLinkToController() {
      this.isLinkedToController = false;
      this.markDirty();
   }

   public void clearLinkToController2() {
      this.isLinkedToController = false;
      this.markDirty();
   }

   public void clearLinkToController3() {
      this.isLinkedToController = false;
      this.markDirty();
   }

   public void clearLinkToController4() {
      this.isLinkedToController = false;
      this.markDirty();
   }

   public void connectOrDisconnect(String address, EntityPlayer player) {
      if (address.length() > 0) {
         this.connect(address, player);
      } else {
         this.attemptToDisconnect(player);
      }

   }

   public String attemptToDisconnect(EntityPlayer player) {
      boolean canDisconnect = this.disconnectionAllowed();
      SGBaseTE dte = this.getConnectedStargateTE();
      boolean validConnection = dte != null && !dte.isInvalid() && dte.getConnectedStargateTE() == this;
      if (!canDisconnect && validConnection) {
         return this.operationFailure(player, "Connection initiated from other end");
      } else {
         if (this.state != SGState.Disconnecting) {
            this.disconnect();
         }

         return null;
      }
   }

   public boolean disconnectionAllowed() {
      return this.isInitiator || closeFromEitherEnd;
   }

   String connect(String address, EntityPlayer player) {
      if (this.state != SGState.Idle) {
         return this.diallingFailure(player, "Stargate is busy");
      } else {
         String homeAddress = this.findHomeAddress();
         if (homeAddress.equals("")) {
            return this.diallingFailure(player, "Coordinates of dialling stargate are out of range");
         } else {
            SGBaseTE dte;
            try {
               dte = SGAddressing.findAddressedStargate(address, this.worldObj);
            } catch (SGAddressing.AddressingError e) {
               return this.diallingFailure(player, e.getMessage());
            }

            if (dte != null && dte.isMerged) {
               int chevrons = requiredChevrons(this.type, dte.type);
               if (!this.canDialWithChevrons(chevrons)) {
                  return this.diallingFailure(player, "Not enough chevrons to dial " + address);
               } else if (dte != this) {
                  if (!dte.canDialWithChevrons(chevrons)) {
                     return this.diallingFailure(player, "Destination stargate has insufficient chevrons");
                  } else if (dte.state != SGState.Idle) {
                     return this.diallingFailure(player, "Stargate at address " + address + " is busy");
                  } else {
                     this.distanceFactor = distanceFactorForCoordDifference(this, dte);
                     if (!this.energyIsAvailable(energyToOpen * this.distanceFactor)) {
                        return this.diallingFailure(player, "Stargate has insufficient energy");
                     } else {
                        if (dte.qd != this.qd) {
                           dte.hardlyChanged = true;
                           dte.qd = this.qd;
                        } else {
                           dte.hardlyChanged = false;
                        }

                        this.startDiallingStargate(address, dte, true, chevrons);
                        dte.startDiallingStargate(homeAddress, this, false, chevrons);
                        return null;
                     }
                  }
               } else {
                  return this.diallingFailure(player, "Stargate cannot connect to itself");
               }
            } else {
               return this.diallingFailure(player, "No stargate at address " + address);
            }
         }
      }
   }

   public static double distanceFactorForCoordDifference(TileEntity te1, TileEntity te2) {
      BlockPos pos1 = BaseBlockUtils.getTileEntityPos(te1);
      BlockPos pos2 = BaseBlockUtils.getTileEntityPos(te2);
      double dx = (double)(pos1.getX() - pos2.getX());
      double dz = (double)(pos1.getZ() - pos2.getZ());
      double d = Math.sqrt(dx * dx + dz * dz);
      double ld = Math.log(0.05 * d + (double)1.0F);
      double lm = Math.log((double)223948.0F);
      double lr = ld / lm;
      double f = (double)1.0F + (double)14.0F * distanceFactorMultiplier * lr * lr;
      if (BaseBlockUtils.getTileEntityWorld(te1) != BaseBlockUtils.getTileEntityWorld(te2)) {
         f *= interDimensionMultiplier;
      }

      return f;
   }

   public void playSGSoundEffect(String name, float volume, float pitch) {
      this.playSoundEffect(name, volume * soundVolume, pitch);
   }

   String diallingFailure(EntityPlayer player, String mess) {
      if (player != null && this.state == SGState.Idle) {
         if (this.type == 0) {
            this.playSGSoundEffect("sgcraft:sg_abort", 1.0F, 1.0F);
         } else {
            this.playSGSoundEffect("sgcraft:sg_abort" + this.type, 1.0F, 1.0F);
         }
      }

      return this.operationFailure(player, mess);
   }

   String operationFailure(EntityPlayer player, String mess) {
      if (player != null) {
         sendChatMessage(player, mess);
      }

      return mess;
   }

   static void sendChatMessage(EntityPlayer player, String mess) {
      player.addChatMessage(new ChatComponentText(mess));
   }

   String findHomeAddress() {
      try {
         return this.getHomeAddress();
      } catch (SGAddressing.AddressingError var3) {
         return "";
      }
   }

   public void disconnect() {
      SGBaseTE dte = at(this.connectedLocation);
      if (dte != null) {
         dte.clearConnection();
         if (dte.hardlyChanged && dte.qd) {
            dte.qd = false;
         } else if (dte.hardlyChanged && !dte.qd) {
            dte.qd = true;
         }

         if (this.hardlyChanged && this.qd) {
            this.qd = false;
         } else if (this.hardlyChanged && !this.qd) {
            this.qd = true;
         }
      }

      this.clearConnection();
   }

   public void clearConnection() {
      if (this.state != SGState.Idle || this.connectedLocation != null) {
          this.dialledAddress = "";
          this.diallingChevronCount = 0;
         this.connectedLocation = null;
         this.isInitiator = false;
         this.numEngagedChevrons = 0;
         this.lastNumChevrons = -1;
         this.chevronMovingStartTime = 0.0F;
         this.chevronEngagingTimes = new float[10];
         this.connectedTime = 0;
         this.markChanged();
         if (this.state == SGState.Connected) {
            this.enterState(SGState.Disconnecting, 30);
            if (this.type == 0) {
               this.playSGSoundEffect("sgcraft:sg_close", 1.0F, 1.0F);
            } else {
               this.playSGSoundEffect("sgcraft:sg_close" + this.type, 1.0F, 1.0F);
            }
         } else {
            if (this.state != SGState.Idle && this.state != SGState.Disconnecting) {
               if (this.type == 0) {
                  this.playSGSoundEffect("sgcraft:sg_abort", 1.0F, 1.0F);
               } else {
                  this.playSGSoundEffect("sgcraft:sg_abort" + this.type, 1.0F, 1.0F);
               }
            }

            this.enterState(SGState.Idle, 0);
         }
      }

   }

   void startDiallingStargate(String address, SGBaseTE dte, boolean initiator, int chevrons) {
      this.dialledAddress = address;
      this.diallingChevronCount = chevrons;
      this.connectedLocation = new SGLocation(dte);
      this.isInitiator = initiator;
      this.interDialingCounter = this.interDialingDelay;
      this.markDirty();
      this.startDiallingNextSymbol();
      this.postEvent(initiator ? "sgDialOut" : "sgDialIn", address);
   }

   void serverUpdate() {
      if (!this.loaded) {
         this.loaded = true;

         try {
            this.homeAddress = this.getHomeAddress();
            this.addressError = "";
         } catch (SGAddressing.AddressingError e) {
            this.homeAddress = null;
            this.addressError = e.getMessage();
         }

         if (SGCraft.ocIntegration != null) {
            SGCraft.ocIntegration.onSGBaseTEAdded(this);
         }
      }

      if (this.isMerged) {
         this.tickEnergyUsage();
         if (this.timeout > 0) {
            if (this.state == SGState.Transient && !this.irisIsClosed()) {
               this.performTransientDamage();
            }

            --this.timeout;
         } else {
            switch (this.state) {
               case Idle:
                  if (this.undialledDigitsRemaining()) {
                     this.startDiallingNextSymbol();
                  }
                  break;
               case Dialling:
                  this.finishDiallingSymbol();
                  break;
               case InterDialling:
                  this.startDiallingNextSymbol();
                  break;
               case Transient:
                  this.enterState(SGState.Connected, this.isInitiator ? ticksToStayOpen : 0);
                  break;
               case Connected:
                  if (this.isInitiator && ticksToStayOpen > 0) {
                     this.disconnect();
                  }
                  break;
               case Disconnecting:
                  this.enterState(SGState.Idle, 0);
            }
         }
      }

   }

   void tickEnergyUsage() {
      if (this.state == SGState.Connected && this.isInitiator && !this.useEnergy(energyUsePerTick * this.distanceFactor)) {
         this.disconnect();
      }

   }

   double availableEnergy() {
      List<ISGEnergySource> sources = this.findEnergySources();
      return this.energyInBuffer + this.energyAvailableFrom(sources);
   }

   boolean energyIsAvailable(double amount) {
      if (fuelFree) {
         return true;
      }
      double energy = this.availableEnergy();
      return energy >= amount;
   }

   boolean useEnergy(double amount) {
      if (fuelFree) {
         return true;
      }
      if (amount <= this.energyInBuffer) {
         this.energyInBuffer -= amount;
         return true;
      } else {
         List<ISGEnergySource> sources = this.findEnergySources();
         double energyAvailable = this.energyInBuffer + this.energyAvailableFrom(sources);
         if (amount > energyAvailable) {
            return false;
         } else {
            double desiredEnergy = Math.max(amount, maxEnergyBuffer);
            double targetEnergy = Math.min(desiredEnergy, energyAvailable);
            double energyRequired = targetEnergy - this.energyInBuffer;
            double energyOnHand = this.energyInBuffer + this.drawEnergyFrom(sources, energyRequired);
            if (amount - 1.0E-4 > energyOnHand) {
               return false;
            } else {
               this.setEnergyInBuffer(energyOnHand - amount);
               return true;
            }
         }
      }
   }

   List<ISGEnergySource> findEnergySources() {
      List<ISGEnergySource> result = new ArrayList();
      Trans3 t = this.localToGlobalTransformation();

      for(int i = -2; i <= 2; ++i) {
         BlockPos bp = t.p((double)i, (double)-1.0F, (double)0.0F).blockPos();
         TileEntity nte = BaseBlockUtils.getWorldTileEntity(this.worldObj, bp);
         if (nte instanceof ISGEnergySource) {
            result.add((ISGEnergySource)nte);
         }
      }

      DHDTE te = this.getLinkedControllerTE();
      if (te != null) {
         result.add(te);
      }

      return result;
   }

   double energyAvailableFrom(List<ISGEnergySource> sources) {
      double energy = (double)0.0F;

      for(ISGEnergySource source : sources) {
         double e = source.availableEnergy();
         energy += e;
      }

      return energy;
   }

   double drawEnergyFrom(List<ISGEnergySource> sources, double amount) {
      double total = (double)0.0F;

      for(ISGEnergySource source : sources) {
         if (total >= amount) {
            break;
         }

         double e = source.drawEnergy(amount - total);
         total += e;
      }

      return total;
   }

   void setEnergyInBuffer(double amount) {
      if (this.energyInBuffer != amount) {
         this.energyInBuffer = amount;
         this.markDirty();
      }

   }

   void performTransientDamage() {
      Trans3 t = this.localToGlobalTransformation();
      Vector3 p0 = t.p((double)-1.5F, (double)0.5F, (double)0.5F);
      Vector3 p1 = t.p((double)1.5F, (double)3.5F, (double)5.5F);
      Vector3 q0 = p0.min(p1);
      Vector3 q1 = p0.max(p1);
      AxisAlignedBB box = BaseUtils.newAxisAlignedBB(q0.x, q0.y, q0.z, q1.x, q1.y, q1.z);

      for(Object value : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
         EntityLivingBase ent = (EntityLivingBase)value;
         Vector3 ep = new Vector3(ent.posX, ent.posY, ent.posZ);
         Vector3 gp = t.p((double)0.0F, (double)2.0F, (double)0.5F);
         double dist = ep.distance(gp);
         if (dist > (double)1.0F) {
            dist = (double)1.0F;
         }

         int damage = (int)Math.ceil(dist * (double)50.0F);
         ent.attackEntityFrom(transientDamage, (float)damage);
      }

   }

   boolean undialledDigitsRemaining() {
      int n = this.numEngagedChevrons;
      return n < this.diallingChevronCount;
   }

   void startDiallingNextSymbol() {
      if (this.interDialingCounter < this.interDialingDelay) {
         ++this.interDialingCounter;
      } else if (this.undialledDigitsRemaining()) {
         this.startDiallingSymbol(this.dialledAddress.charAt(this.numEngagedChevrons));
      } else {
         this.enterState(SGState.Transient, 20);
         this.connectedTime = 0;
         if (this.type == 0) {
            this.playSGSoundEffect("sgcraft:sg_open", 1.0F, 1.0F);
         } else {
            this.playSGSoundEffect("sgcraft:sg_open" + this.type, 1.0F, 1.0F);
         }
      }

   }

   void startDiallingSymbol(char c) {
      int i = SGAddressing.charToSymbol(c);
      if (this.type == 1) {
          if (this.numEngagedChevrons + 1 == this.diallingChevronCount) {
             i = 0;
          } else if (this.numEngagedChevrons + 1 <= 3) {
            i = (this.numEngagedChevrons + 1) * 4;
         } else if (this.numEngagedChevrons + 1 > 3 && this.numEngagedChevrons + 1 <= 6) {
            i = (this.numEngagedChevrons + 3) * 4;
          } else if (this.diallingChevronCount > 7 && this.numEngagedChevrons + 1 >= 7 && this.numEngagedChevrons + 1 < 9) {
             i = (this.numEngagedChevrons - 2) * 4;
          } else {
            i = (this.numEngagedChevrons + 1) * 4;
         }
      }

      if (i >= 0 && i < numRingSymbols[this.type]) {
         if (this.angleDirection) {
            this.startDiallingToAngle((double)360.0F - (double)i * ringSymbolAngle[this.type]);
         } else {
            this.startDiallingToAngle((double)i * -ringSymbolAngle[this.type]);
         }

         if (this.type == 1) {
            this.angleDirection = !this.angleDirection;
         }

         if (!this.qd) {
            String dialSound = this.diallingChevronCount > 7 ? "sg_dial9" : "sg_dial7";
            if (this.type == 0) {
               this.playSGSoundEffect("sgcraft:" + dialSound, 1.0F, 1.0F);
            } else {
               this.playSGSoundEffect("sgcraft:" + dialSound + this.type, 1.0F, 1.0F);
            }
         } else if (this.type == 0) {
            this.playSGSoundEffect("sgcraft:sg_dial7QD", 1.0F, 1.0F);
         } else {
            this.playSGSoundEffect("sgcraft:sg_dial7QD" + this.type, 1.0F, 1.0F);
         }
      } else {
         this.dialledAddress = "";
         this.enterState(SGState.Idle, 0);
      }

   }

   void startDiallingToAngle(double a) {
      if (this.type == 3) {
         a += ringSymbolAngle[this.type] / (double)2.0F;
      }

      if (this.type != 1) {
         this.targetRingAngle = Utils.normaliseAngle(a);
      } else {
         this.targetRingAngle = a;
      }

      if (this.qd) {
         this.enterState(SGState.Dialling, 4);
      } else {
         this.enterState(SGState.Dialling, diallingTime[this.type]);
      }

   }

   void finishDiallingSymbol() {
      this.lastNumChevrons = this.numEngagedChevrons++;
      this.newChevronEngaged = true;
      this.interDialingCounter = 0;
      String symbol = this.dialledAddress.substring(this.numEngagedChevrons - 1, this.numEngagedChevrons);
      this.postEvent("sgChevronEngaged", this.numEngagedChevrons, symbol);
      if (this.undialledDigitsRemaining()) {
         if (this.qd) {
            this.enterState(SGState.InterDialling, 1);
         } else {
            this.enterState(SGState.InterDialling, 10);
         }
      } else {
         this.finishDiallingAddress();
      }

   }

   void finishDiallingAddress() {
      if (this.isInitiator && !this.useEnergy(energyToOpen * this.distanceFactor)) {
         this.disconnect();
      } else {
         this.enterState(SGState.InterDialling, 10);
      }

   }

   boolean canTravelFromThisEnd() {
      return this.isInitiator || !oneWayTravel;
   }

   static String repr(Entity entity) {
      if (entity != null) {
         String s = String.format("%s#%s", entity.getClass().getSimpleName(), entity.getEntityId());
         if (entity.isDead) {
            s = s + "(dead)";
         }

         return s;
      } else {
         return "null";
      }
   }

   void checkForEntitiesInPortal(byte id) {
      if (this.state == SGState.Connected) {
         for(TrackedEntity trk : this.trackedEntities) {
            this.entityInPortal(id, trk.entity, trk.lastPos);
         }

         this.trackedEntities.clear();
         Vector3 p0 = new Vector3((double)-1.5F, (double)0.5F, (double)-3.5F);
         Vector3 p1 = new Vector3((double)1.5F, (double)3.5F, (double)3.5F);
         Trans3 t = this.localToGlobalTransformation();
         AxisAlignedBB box = t.box(p0, p1);

         for(Object value : this.worldObj.getEntitiesWithinAABB(Entity.class, box)) {
            Entity entity = (Entity)value;
            if (!(entity instanceof EntityFishHook) && !entity.isDead && entity.ridingEntity == null) {
               this.trackedEntities.add(new TrackedEntity(entity));
            }
         }
      } else {
         this.trackedEntities.clear();
      }

   }

   public void entityInPortal(byte id, Entity entity, Vector3 prevPos) {
      if (!entity.isDead && this.state == SGState.Connected && this.canTravelFromThisEnd() && !(entity instanceof EntityPlayer && SGWormholeTravel.protects((EntityPlayer)entity))) {
         Trans3 t = this.localToGlobalTransformation();
         double vx = entity.posX - prevPos.x;
         double vy = entity.posY - prevPos.y;
         double vz = entity.posZ - prevPos.z;
         Vector3 p1 = t.ip(entity.posX, entity.posY, entity.posZ);
         Vector3 p0 = t.ip((double)2.0F * prevPos.x - entity.posX, (double)2.0F * prevPos.y - entity.posY, (double)2.0F * prevPos.z - entity.posZ);
         double z0 = (double)0.0F;
         if (p0.z >= z0 && p1.z < z0 && p1.z > z0 - (double)5.0F) {
            entity.motionX = vx;
            entity.motionY = vy;
            entity.motionZ = vz;
            SGBaseTE dte = this.getConnectedStargateTE();
            if (dte != null) {
               Trans3 dt = dte.localToGlobalTransformation();
               boolean st = false;
               if (dte.type != this.type) {
                  st = random.nextInt(100) < crossTypeFailurePercent;
               }

               while(entity.ridingEntity != null) {
                  entity = entity.ridingEntity;
               }

               this.teleportEntityAndRider(id, entity, t, dt, this.connectedLocation.dimension, dte.irisIsClosed(), st);
            }
         }
      }

   }

   Entity teleportEntityAndRider(byte id, Entity entity, Trans3 t1, Trans3 t2, int dimension, boolean destBlocked, boolean st) {
      Entity rider = entity.riddenByEntity;
      if (rider != null) {
         rider.mountEntity((Entity)null);
         rider = this.teleportEntityAndRider(id, rider, t1, t2, dimension, destBlocked, st);
      }

      unleashEntity(entity);
      entity = teleportEntity(this, this.getConnectedStargateTE(), id, entity, t1, t2, dimension, destBlocked, st);
      if (entity != null && !entity.isDead && rider != null && !rider.isDead) {
         rider.mountEntity(entity);
      }

      return entity;
   }

   protected static void unleashEntity(Entity entity) {
      if (entity instanceof EntityLiving) {
         ((EntityLiving)entity).clearLeashed(true, false);
      }

      for(EntityLiving entity2 : entitiesWithinLeashRange(entity)) {
         if (entity2.getLeashed() && entity2.getLeashedToEntity() == entity) {
            entity2.clearLeashed(true, false);
         }
      }

   }

   protected static List<EntityLiving> entitiesWithinLeashRange(Entity entity) {
      AxisAlignedBB box = AxisAlignedBB.getBoundingBox(entity.posX - (double)7.0F, entity.posY - (double)7.0F, entity.posZ - (double)7.0F, entity.posX + (double)7.0F, entity.posY + (double)7.0F, entity.posZ + (double)7.0F);
      return entity.worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
   }

   static Entity teleportEntity(SGBaseTE te, SGBaseTE dest, byte id, Entity entity, Trans3 t1, Trans3 t2, int dimension, boolean destBlocked, boolean st) {
      Entity newEntity = null;
      Vector3 p = t1.ip(entity.posX, entity.posY, entity.posZ);
      Vector3 v = t1.iv(entity.motionX, entity.motionY, entity.motionZ);
      Vector3 s = transformedLook(t1, t2, yawSign(entity) * entity.rotationYaw, entity.rotationPitch);
      Vector3 q = destBlocked ? t2.p(-p.x, p.y, -p.z) : safeArrival(dest, entity, t2, new Vector3(-p.x, p.y, -p.z));
      if (q == null) {
         if (entity instanceof EntityPlayer) {
            sendChatMessage((EntityPlayer)entity, "Destination exit is blocked");
         }
         return entity;
      }
      Vector3 u = t2.v(-v.x, v.y, -v.z);
      double a = yawAngle(s, entity);
      float pitch = (float)Math.toDegrees(Math.atan2(-s.y, Math.hypot(s.x, s.z)));
      if (!destBlocked) {
         if (entity.dimension == dimension) {
            if (st) {
               terminateAnythingByPortal(entity);
            } else {
               double volume = Math.min((double)(entity.width * entity.height), (double)1.0F);
               double soundPitch = (double)2.0F - volume;
               worldForDimension(dimension).playSoundEffect(entity.posX, entity.posY, entity.posZ, "sgcraft:sg_tp", (float)volume, (float)soundPitch);
               if (!(entity instanceof EntityPlayer)) {
                  worldForDimension(dimension).playSoundEffect(q.x, q.y, q.z, "sgcraft:sg_tp", (float)volume, (float)soundPitch);
               }

               newEntity = teleportWithinDimension(entity, q, u, a, pitch, destBlocked);
            }
         } else if (st) {
            terminateAnythingByPortal(entity);
         } else {
            double volume = Math.min((double)(entity.width * entity.height), (double)1.0F);
            double soundPitch = (double)2.0F - volume;
            worldForDimension(dimension).playSoundEffect(entity.posX, entity.posY, entity.posZ, "sgcraft:sg_tp", (float)volume, (float)soundPitch);
            if (!(entity instanceof EntityPlayer)) {
               worldForDimension(dimension).playSoundEffect(q.x, q.y, q.z, "sgcraft:sg_tp", (float)volume, (float)soundPitch);
            }

            newEntity = teleportToOtherDimension(entity, q, u, a, pitch, dimension, destBlocked);
            if (newEntity != null) {
               newEntity.dimension = dimension;
            }
         }
      } else {
         terminateEntityByIrisImpact(entity);
         playIrisHitSound(dest.barrierKind, id, worldForDimension(dimension), q, entity);
         dest.irisHitTime = -1.0F;
         dest.irisHitEntitySize = entity.height;
         dest.irisHitOffsetX = (float)(Math.random() * (double)2.4F) - 1.2F;
         dest.markChanged();
      }

      if (newEntity instanceof EntityPlayerMP) {
         SGWormholeTravel.begin((EntityPlayerMP)newEntity);
      }
      return newEntity;
   }

   static double exitClearance(int orientation, float height) {
      return orientation >= 8 ? height + 0.5 : orientation >= 4 ? 0.5 : 1.0;
   }

   static Vector3 transformedLook(Trans3 source, Trans3 destination, float yaw, float pitch) {
      double y = Math.toRadians(yaw);
      double p = Math.toRadians(pitch);
      Vector3 local = source.iv(-Math.cos(p) * Math.sin(y), -Math.sin(p), Math.cos(p) * Math.cos(y));
      return destination.v(-local.x, local.y, -local.z);
   }

   static Vector3 safeArrival(SGBaseTE destination, Entity entity, Trans3 transform, Vector3 local) {
      double start = Math.max(local.z, exitClearance(destination.gateOrientation(), entity.height));
      for (double forward = start; forward <= start + 3.0; forward += 0.5) {
         Vector3 point = transform.p(local.x, local.y, forward);
         double halfWidth = entity.width / 2.0;
         AxisAlignedBB box = AxisAlignedBB.getBoundingBox(point.x - halfWidth, point.y, point.z - halfWidth, point.x + halfWidth, point.y + entity.height, point.z + halfWidth);
         if (destination.worldObj.getCollidingBoundingBoxes(entity, box).isEmpty()) {
            return point;
         }
      }
      return null;
   }

   static void terminateEntityByIrisImpact(Entity entity) {
      if (entity instanceof EntityPlayer) {
         terminatePlayerByIrisImpact((EntityPlayer)entity);
      } else {
         entity.setDead();
      }

   }

   static void terminatePlayerByIrisImpact(EntityPlayer player) {
      if (player.capabilities.isCreativeMode) {
         sendChatMessage(player, "Destination blocked by iris");
      } else {
         if (!preserveInventory && !BaseUtils.getGameRuleBoolean(player.worldObj.getGameRules(), "keepInventory")) {
            BaseInventoryUtils.clearInventory(player.inventory);
         }

         player.attackEntityFrom(irisDamageSource, 1000000.0F);
      }

   }

   static void terminateAnythingByPortal(Entity entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!player.capabilities.isCreativeMode) {
            if (!preserveInventory && !BaseUtils.getGameRuleBoolean(player.worldObj.getGameRules(), "keepInventory")) {
               BaseInventoryUtils.clearInventory(player.inventory);
            }

            player.attackEntityFrom(portalDamageSource, 1000000.0F);
         }
      } else {
         entity.attackEntityFrom(portalDamageSource, 1000000.0F);
      }

   }

   static WorldServer worldForDimension(int dimension) {
      MinecraftServer server = MinecraftServer.getServer();
      return server.worldServerForDimension(dimension);
   }

   static void playIrisHitSound(BarrierKind kind, byte id, World world, Vector3 pos, Entity entity) {
      double volume = Math.min((double)(entity.width * entity.height), (double)1.0F);
      double pitch = (double)2.0F - volume;
      world.playSoundEffect(pos.x, pos.y, pos.z, "sgcraft:" + barrierSound(kind, id, "hit"), (float)volume, (float)pitch);
   }

   static String barrierSound(BarrierKind kind, int type, String action) {
      if (kind == BarrierKind.IRIS) {
         return type == 0 ? "iris_" + action : "iris_metal_" + action + type;
      }
      return type == 0 ? "iris_shield_" + action + "0" : "iris_" + action + type;
   }

   static Entity teleportWithinDimension(Entity entity, Vector3 p, Vector3 v, double a, float pitch, boolean destBlocked) {
      return entity instanceof EntityPlayerMP ? teleportPlayerWithinDimension((EntityPlayerMP)entity, p, v, a, pitch) : teleportEntityToWorld(entity, p, v, a, pitch, (WorldServer)entity.worldObj, destBlocked);
   }

   static Entity teleportPlayerWithinDimension(EntityPlayerMP entity, Vector3 p, Vector3 v, double a, float pitch) {
      entity.rotationYaw = (float)a;
      entity.rotationPitch = pitch;
      entity.setPositionAndUpdate(p.x, p.y, p.z);
      setVelocity(entity, v);
      entity.worldObj.updateEntityWithOptionalForce(entity, false);
      worldForDimension(entity.dimension).playSoundEffect(entity.posX, entity.posY, entity.posZ, "sgcraft:sg_tp", 1.0F, 1.0F);
      return entity;
   }

   static Entity teleportToOtherDimension(Entity entity, Vector3 p, Vector3 v, double a, float pitch, int dimension, boolean destBlocked) {
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         transferPlayerToDimension(player, dimension, p, a, pitch);
         setVelocity(player, v);
         return player;
      } else {
         return teleportEntityToDimension(entity, p, v, a, pitch, dimension, destBlocked);
      }
   }

   static void sendDimensionRegister(EntityPlayerMP player, int dimensionID) {
      int providerID = DimensionManager.getProviderType(dimensionID);
      ForgeMessage msg = new ForgeMessage.DimensionRegisterMessage(dimensionID, providerID);
      FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
      channel.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   static void transferPlayerToDimension(EntityPlayerMP player, int newDimension, Vector3 p, double a, float pitch) {
      MinecraftServer server = MinecraftServer.getServer();
      ServerConfigurationManager scm = server.getConfigurationManager();
      int oldDimension = player.dimension;
      player.dimension = newDimension;
      WorldServer oldWorld = server.worldServerForDimension(oldDimension);
      WorldServer newWorld = server.worldServerForDimension(newDimension);
      sendDimensionRegister(player, newDimension);
      player.closeScreen();
      player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, BaseUtils.getWorldDifficulty(player.worldObj), newWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
      oldWorld.removePlayerEntityDangerously(player);
      player.isDead = false;
      player.setLocationAndAngles(p.x, p.y, p.z, (float)a, pitch);
      newWorld.spawnEntityInWorld(player);
      player.setWorld(newWorld);
      BaseUtils.scmPreparePlayer(scm, player, oldWorld);
      player.playerNetServerHandler.setPlayerLocation(p.x, p.y, p.z, (float)a, pitch);
      player.theItemInWorldManager.setWorld(newWorld);
      scm.updateTimeAndWeatherForPlayer(player, newWorld);
      scm.syncPlayerInventory(player);

      for(Object value : player.getActivePotionEffects()) {
         PotionEffect effect = (PotionEffect)value;
         player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
      }

      player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
      FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimension, newDimension);
      worldForDimension(newDimension).playSoundEffect(player.posX, player.posY, player.posZ, "sgcraft:sg_tp", 1.0F, 1.0F);
   }

   static Entity teleportEntityToDimension(Entity entity, Vector3 p, Vector3 v, double a, float pitch, int dimension, boolean destBlocked) {
      MinecraftServer server = MinecraftServer.getServer();
      WorldServer world = server.worldServerForDimension(dimension);
      return teleportEntityToWorld(entity, p, v, a, pitch, world, destBlocked);
   }

   static Entity teleportEntityToWorld(Entity oldEntity, Vector3 p, Vector3 v, double a, float pitch, WorldServer newWorld, boolean destBlocked) {
      WorldServer oldWorld = (WorldServer)oldEntity.worldObj;
      NBTTagCompound nbt = new NBTTagCompound();
      oldEntity.writeToNBT(nbt);
      extractEntityFromWorld(oldWorld, oldEntity);
      if (destBlocked && !(oldEntity instanceof EntityLivingBase)) {
         return null;
      } else {
         Entity newEntity = instantiateEntityFromNBT(oldEntity.getClass(), nbt, newWorld);
         if (newEntity != null) {
            if (oldEntity instanceof EntityLiving) {
               copyMoreEntityData((EntityLiving)oldEntity, (EntityLiving)newEntity);
            }

            setVelocity(newEntity, v);
            newEntity.setLocationAndAngles(p.x, p.y, p.z, (float)a, pitch);
            checkChunk(newWorld, newEntity);
            newEntity.forceSpawn = true;
            newWorld.spawnEntityInWorld(newEntity);
            newEntity.setWorld(newWorld);
         }

         oldWorld.resetUpdateEntityTick();
         if (oldWorld != newWorld) {
            newWorld.resetUpdateEntityTick();
         }

         return newEntity;
      }
   }

   static Entity instantiateEntityFromNBT(Class cls, NBTTagCompound nbt, WorldServer world) {
      try {
         Entity entity = (Entity)cls.getConstructor(World.class).newInstance(world);
         entity.readFromNBT(nbt);
         return entity;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   static void copyMoreEntityData(EntityLiving oldEntity, EntityLiving newEntity) {
      float s = oldEntity.getAIMoveSpeed();
      if (s != 0.0F) {
         newEntity.setAIMoveSpeed(s);
      }

   }

   static void setVelocity(Entity entity, Vector3 v) {
      entity.motionX = v.x;
      entity.motionY = v.y;
      entity.motionZ = v.z;
   }

   static void extractEntityFromWorld(World world, Entity entity) {
      if (entity instanceof EntityPlayer) {
         world.playerEntities.remove(entity);
         world.updateAllPlayersSleepingFlag();
      }

      int i = entity.chunkCoordX;
      int j = entity.chunkCoordZ;
      if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j)) {
         world.getChunkFromChunkCoords(i, j).removeEntity(entity);
      }

      world.loadedEntityList.remove(entity);
      world.onEntityRemoved(entity);
   }

   static void checkChunk(World world, Entity entity) {
      int cx = MathHelper.floor_double(entity.posX / (double)16.0F);
      int cy = MathHelper.floor_double(entity.posZ / (double)16.0F);
      world.getChunkFromChunkCoords(cx, cy);
   }

   protected static int yawSign(Entity entity) {
      return entity instanceof EntityArrow ? -1 : 1;
   }

   static Vector3 yawVector(Entity entity) {
      return yawVector((double)((float)yawSign(entity) * entity.rotationYaw));
   }

   static Vector3 yawVector(double yaw) {
      double a = Math.toRadians(yaw);
      Vector3 v = new Vector3(-Math.sin(a), (double)0.0F, Math.cos(a));
      return v;
   }

   static double yawAngle(Vector3 v, Entity entity) {
      double a = Math.atan2(-v.x, v.z);
      double d = Math.toDegrees(a);
      return (double)yawSign(entity) * d;
   }

   public SGBaseTE getConnectedStargateTE() {
      return this.isConnected() && this.connectedLocation != null ? this.connectedLocation.getStargateTE() : null;
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      SGState oldState = this.state;
      super.onDataPacket(net, pkt);
      if (this.isMerged && this.state != oldState) {
         switch (this.state) {
            case Transient:
               this.initiateOpeningTransient();
               break;
            case Disconnecting:
               this.initiateClosingTransient();
         }
      }

   }

   void clientUpdate() {
      this.lastRingAngle = this.ringAngle;
      switch (this.state) {
         case Dialling:
            if (!this.qd) {
               this.updateRingAngle();
            }
         case InterDialling:
         default:
            break;
         case Transient:
         case Connected:
         case Disconnecting:
            this.applyRandomImpulse();
            this.updateEventHorizon();
      }

   }

   void setRingAngle(double a) {
      this.ringAngle = a;
   }

   void updateRingAngle() {
      if (this.timeout > 0) {
         if (this.type == 1) {
            this.setRingAngle(this.ringAngle + (this.targetRingAngle - this.ringAngle) / (double)this.timeout);
         } else {
            double da = Utils.diffAngle(this.ringAngle, this.targetRingAngle) / (double)this.timeout;
            this.setRingAngle(Utils.addAngle(this.ringAngle, da));
         }

         --this.timeout;
      } else {
         this.setRingAngle(this.targetRingAngle);
      }

   }

   public float[][][] getEventHorizonGrid() {
      if (this.ehGrid == null) {
         this.ehGrid = new float[this.ehResolution][this.ehResolution][4];

         for(int i = 0; i < this.ehResolution; ++i) {
            for(int j = 0; j < this.ehResolution; ++j) {
               this.ehGrid[i][j] = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
            }
         }
      }

      return this.ehGrid;
   }

   void initiateOpeningTransient() {
      this.transientStartTime = 0.0F;
      this.getEventHorizonGrid();
      this.ehTime = 0;
   }

   public static int openingFrame(float elapsed) {
      return Math.min(184, Math.max(0, (int)(elapsed * 1.85F)));
   }

   void initiateClosingTransient() {
   }

   void applyRandomImpulse() {
   }

   void updateEventHorizon() {
      float maxTime = 20.0F;
      if (this.ehGrid != null && this.ehTime > -1) {
         float progress = this.clamp((float)((double)(-(1.0F / (maxTime * maxTime))) * Math.pow((double)((float)this.ehTime - maxTime), (double)2.0F) + (double)1.0F), 0.0F, 1.0F) * 4.0F * (float)(this.irisPhase / maxIrisPhase[this.type]);

         for(int i = 2; i < this.ehGrid.length - 2; ++i) {
            for(int j = 2; j < this.ehGrid[i].length - 2; ++j) {
               this.ehGrid[i][j][1] = this.ehGrid[i][j][0];
               float x = (float)(-0.11111111 * Math.pow((double)(i - 3 - 2), (double)2.0F) + (double)1.0F);
               float y = (float)(-0.11111111 * Math.pow((double)(j - 3 - 2), (double)2.0F) + (double)1.0F);
               this.ehGrid[i][j][0] = x * y * progress;
            }
         }

         ++this.ehTime;
      }

      if ((float)this.ehTime == maxTime * 2.0F + 1.0F && this.ehGrid != null) {
         for(int i = 0; i < this.ehGrid.length; ++i) {
            for(int j = 0; j < this.ehGrid[i].length; ++j) {
               this.ehGrid[i][j][1] = this.ehGrid[i][j][0];
            }
         }

         this.ehTime = -1;
      }

   }

   protected float clamp(float number, float min, float max) {
      return Math.min(max, Math.max(min, number));
   }

   protected IInventory getInventory() {
      return this.inventory;
   }

   public boolean irisIsClosed() {
      return this.hasIrisUpgrade && this.irisPhase < maxIrisPhase[this.type];
   }

   public double getIrisAperture(double t) {
      return ((double)this.lastIrisPhase * ((double)1.0F - t) + (double)this.irisPhase * t) / (double)maxIrisPhase[this.type];
   }

   void irisUpdate() {
      this.lastIrisPhase = this.irisPhase;
      switch (this.irisState) {
         case Opening:
            if (this.irisPhase < maxIrisPhase[this.type]) {
               ++this.irisPhase;
            } else {
               this.enterIrisState(IrisState.Open);
            }
            break;
         case Closing:
            if (this.irisPhase > 0) {
               --this.irisPhase;
            } else {
               this.enterIrisState(IrisState.Closed);
            }
      }

   }

   void enterIrisState(IrisState newState) {
      if (this.irisState != newState) {
         String oldDesc = irisStateDescription(this.irisState);
         String newDesc = irisStateDescription(newState);
         this.irisState = newState;
         this.markChanged();
         if (!this.worldObj.isRemote) {
            switch (newState) {
               case Opening:
                  this.playSGSoundEffect("sgcraft:" + barrierSound(this.barrierKind, this.type, "open"), 1.0F, 1.0F);
                  break;
               case Closing:
                  this.playSGSoundEffect("sgcraft:" + barrierSound(this.barrierKind, this.type, "close"), 1.0F, 1.0F);
            }
         }

         if (!oldDesc.equals(newDesc)) {
            this.postEvent("sgIrisStateChange", newDesc, oldDesc);
         }
      }

   }

   public void openIris() {
      if (this.isMerged && this.hasIrisUpgrade && this.irisState != IrisState.Open) {
         this.enterIrisState(IrisState.Opening);
      }

   }

   public void closeIris() {
      if (this.isMerged && this.hasIrisUpgrade && this.irisState != IrisState.Closed) {
         this.enterIrisState(IrisState.Closing);
      }

   }

   public void onNeighborBlockChange() {
      if (!this.worldObj.isRemote) {
         boolean newInput = BaseBlockUtils.blockIsGettingExternallyPowered(this.worldObj, this.getPos());
         if (this.redstoneInput != newInput) {
            this.redstoneInput = newInput;
            this.markDirty();
            if (this.redstoneInput) {
               this.closeIris();
            } else {
               this.openIris();
            }
         }
      }

   }

   void updateIrisEntity() {
      if (!this.worldObj.isRemote) {
         if (this.isMerged && this.hasIrisUpgrade) {
            if (!this.hasIrisEntity()) {
               IrisEntity ent = new IrisEntity(this);
               this.worldObj.spawnEntityInWorld(ent);
            }
         } else {
            for(IrisEntity ent : this.findIrisEntities()) {
               this.worldObj.removeEntity(ent);
            }
         }
      }

   }

   boolean hasIrisEntity() {
      return this.findIrisEntities().size() != 0;
   }

   List<IrisEntity> findIrisEntities() {
      int x = this.getX();
      int y = this.getY();
      int z = this.getZ();
      AxisAlignedBB box = BaseUtils.newAxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 2), (double)(z + 1));
      return this.worldObj.getEntitiesWithinAABB(IrisEntity.class, box);
   }

   boolean isCamouflageSlot(int slot) {
      return slot >= 0 && slot < 24;
   }

   protected void onInventoryChanged(int slot) {
      super.onInventoryChanged(slot);
      if (this.isCamouflageSlot(slot)) {
         for(int dx = -3; dx <= 3; ++dx) {
            for(int dz = -3; dz <= 3; ++dz) {
               BaseBlockUtils.markWorldBlockForUpdate(this.worldObj, this.getPos().add(dx, 0, dz));
            }
         }
      }

   }

   public int numItemsInSlot(int slot) {
      ItemStack stack = this.getStackInSlot(slot);
      return stack != null ? stack.stackSize : 0;
   }

   protected int baseCornerCamouflage() {
      return Math.max(this.baseCamouflageAt(0), this.baseCamouflageAt(6));
   }

   protected int baseCamouflageAt(int i) {
      ItemStack stack = this.getStackInSlot(i);
      if (stack != null) {
         Item item = stack.getItem();
         Block block = Block.getBlockFromItem(stack.getItem());
         if (block != null) {
            if (block instanceof BlockSlab) {
               return 1;
            }

            if (block.isBlockNormalCube()) {
               return 2;
            }
         }
      }

      return 0;
   }

   public Collection<BlockRef> adjacentTiles() {
      Collection<BlockRef> result = new ArrayList();
      Trans3 t = this.localToGlobalTransformation();

      for(int i = -3; i <= 3; ++i) {
         BlockPos bp = t.p((double)i, (double)-1.0F, (double)0.0F).blockPos();
         TileEntity te = BaseBlockUtils.getWorldTileEntity(this.worldObj, bp);
         if (te != null) {
            result.add(new BlockRef(te));
         }
      }

      return result;
   }

   static boolean isAdjacentSlot(Trans3 t, BlockPos pos) {
      Vector3 p = t.ip(Vector3.blockCenter(pos));
      return p.roundY() == -1 && p.roundZ() == 0 && Math.abs(p.roundX()) <= 3;
   }

   public void forwardNetworkPacket(Object packet) {
      SGBaseTE dte = this.getConnectedStargateTE();
      if (dte != null) {
         dte.rebroadcastNetworkPacket(packet);
      }

   }

   void rebroadcastNetworkPacket(Object packet) {
      for(BlockRef ref : this.adjacentTiles()) {
         TileEntity te = ref.getTileEntity();
         if (te instanceof SGInterfaceTE) {
            ((SGInterfaceTE)te).rebroadcastNetworkPacket(packet);
         }
      }

   }

   public String sendMessage(Object[] args) {
      SGBaseTE dte = this.getConnectedStargateTE();
      if (dte != null) {
         dte.postEvent("sgMessageReceived", args);
         return null;
      } else {
         return "Stargate not connected";
      }
   }

   void postEvent(String name, Object... args) {
      for(BlockRef b : this.adjacentTiles()) {
         TileEntity te = b.getTileEntity();
         if (te instanceof IComputerInterface) {
            ((IComputerInterface)te).postEvent(this, name, args);
         }
      }

   }

   public String sgStateDescription() {
      return sgStateDescription(this.state);
   }

   static String sgStateDescription(SGState state) {
      switch (state) {
         case Idle:
            return "Idle";
         case Dialling:
         case InterDialling:
            return "Dialling";
         case Transient:
            return "Opening";
         case Connected:
            return "Connected";
         case Disconnecting:
            return "Closing";
         default:
            return "Unknown";
      }
   }

   public String irisStateDescription() {
      return irisStateDescription(this.irisState);
   }

   static String irisStateDescription(IrisState state) {
      return state.toString();
   }

   static {
      ringSymbolAngle = new double[]{(double)360.0F / (double)numRingSymbols[0], (double)360.0F / (double)numRingSymbols[1], (double)360.0F / (double)numRingSymbols[2], (double)360.0F / (double)numRingSymbols[3], (double)360.0F / (double)numRingSymbols[4]};
      irisDamageSource = new DamageSource("iris");
      portalDamageSource = new DamageSource("portal");
      diallingTime = new int[]{40, 32, 40, 40, 40};
      maxIrisPhase = new int[]{60, 20, 20, 30, 20};
      defaultChevronAngle = 40.0F;
      chevronAngles = new float[][]{{45.0F, 45.0F, 40.0F}, {36.0F, 33.0F, 30.0F}};
      maxEnergyBuffer = (double)1000.0F;
      energyPerFuelItem = (double)96000.0F;
      distanceFactorMultiplier = (double)1.0F;
      interDimensionMultiplier = (double)4.0F;
      gateOpeningsPerFuelItem = 24;
      minutesOpenPerFuelItem = 80;
      secondsToStayOpen = 300;
      oneWayTravel = false;
      closeFromEitherEnd = true;
      chunkLoadingRange = 1;
      preserveInventory = false;
      soundVolume = 1.0F;
      variableChevronPositions = true;
      transparency = true;
      random = new Random();
      transientDamage = new DamageSource("transient");
   }

   class TrackedEntity {
      public Entity entity;
      public Vector3 lastPos;

      public TrackedEntity(Entity entity) {
         this.entity = entity;
         this.lastPos = new Vector3(entity.posX, entity.posY, entity.posZ);
      }
   }
}
