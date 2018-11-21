package landmaster.cartblanche.proxy;

import landmaster.cartblanche.api.*;
import landmaster.cartblanche.config.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.item.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.fml.client.registry.*;

public class ClientProxy extends CommonProxy {
	@Override
	public void initEntityRendering() {
		if (Config.ender_chest_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityEnderChestCart.class, RenderMinecart<EntityEnderChestCart>::new);
		}
	}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
	    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ModInfo.MODID + ":" + id, "inventory"));
	}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id, String variant) {
		ModelResourceLocation rl = new ModelResourceLocation(ModInfo.MODID + ":" + id, variant);
		if (meta >= 0)  {
			ModelLoader.setCustomModelResourceLocation(item, meta, rl);
		} else {
			ModelLoader.setCustomMeshDefinition(item, stack -> rl);
			ModelBakery.registerItemVariants(item, rl);
		}
	}
}
