package landmaster.cartblanche.jei;

import java.util.*;
import java.util.stream.*;

import javax.annotation.*;

import landmaster.cartblanche.item.*;
import landmaster.cartblanche.util.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.wrapper.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.registry.*;

public class BeaconCartLevelRecipeJEI implements ICraftingRecipeWrapper {
	private static List<ItemStack> validStacks = null;
	
	private BeaconCartLevelRecipe recipe;
	
	public BeaconCartLevelRecipeJEI(BeaconCartLevelRecipe recipe) {
		this.recipe = recipe;
	}
	
	private static List<ItemStack> getValidStacks() {
		if (validStacks == null) {
			validStacks = ForgeRegistries.BLOCKS.getValuesCollection().stream()
					.flatMap(block -> {
						NonNullList<ItemStack> lst = NonNullList.create();
						block.getSubBlocks(CreativeTabs.SEARCH, lst);
						return lst.stream();
					})
					.filter(stack -> stack.getItem() instanceof ItemBlock)
					.filter(stack -> {
						try {
							Block block = Block.getBlockFromItem(stack.getItem());
							@SuppressWarnings("deprecation")
							IBlockState state = block.getStateFromMeta(stack.getMetadata());
							return block.isBeaconBase(new FakeOneBlockAccess(state), BlockPos.ORIGIN, BlockPos.ORIGIN);
						} catch (Throwable e) {
							return false;
						}
					})
					.collect(Collectors.toList());
		}
		return validStacks;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ItemStack beaconCart = new ItemStack(ModItems.mod_minecart, 1, ItemModMinecart.Type.BEACON.ordinal());
		
		List<List<ItemStack>> lst = new ArrayList<>();
		lst.add(Arrays.asList(beaconCart.copy()));
		lst.addAll(Collections.nCopies(recipe.getNumber(), getValidStacks()));
		ingredients.setInputLists(VanillaTypes.ITEM, lst);
		
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("BeaconMaterials", Collections.nCopies(recipe.getNumber(), Blocks.IRON_BLOCK).stream()
				.map(ItemStack::new)
				.map(ItemStack::serializeNBT)
				.collect(NBTTagList::new, NBTTagList::appendTag, (list0, list1) -> list1.forEach(list0::appendTag)));
		beaconCart.setTagCompound(compound);
		
		ingredients.setOutput(VanillaTypes.ITEM, beaconCart.copy());
	}
	
	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getRegistryName();
	}
	
}
