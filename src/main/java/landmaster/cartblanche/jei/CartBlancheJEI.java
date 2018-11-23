package landmaster.cartblanche.jei;

import landmaster.cartblanche.*;
import landmaster.cartblanche.util.*;
import mezz.jei.api.*;
import mezz.jei.api.recipe.*;

@JEIPlugin
public class CartBlancheJEI implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		CartBlanche.log.debug("Adding JEI integration for Cart Blanche");
		
		registry.handleRecipes(BeaconCartLevelRecipe.class, BeaconCartLevelRecipeJEI::new, VanillaRecipeCategoryUid.CRAFTING);
	}
}
