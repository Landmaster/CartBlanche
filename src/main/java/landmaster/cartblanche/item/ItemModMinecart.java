package landmaster.cartblanche.item;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import javax.annotation.*;

import landmaster.cartblanche.config.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.advancements.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.dispenser.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;

public class ItemModMinecart extends ItemMinecart {
	public static final int BEACON_CART_MAX_BLOCKS = 3*3+5*5+7*7+9*9;
	
	public static enum Type {
		ENDER_CHEST((IReducedMinecartFactory)EntityEnderChestCart::new, () -> Config.ender_chest_cart),
		JUKEBOX((IReducedMinecartFactory)EntityJukeboxCart::new, () -> Config.jukebox_cart),
		BEACON((worldIn, x, y, z, stack) -> new EntityBeaconCart(worldIn,x,y,z)
				.setBeaconMaterials(
						StreamSupport.stream(stack.getTagCompound().getTagList("BeaconMaterials", 10).spliterator(), false)
						.map(nbt -> (NBTTagCompound)nbt)
						.map(ItemStack::new)
						.collect(Collectors.toList())
						), () -> Config.beacon_cart);
		
		public final IMinecartFactory factory;
		public final BooleanSupplier config;
		
		Type(IMinecartFactory factory, BooleanSupplier config) {
			this.factory = factory;
			this.config = config;
		}
	}
	
	private static final IBehaviorDispenseItem MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
		private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
		
		/**
		 * Dispense the specified stack, play the dispense sound and spawn
		 * particles.
		 */
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing enumfacing = (EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING);
			World world = source.getWorld();
			double d0 = source.getX() + (double) enumfacing.getXOffset() * 1.125D;
			double d1 = Math.floor(source.getY()) + (double) enumfacing.getYOffset();
			double d2 = source.getZ() + (double) enumfacing.getZOffset() * 1.125D;
			BlockPos blockpos = source.getBlockPos().offset(enumfacing);
			IBlockState iblockstate = world.getBlockState(blockpos);
			BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate
					.getBlock() instanceof BlockRailBase
							? ((BlockRailBase) iblockstate.getBlock()).getRailDirection(world, blockpos, iblockstate,
									null)
							: BlockRailBase.EnumRailDirection.NORTH_SOUTH;
			double d3;
			
			if (BlockRailBase.isRailBlock(iblockstate)) {
				if (blockrailbase$enumraildirection.isAscending()) {
					d3 = 0.6D;
				} else {
					d3 = 0.1D;
				}
			} else {
				if (iblockstate.getMaterial() != Material.AIR
						|| !BlockRailBase.isRailBlock(world.getBlockState(blockpos.down()))) {
					return this.behaviourDefaultDispenseItem.dispense(source, stack);
				}
				
				IBlockState iblockstate1 = world.getBlockState(blockpos.down());
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection1 = iblockstate1
						.getBlock() instanceof BlockRailBase
								? ((BlockRailBase) iblockstate1.getBlock()).getRailDirection(world, blockpos.down(),
										iblockstate1, null)
								: BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				
				if (enumfacing != EnumFacing.DOWN && blockrailbase$enumraildirection1.isAscending()) {
					d3 = -0.4D;
				} else {
					d3 = -0.9D;
				}
			}
			
			EntityMinecart entityminecart = ((ItemModMinecart) stack.getItem()).factory.create(world, d0, d1 + d3, d2,
					stack);
			
			if (stack.hasDisplayName()) {
				entityminecart.setCustomNameTag(stack.getDisplayName());
			}
			
			world.spawnEntity(entityminecart);
			
