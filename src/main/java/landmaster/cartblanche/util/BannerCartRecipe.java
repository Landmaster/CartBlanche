package landmaster.cartblanche.util;

import java.util.Optional;

import landmaster.cartblanche.item.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraftforge.registries.IForgeRegistryEntry.*;

public class BannerCartRecipe extends Impl<IRecipe> implements IRecipe {
	public BannerCartRecipe() {
		this.setRegistryName("banner_cart");
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return !this.getCraftingResult(inv).isEmpty();
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack oldCart = ItemStack.EMPTY, banner = ItemStack.EMPTY;
		for (int i=0; i<inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			} else if (stack.getItem() == Items.MINECART) {
				if (!oldCart.isEmpty()) {
					return ItemStack.EMPTY;
				}
				oldCart = stack;
			} else if (stack.getItem() == Items.BANNER) {
				if (!banner.isEmpty()) {
					return ItemStack.EMPTY;
				}
				banner = stack;
			} else {
				return ItemStack.EMPTY;
			}
		}
		
		if (!oldCart.isEmpty() && !banner.isEmpty()) {
			ItemStack result = new ItemStack(ModItems.mod_minecart, 1, ItemModMinecart.Type.BANNER.ordinal());
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("Base", banner.getMetadata());
			compound.merge(Optional.ofNullable(banner.getSubCompound("BlockEntityTag")).orElseGet(NBTTagCompound::new));
			result.setTagCompound(compound);
			return result;
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width*height >= 2;
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
