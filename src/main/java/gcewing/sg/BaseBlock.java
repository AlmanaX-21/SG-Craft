package gcewing.sg;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BaseBlock<TE extends TileEntity> extends BlockContainer implements BaseMod.IBlock {
   protected static Random RANDOM = new Random();
   public static IOrientationHandler orient1Way = new Orient1Way();
   protected MapColor mapColor;
   protected final BlockState blockState;
   protected IBlockState defaultBlockState;
   protected IProperty[] properties;
   protected Object[][] propertyValues;
   protected int numProperties;
   protected int renderID;
   protected Class<? extends TileEntity> tileEntityClass;
   protected IOrientationHandler orientationHandler;
   protected String[] textureNames;
   protected BaseMod.ModelSpec modelSpec;
   protected BaseMod mod;
   protected AxisAlignedBB boxHit;
   protected ThreadLocal<TileEntity> harvestingTileEntity;
   protected static AxisAlignedBB defaultCollisionBox = AxisAlignedBB.getBoundingBox((double)-0.5F, (double)-0.5F, (double)-0.5F, (double)0.5F, (double)0.5F, (double)0.5F);

   public Class getDefaultItemClass() {
      return BaseItemBlock.class;
   }

   public BaseBlock(Material material) {
      this(material, (IOrientationHandler)null, (Class)null, (String)null);
   }

   public BaseBlock(Material material, IOrientationHandler orient) {
      this(material, orient, (Class)null, (String)null);
   }

   public BaseBlock(Material material, Class<TE> teClass) {
      this(material, (IOrientationHandler)null, teClass, (String)null);
   }

   public BaseBlock(Material material, IOrientationHandler orient, Class<TE> teClass) {
      this(material, orient, teClass, (String)null);
   }

   public BaseBlock(Material material, Class<TE> teClass, String teID) {
      this(material, (IOrientationHandler)null, teClass, teID);
   }

   public BaseBlock(Material material, IOrientationHandler orient, Class<TE> teClass, String teID) {
      super(material);
      this.tileEntityClass = null;
      this.orientationHandler = orient1Way;
      this.harvestingTileEntity = new ThreadLocal();
      if (orient == null) {
         orient = orient1Way;
      }

      this.orientationHandler = orient;
      this.tileEntityClass = teClass;
      if (teClass != null) {
         if (teID == null) {
            teID = teClass.getName();
         }

         try {
            GameRegistry.registerTileEntity(teClass, teID);
         } catch (IllegalArgumentException var6) {
         }
      }

      this.blockState = this.createBlockState();
      this.defaultBlockState = this.blockState.getBaseState();
      this.opaque = true;
   }

   public BaseBlock setOpaque(boolean state) {
      this.opaque = state;
      return this;
   }

   public boolean isOpaqueCube() {
      return this.opaque;
   }

   public IOrientationHandler getOrientationHandler() {
      return this.orientationHandler;
   }

   protected void defineProperties() {
      this.properties = new IProperty[4];
      this.propertyValues = new Object[4][];
      this.getOrientationHandler().defineProperties(this);
   }

   protected void addProperty(IProperty property) {
      if (this.numProperties < 4) {
         int i = this.numProperties++;
         this.properties[i] = property;
         Object[] values = BaseUtils.arrayOf(property.getAllowedValues());
         this.propertyValues[i] = values;
      } else {
         throw new IllegalStateException("Block " + this.getClass().getName() + " has too many properties");
      }
   }

   protected BlockState createBlockState() {
      this.defineProperties();
      this.checkProperties();
      IProperty[] props = (IProperty[])Arrays.copyOf(this.properties, this.numProperties);
      return new BlockState(this, props);
   }

   protected void checkProperties() {
      int n = 1;

      for(int i = 0; i < this.numProperties; ++i) {
         n *= this.propertyValues[i].length;
      }

      if (n > 16) {
         throw new IllegalStateException(String.format("Block %s has %s combinations of property values (16 allowed)", this.getClass().getName(), n));
      }
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public final IBlockState getDefaultState() {
      return this.defaultBlockState;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return state;
   }

   public int getMetaFromState(IBlockState state) {
      int meta = 0;

      for(int i = this.numProperties - 1; i >= 0; --i) {
         Object value = state.getValue(this.properties[i]);
         Object[] values = this.propertyValues[i];

         int k;
         for(k = values.length - 1; k > 0 && !values[k].equals(value); --k) {
         }

         meta = meta * values.length + k;
      }

      return meta & 15;
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState();
      int m = meta;

      for(int i = this.numProperties - 1; i >= 0; --i) {
         Object[] values = this.propertyValues[i];
         int n = values.length;
         int k = m % n;
         m /= n;
         state = state.withProperty(this.properties[i], (Comparable)values[k]);
      }

      return state;
   }

   public int getNumSubtypes() {
      return 1;
   }

   public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
      TileEntity te = world.getTileEntity(x, y, z);
      this.harvestingTileEntity.set(te);
   }

   public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
      TileEntity te = (TileEntity)this.harvestingTileEntity.get();
      this.harvestBlock(world, player, new BlockPos(x, y, z), this.getStateFromMeta(meta), te);
   }

   public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      super.harvestBlock(world, player, pos.x, pos.y, pos.z, this.getMetaFromState(state));
   }

   public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
      IBlockState state = this.getStateFromMeta(meta);
      ArrayList<ItemStack> result = this.getDrops(world, new BlockPos(x, y, z), state, fortune);
      this.harvestingTileEntity.set(null);
      return result;
   }

   public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      TileEntity te = BaseBlockUtils.getWorldTileEntity(world, pos);
      if (te == null) {
         te = (TileEntity)this.harvestingTileEntity.get();
      }

      return this.getDropsFromTileEntity(world, pos, state, te, fortune);
   }

   protected ArrayList<ItemStack> getDropsFromTileEntity(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te, int fortune) {
      int meta = this.getMetaFromState(state);
      return super.getDrops((World)world, pos.x, pos.y, pos.z, meta, fortune);
   }

   public void setModelAndTextures(String modelName, String... textureNames) {
      this.textureNames = textureNames;
      this.modelSpec = new BaseMod.ModelSpec(modelName, textureNames);
   }

   public void setModelAndTextures(String modelName, Vector3 origin, String... textureNames) {
      this.textureNames = textureNames;
      this.modelSpec = new BaseMod.ModelSpec(modelName, origin, textureNames);
   }

   public String[] getTextureNames() {
      return this.textureNames;
   }

   public BaseMod.ModelSpec getModelSpec(IBlockState state) {
      return this.modelSpec;
   }

   public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
      return this.getBlockLayer() == layer;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.SOLID;
   }

   public int getRenderType() {
      return this.renderID;
   }

   public void setRenderType(int id) {
      this.renderID = id;
   }

   public String getQualifiedRendererClassName() {
      String name = this.getRendererClassName();
      if (name != null) {
         name = this.getClass().getPackage().getName() + "." + name;
      }

      return name;
   }

   protected String getRendererClassName() {
      return null;
   }

   public Trans3 localToGlobalRotation(IBlockAccess world, BlockPos pos) {
      return this.localToGlobalRotation(world, pos, BaseBlockUtils.getWorldBlockState(world, pos));
   }

   public Trans3 localToGlobalRotation(IBlockAccess world, BlockPos pos, IBlockState state) {
      return this.localToGlobalTransformation(world, pos, state, Vector3.zero);
   }

   public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos) {
      return this.localToGlobalTransformation(world, pos, BaseBlockUtils.getWorldBlockState(world, pos));
   }

   public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state) {
      return this.localToGlobalTransformation(world, pos, state, Vector3.blockCenter(pos));
   }

   public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state, Vector3 origin) {
      IOrientationHandler oh = this.getOrientationHandler();
      return oh.localToGlobalTransformation(world, pos, state, origin);
   }

   public boolean hasTileEntity(int meta) {
      return this.hasTileEntity(this.getStateFromMeta(meta));
   }

   public boolean hasTileEntity(IBlockState state) {
      return this.tileEntityClass != null;
   }

   public TE getTileEntity(IBlockAccess world, BlockPos pos) {
      return (TE)(this.hasTileEntity() ? world.getTileEntity(pos.x, pos.y, pos.z) : null);
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      if (this.tileEntityClass != null) {
         try {
            return (TileEntity)this.tileEntityClass.newInstance();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else {
         return null;
      }
   }

   public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getOrientationHandler().onBlockPlaced(this, world, pos, side, hitX, hitY, hitZ, this.getStateFromMeta(meta), placer);
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      super.onBlockAdded(world, x, y, z);
      BlockPos pos = new BlockPos(x, y, z);
      int meta = world.getBlockMetadata(x, y, z);
      if (this.hasTileEntity(meta)) {
         TileEntity te = this.getTileEntity(world, pos);
         if (te instanceof BaseMod.ITileEntity) {
            ((BaseMod.ITileEntity)te).onAddedToWorld();
         }
      }

      this.onBlockAdded(world, pos, this.getStateFromMeta(meta));
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
      BlockPos pos = new BlockPos(x, y, z);
      IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
      this.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
      BlockPos pos = new BlockPos(x, y, z);
      this.breakBlock(world, pos, this.getStateFromMeta(meta));
      if (this.hasTileEntity(meta)) {
         TileEntity te = world.getTileEntity(x, y, z);
         if (te instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
         }
      }

      super.breakBlock(world, x, y, z, block, meta);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
   }

   public boolean canHarvestBlock(EntityPlayer player, int meta) {
      return this.canHarvestBlock(this.getStateFromMeta(meta), player);
   }

   public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
      return super.canHarvestBlock(player, this.getMetaFromState(state));
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      int meta = world.getBlockMetadata(x, y, z);
      IBlockState state = this.getStateFromMeta(meta);
      return this.onBlockActivated(world, new BlockPos(x, y, z), state, player, BaseUtils.facings[side], hitX, hitY, hitZ);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float cx, float cy, float cz) {
      TileEntity te = this.getTileEntity(world, pos);
      if (te != null) {
         int id = this.mod.getGuiId(te.getClass());
         if (id >= 0) {
            this.mod.openGui(player, id, world, pos);
            return true;
         }
      }

      return false;
   }

   public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
      return side != ForgeDirection.UNKNOWN ? this.isSideSolid(world, new BlockPos(x, y, z), BaseUtils.facings[side.ordinal()]) : super.isSideSolid(world, x, y, z, side);
   }

   public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return super.isSideSolid(world, pos.x, pos.y, pos.z, ForgeDirection.VALID_DIRECTIONS[side.ordinal()]);
   }

   public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
      return this.getWeakChanges(world, new BlockPos(x, y, z));
   }

   public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
      return super.getWeakChanges(world, pos.x, pos.y, pos.z);
   }

   public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
      int meta = world.getBlockMetadata(x, y, z);
      IBlockState state = this.getStateFromMeta(meta);
      this.onNeighborBlockChange(world, new BlockPos(x, y, z), state, block);
   }

   public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
   }

   public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
      int meta = world.getBlockMetadata(x, y, z);
      IBlockState state = this.getStateFromMeta(meta);
      return this.getStrongPower(world, new BlockPos(x, y, z), state, BaseUtils.facings[side]);
   }

   public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
      return 0;
   }

   public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
      int meta = world.getBlockMetadata(x, y, z);
      IBlockState state = this.getStateFromMeta(meta);
      return this.getWeakPower(world, new BlockPos(x, y, z), state, BaseUtils.facings[side]);
   }

   public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
      return 0;
   }

   public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
      return this.shouldCheckWeakPower(world, new BlockPos(x, y, z), BaseUtils.facings[side]);
   }

   public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
      return super.shouldCheckWeakPower(world, pos.x, pos.y, pos.z, side.ordinal());
   }

   public void spawnAsEntity(World world, BlockPos pos, ItemStack stack) {
      this.dropBlockAsItem(world, pos.x, pos.y, pos.z, stack);
   }

   public int damageDropped(int meta) {
      return this.damageDropped(this.getStateFromMeta(meta));
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public MapColor getMapColor(int meta) {
      return this.mapColor != null ? this.mapColor : super.getMapColor(meta);
   }

   public Item getItemDropped(int meta, Random random, int fortune) {
      return this.getItemDropped(this.getStateFromMeta(meta), random, fortune);
   }

   public Item getItemDropped(IBlockState state, Random random, int fortune) {
      return super.getItemDropped(this.getMetaFromState(state), random, fortune);
   }

   public boolean renderAsNormalBlock() {
      return this.isFullCube();
   }

   public boolean isFullCube() {
      return super.renderAsNormalBlock();
   }

   public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
      return this.collisionRayTrace(world, new BlockPos(x, y, z), start, end);
   }

   public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
      this.boxHit = null;
      MovingObjectPosition result = null;
      double nearestDistance = (double)0.0F;
      IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
      List<AxisAlignedBB> list = this.getGlobalCollisionBoxes(world, pos, state, (Entity)null);
      if (list != null) {
         int n = list.size();

         for(int i = 0; i < n; ++i) {
            AxisAlignedBB box = (AxisAlignedBB)list.get(i);
            MovingObjectPosition mp = box.calculateIntercept(start, end);
            if (mp != null) {
               mp.subHit = i;
               double d = start.squareDistanceTo(mp.hitVec);
               if (result == null || d < nearestDistance) {
                  result = mp;
                  nearestDistance = d;
               }
            }
         }
      }

      if (result != null) {
         int i = result.subHit;
         this.boxHit = ((AxisAlignedBB)list.get(i)).offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
         result = BaseUtils.newMovingObjectPosition(result.hitVec, result.sideHit, pos);
         result.subHit = i;
      }

      return result;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
      this.setBlockBoundsBasedOnState(world, new BlockPos(x, y, z));
   }

   public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
      AxisAlignedBB box = this.boxHit;
      if (box == null) {
         IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
         box = this.getLocalBounds(world, pos, state, (Entity)null);
      }

      if (box != null) {
         this.setBlockBounds(box);
      } else {
         super.setBlockBoundsBasedOnState(world, pos.x, pos.y, pos.z);
      }

   }

   protected AxisAlignedBB getLocalBounds(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity) {
      BaseMod.ModelSpec spec = this.getModelSpec(state);
      if (spec != null) {
         BaseModClient.IModel model = this.mod.getModel(spec.modelName);
         Trans3 t = this.localToGlobalTransformation(world, pos, state, Vector3.blockCenter).translate(spec.origin);
         return t.t(model.getBounds());
      } else {
         return null;
      }
   }

   public void setBlockBounds(AxisAlignedBB box) {
      this.setBlockBounds((float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
   }

   public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB clip, List result, Entity entity) {
      BlockPos pos = new BlockPos(x, y, z);
      IBlockState state = BaseBlockUtils.getWorldBlockState(world, pos);
      this.addCollisionBoxesToList(world, pos, state, clip, result, entity);
   }

   public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB clip, List result, Entity entity) {
      List<AxisAlignedBB> list = this.getGlobalCollisionBoxes(world, pos, state, entity);
      if (list != null) {
         for(AxisAlignedBB box : list) {
            if (clip.intersectsWith(box)) {
               result.add(box);
            } else {
               super.addCollisionBoxesToList(world, pos.x, pos.y, pos.z, clip, result, entity);
            }
         }
      }

   }

   protected List<AxisAlignedBB> getGlobalCollisionBoxes(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity) {
      Trans3 t = this.localToGlobalTransformation(world, pos, state);
      return this.getCollisionBoxes(world, pos, state, t, entity);
   }

   protected List<AxisAlignedBB> getLocalCollisionBoxes(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity) {
      Trans3 t = this.localToGlobalTransformation(world, pos, state, Vector3.zero);
      return this.getCollisionBoxes(world, pos, state, t, entity);
   }

   protected List<AxisAlignedBB> getCollisionBoxes(IBlockAccess world, BlockPos pos, IBlockState state, Trans3 t, Entity entity) {
      List<AxisAlignedBB> list = new ArrayList();
      BaseMod.ModelSpec spec = this.getModelSpec(state);
      if (spec != null) {
         BaseModClient.IModel model = this.mod.getModel(spec.modelName);
         model.addBoxesToList(t.translate(spec.origin), list);
      } else {
         list.add(t.t(defaultCollisionBox));
      }

      return list;
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return this.getPickBlock(target, world, new BlockPos(x, y, z));
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
      return super.getPickBlock(target, world, pos.x, pos.y, pos.z);
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
      return this.getPlayerRelativeBlockHardness(player, world, new BlockPos(x, y, z));
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
      return super.getPlayerRelativeBlockHardness(player, world, pos.x, pos.y, pos.z);
   }

   public float getBlockHardness(World world, int x, int y, int z) {
      return this.getBlockHardness(world, new BlockPos(x, y, z));
   }

   public float getBlockHardness(World world, BlockPos pos) {
      return super.getBlockHardness(world, pos.x, pos.y, pos.z);
   }

   @SideOnly(Side.CLIENT)
   public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer er) {
      BlockPos pos = new BlockPos(target.blockX, target.blockY, target.blockZ);
      IBlockState state = this.getParticleState(world, pos);
      Block block = state.getBlock();
      int meta = BaseBlockUtils.getMetaFromBlockState(state);
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      float f = 0.1F;
      double d0 = (double)i + RANDOM.nextDouble() * (this.getBlockBoundsMaxX() - this.getBlockBoundsMinX() - (double)(f * 2.0F)) + (double)f + this.getBlockBoundsMinX();
      double d1 = (double)j + RANDOM.nextDouble() * (this.getBlockBoundsMaxY() - this.getBlockBoundsMinY() - (double)(f * 2.0F)) + (double)f + this.getBlockBoundsMinY();
      double d2 = (double)k + RANDOM.nextDouble() * (this.getBlockBoundsMaxZ() - this.getBlockBoundsMinZ() - (double)(f * 2.0F)) + (double)f + this.getBlockBoundsMinZ();
      switch (target.sideHit) {
         case 0:
            d1 = (double)j + this.getBlockBoundsMinY() - (double)f;
            break;
         case 1:
            d1 = (double)j + this.getBlockBoundsMaxY() + (double)f;
            break;
         case 2:
            d2 = (double)k + this.getBlockBoundsMinZ() - (double)f;
            break;
         case 3:
            d2 = (double)k + this.getBlockBoundsMaxZ() + (double)f;
            break;
         case 4:
            d0 = (double)i + this.getBlockBoundsMinX() - (double)f;
            break;
         case 5:
            d0 = (double)i + this.getBlockBoundsMaxX() + (double)f;
      }

      EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, (double)0.0F, (double)0.0F, (double)0.0F, block, meta);
      er.addEffect(fx.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer er) {
      BlockPos pos = new BlockPos(x, y, z);
      IBlockState state = this.getParticleState(world, pos);
      Block block = state.getBlock();
      meta = BaseBlockUtils.getMetaFromBlockState(state);
      byte b0 = 4;

      for(int i = 0; i < b0; ++i) {
         for(int j = 0; j < b0; ++j) {
            for(int k = 0; k < b0; ++k) {
               double d0 = (double)pos.getX() + ((double)i + (double)0.5F) / (double)b0;
               double d1 = (double)pos.getY() + ((double)j + (double)0.5F) / (double)b0;
               double d2 = (double)pos.getZ() + ((double)k + (double)0.5F) / (double)b0;
               EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, d0 - (double)pos.getX() - (double)0.5F, d1 - (double)pos.getY() - (double)0.5F, d2 - (double)pos.getZ() - (double)0.5F, block, meta);
               er.addEffect(fx);
            }
         }
      }

      return true;
   }

   public IBlockState getParticleState(IBlockAccess world, BlockPos pos) {
      return BaseBlockUtils.getWorldBlockState(world, pos);
   }

   public int getRenderBlockPass() {
      return this.canRenderInLayer(EnumWorldBlockLayer.TRANSLUCENT) ? 1 : 0;
   }

   public boolean canRenderInPass(int pass) {
      for(EnumWorldBlockLayer layer : BaseModClient.passLayers[pass + 1]) {
         if (this.canRenderInLayer(layer)) {
            return true;
         }
      }

      return false;
   }

   public static class Orient1Way implements IOrientationHandler {
      public void defineProperties(BaseBlock block) {
      }

      public IBlockState onBlockPlaced(Block block, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState baseState, EntityLivingBase placer) {
         return baseState;
      }

      public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state, Vector3 origin) {
         return new Trans3(origin);
      }
   }

   public interface IOrientationHandler {
      void defineProperties(BaseBlock var1);

      IBlockState onBlockPlaced(Block var1, World var2, BlockPos var3, EnumFacing var4, float var5, float var6, float var7, IBlockState var8, EntityLivingBase var9);

      Trans3 localToGlobalTransformation(IBlockAccess var1, BlockPos var2, IBlockState var3, Vector3 var4);
   }
}