			// START trigger
			if (entityminecart instanceof EntityBeaconCart) {
				double i = entityminecart.posX;
				double j = entityminecart.posY;
				double k = entityminecart.posZ;
				
				if (!world.isRemote && 0 < ((EntityBeaconCart)entityminecart).getLevels()) {
					TileEntityBeacon dummy = new TileEntityBeacon();
					dummy.setWorld(world);
					dummy.setPos(entityminecart.getPosition());
					dummy.setField(0, ((EntityBeaconCart)entityminecart).getLevels());
					
					for (EntityPlayerMP entityplayermp : world.getEntitiesWithinAABB(EntityPlayerMP.class,
							(new AxisAlignedBB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k))
									.grow(10.0D, 5.0D, 10.0D))) {
						CriteriaTriggers.CONSTRUCT_BEACON.trigger(entityplayermp, dummy);
					}
				}
			}
			// END trigger
			
			stack.shrink(1);
			return stack;
		}
		
		/**
		 * Play the dispense sound from the specified block.
		 */
		protected void playDispenseSound(IBlockSource source) {
			source.getWorld().playEvent(1000, source.getBlockPos(), 0);
		}
	};
	
	public int getBeaconLevels(ItemStack stack) {
		int sz = getNumBlocks(stack);
		
		int levels = -1;
		int len = 0;
		do {
			++levels;
			len += (2 * levels + 3) * (2 * levels + 3);
		} while (len <= sz);
		return levels;
	}
	
	public int getNumBlocks(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getTagList("BeaconMaterials", 10).tagCount() : 0;
	}
	
	public int getNumBlocksFromLevel(int level) {
		int len = 0;
		for (int i=0; i<level; ++i) {
			len += (2*i+3)*(2*i+3);
		}
		return len;
	}
	
	public List<ItemStack> getBeaconMaterials(ItemStack stack) {
		if (!stack.hasTagCompound()) return Collections.emptyList();
		return StreamSupport.stream(stack.getTagCompound().getTagList("BeaconMaterials", 10).spliterator(), false)
		.map(nbt -> (NBTTagCompound)nbt)
		.map(ItemStack::new)
		.collect(Collectors.toList());
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if (stack.getMetadata() == Type.BEACON.ordinal()) {
			int levels = this.getBeaconLevels(stack), numBlocks = getNumBlocks(stack);
			tooltip.add(TextFormatting.BLUE+I18n.format("tooltip.beacon_cart_num_blocks.name", numBlocks));
			tooltip.add(TextFormatting.AQUA+I18n.format("tooltip.beacon_cart_level.name", levels));
			if (levels < 4) {
				tooltip.add(TextFormatting.GREEN+I18n.format("tooltip.beacon_cart_blocks_until.name", getNumBlocksFromLevel(levels+1) - numBlocks));
				tooltip.add(TextFormatting.RED+I18n.format("tooltip.beacon_cart_instructions.name"));
			}
		}
	}
	
	@FunctionalInterface
	public static interface IReducedMinecartFactory extends IMinecartFactory {
		default EntityMinecart create(World worldIn, double x, double y, double z, ItemStack stack) {
			return this.create(worldIn, x, y, z);
		}
		public EntityMinecart create(World worldIn, double x, double y, double z);
	}
	
	@FunctionalInterface
	public static interface IMinecartFactory {
		public EntityMinecart create(World worldIn, double x, double y, double z, ItemStack stack);
	}
	
	private IMinecartFactory factory;
	
	public ItemModMinecart() {
		this((worldIn, x, y, z, stack) -> Type.values()[stack.getMetadata()].factory.create(worldIn, x, y, z, stack));
	}
	
	public ItemModMinecart(IMinecartFactory factory) {
		super(null);
		this.factory = factory;
		this.setHasSubtypes(true);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, MINECART_DISPENSER_BEHAVIOR);
		this.setTranslationKey("mod_minecart").setRegistryName("mod_minecart");
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			for (Type type : Type.values()) {
				if (type.config.getAsBoolean()) {
					if (type == Type.BEACON) {
						ItemStack stack = new ItemStack(this, 1, type.ordinal());
						int numBlocks = 0;
						for (int lev=0; lev<=4; ++lev) {
							NBTTagCompound compound = new NBTTagCompound();
							compound.setTag("BeaconMaterials", Collections.nCopies(numBlocks, Blocks.IRON_BLOCK).stream()
									.map(ItemStack::new)
									.map(ItemStack::serializeNBT)
									.collect(NBTTagList::new, NBTTagList::appendTag, (list0, list1) -> list1.forEach(list0::appendTag)));
							stack.setTagCompound(compound);
							subItems.add(stack.copy());
							numBlocks += (2*lev+3)*(2*lev+3);
						}
					} else {
						subItems.add(new ItemStack(this, 1, type.ordinal()));
					}
				}
			}
		}
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "."
				+ Type.values()[stack.getMetadata()].toString().toLowerCase(java.util.Locale.US);
	}
	
	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		
		if (!BlockRailBase.isRailBlock(iblockstate)) {
			return EnumActionResult.FAIL;
		} else {
			ItemStack itemstack = player.getHeldItem(hand);
			
			if (!worldIn.isRemote) {
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate
						.getBlock() instanceof BlockRailBase
								? ((BlockRailBase) iblockstate.getBlock()).getRailDirection(worldIn, pos, iblockstate,
										null)
								: BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				double d0 = 0.0D;
				
				if (blockrailbase$enumraildirection.isAscending()) {
					d0 = 0.5D;
				}
				
				EntityMinecart entityminecart = factory.create(worldIn, (double) pos.getX() + 0.5D,
						(double) pos.getY() + 0.0625D + d0, (double) pos.getZ() + 0.5D, itemstack);
				
				if (itemstack.hasDisplayName()) {
					entityminecart.setCustomNameTag(itemstack.getDisplayName());
				}
				
				worldIn.spawnEntity(entityminecart);
				
				// START trigger
				if (entityminecart instanceof EntityBeaconCart) {
					double i = entityminecart.posX;
					double j = entityminecart.posY;
					double k = entityminecart.posZ;
					
					if (!worldIn.isRemote && 0 < ((EntityBeaconCart)entityminecart).getLevels()) {
						TileEntityBeacon dummy = new TileEntityBeacon();
						dummy.setWorld(worldIn);
						dummy.setPos(entityminecart.getPosition());
						dummy.setField(0, ((EntityBeaconCart)entityminecart).getLevels());
						
						for (EntityPlayerMP entityplayermp : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class,
								(new AxisAlignedBB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k))
										.grow(10.0D, 5.0D, 10.0D))) {
							CriteriaTriggers.CONSTRUCT_BEACON.trigger(entityplayermp, dummy);
						}
					}
				}
				// END trigger
			}
			
			itemstack.shrink(1);
			return EnumActionResult.SUCCESS;
		}
	}
}
