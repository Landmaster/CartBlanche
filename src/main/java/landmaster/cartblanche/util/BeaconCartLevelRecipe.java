package landmaster.cartblanche.util;

import java.util.*;

import landmaster.cartblanche.item.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.registries.IForgeRegistryEntry.*;

public class BeaconCartLevelRecipe extends Impl<IRecipe> implements IRecipe {
	private int number;
	
	public BeaconCartLevelRecipe(int number) {
		this.number = number;
		this.setRegistryName("beacon_cart_level_"+number);
	}
	
	public int getNumber() { return number; }
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return !this.getCraftingResult(inv).isEmpty();
	}
	
	@SuppressWarnings("deprecation")
	private static IBlockState blockFromStack(ItemStack stack) {
		try {
			return Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getMetadata());
		} catch (Throwable e) {
			return null;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack beaconCart = ItemStack.EMPTY;
		List<ItemStack> blocks = new ArrayList<>();
		for (int i=0; i<inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			} else if (ItemStack.areItemsEqual(stack, new ItemStack(ModItems.mod_minecart, 1, ItemModMinecart.Type.BEACON.ordinal()))) {
				if (!beaconCart.isEmpty()) {
					return ItemStack.EMPTY;
				}
				beaconCart = stack;
			} else if (stack.getItem() instanceof ItemBlock
					&& Block.getBlockFromItem(stack.getItem())
					.isBeaconBase(
							new FakeOneBlockAccess(blockFromStack(stack)),
							BlockPos.ORIGIN, BlockPos.ORIGIN)) {
				ItemStack oneItemStack = stack.copy();
				oneItemStack.setCount(1);
				blocks.add(oneItemStack);
			} else {
				return ItemStack.EMPTY;
			}
		}
		if (beaconCart.isEmpty()) return ItemStack.EMPTY;
		int curBlocks = ModItems.mod_minecart.getNumBlocks(beaconCart);
		if (blocks.size() == number && curBlocks+blocks.size() <= ItemModMinecart.BEACON_CART_MAX_BLOCKS) {
			ItemStack res = beaconCart.copy();
			if (!res.hasTagCompound()) res.setTagCompound(new NBTTagCompound());
			NBTTagList list = res.getTagCompound().getTagList("BeaconMaterials", 10);
			blocks.stream()
			.map(ItemStack::serializeNBT)
			.forEach(list::appendTag);
			return res;
		} else {
			return ItemStack.EMPTY;
		}
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width*height >= (number+1);
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
}
