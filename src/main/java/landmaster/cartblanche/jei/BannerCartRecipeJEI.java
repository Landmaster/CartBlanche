package landmaster.cartblanche.jei;

import java.util.*;

import javax.annotation.*;

import landmaster.cartblanche.item.*;
import landmaster.cartblanche.util.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.wrapper.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class BannerCartRecipeJEI implements ICraftingRecipeWrapper {
	private BannerCartRecipe recipe;
	
	public BannerCartRecipeJEI(BannerCartRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(new ItemStack(Items.MINECART), new ItemStack(Items.BANNER)));
		ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ModItems.mod_minecart, 1, ItemModMinecart.Type.BANNER.ordinal()));
	}
	
	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getRegistryName();
	}
}
