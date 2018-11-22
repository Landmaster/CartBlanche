package landmaster.cartblanche.item;

import java.util.*;
import java.util.function.*;

import landmaster.cartblanche.config.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.dispenser.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class ItemModMinecart extends ItemMinecart {
	public static enum Type {
		ENDER_CHEST(EntityEnderChestCart::new, () -> Config.ender_chest_cart),
		JUKEBOX(EntityJukeboxCart::new, () -> Config.jukebox_cart);
		
		public final IIndividualMinecartFactory factory;
		public final BooleanSupplier config;
		
		Type(IIndividualMinecartFactory factory, BooleanSupplier config) {
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
	
	@FunctionalInterface
	public static interface IIndividualMinecartFactory {
		public EntityMinecart create(World worldIn, double x, double y, double z);
	}
	
	@FunctionalInterface
	public static interface IMinecartFactory {
		public EntityMinecart create(World worldIn, double x, double y, double z, ItemStack stack);
	}
	
	private IMinecartFactory factory;
	
	public ItemModMinecart() {
		this((worldIn, x, y, z, stack) -> Type.values()[stack.getMetadata()].factory.create(worldIn, x, y, z));
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
					subItems.add(new ItemStack(this, 1, type.ordinal()));
				}
			}
		}
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "."
				+ Type.values()[stack.getMetadata()].toString().toLowerCase(Locale.US);
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
			}
			
			itemstack.shrink(1);
			return EnumActionResult.SUCCESS;
		}
	}
}
