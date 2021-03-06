package landmaster.cartblanche.proxy;

import java.util.function.*;

import landmaster.cartblanche.api.*;
import landmaster.cartblanche.config.*;
import landmaster.cartblanche.entity.*;
import landmaster.cartblanche.entity.render.*;
import landmaster.cartblanche.item.ItemModMinecart;
import landmaster.cartblanche.item.ModItems;
import landmaster.cartblanche.sound.*;
import net.minecraft.client.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.fml.client.registry.*;

public class ClientProxy extends CommonProxy {
	@Override
	public void initEntityRendering() {
		if (Config.ender_chest_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityEnderChestCart.class, RenderMinecart<EntityEnderChestCart>::new);
		}
		if (Config.jukebox_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityJukeboxCart.class, RenderMinecart<EntityJukeboxCart>::new);
		}
		if (Config.beacon_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBeaconCart.class, RenderBeaconCart::new);
		}
		if (Config.iron_chest_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityIronChestCart.class, RenderIronChestCart::new);
		}
		if (Config.nether_chest_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityNetherChestCart.class, RenderNetherChestCart::new);
		}
		if (Config.banner_cart) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBannerCart.class, RenderBannerCart::new);
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
	
	@Override
	public void registerItemRenderer(Item item, Function<ItemStack, ModelResourceLocation> mapper, ResourceLocation...locs) {
		ModelLoader.setCustomMeshDefinition(item, mapper::apply);
		ModelBakery.registerItemVariants(item, locs);
	}
	
	@Override
	public Object playJukeboxCartSound(SoundEvent soundIn, SoundCategory categoryIn, EntityJukeboxCart cart) {
		JukeboxCartSound sound = new JukeboxCartSound(soundIn, categoryIn, cart);
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		return sound;
	}
	
	@Override
	public boolean isSoundPlaying(Object sound) {
		if (sound instanceof ISound) {
			return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying((ISound)sound);
		}
		return false;
	}
	
	@Override
	public void stopSound(Object sound) {
		if (sound instanceof ISound) {
			//System.out.println("SOUND STOPPED! "+sound);
			Minecraft.getMinecraft().getSoundHandler().stopSound((ISound)sound);
		}
	};
	
	@Override
	public void registerColors() {
		if (Config.banner_cart) {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, idx) -> {
				if (stack.getMetadata() == ItemModMinecart.Type.BANNER.ordinal()) {
					if (idx == 1) {
						return ModItems.mod_minecart.getBannerBaseColor(stack).getColorValue();
					}
				}
				return 0xFFFFFF;
			}, ModItems.mod_minecart);
		}
	}
}
