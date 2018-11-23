package landmaster.cartblanche;

import java.util.*;

import org.apache.logging.log4j.*;

import landmaster.cartblanche.proxy.*;
import landmaster.cartblanche.util.*;
import landmaster.cartblanche.api.*;
import landmaster.cartblanche.config.*;
import landmaster.cartblanche.entity.*;
import landmaster.cartblanche.item.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;
import net.minecraftforge.event.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.common.Mod.*;

@Mod.EventBusSubscriber
@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDS, useMetadata = true, acceptedMinecraftVersions = "[1.12, 1.13)")
public class CartBlanche {
	public static Config config;
	
	@Instance(ModInfo.MODID)
	public static CartBlanche INSTANCE;
	
	@SidedProxy(serverSide = "landmaster.cartblanche.proxy.CommonProxy", clientSide = "landmaster.cartblanche.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public static final Logger log = LogManager.getLogger(
			ModInfo.MODID.toUpperCase(Locale.US/* to avoid problems with Turkish */));
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		(config = new Config(event)).init();
		config.update();
		
		if (Config.ender_chest_cart) {
			EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "ender_chest_cart"), EntityEnderChestCart.class, "EnderChestCart", 0, INSTANCE, 256, 2, true);
		}
		if (Config.jukebox_cart) {
			EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "jukebox_cart"), EntityJukeboxCart.class, "JukeboxCart", 1, INSTANCE, 256, 2, true);
		}
		if (Config.beacon_cart) {
			EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "beacon_cart"), EntityBeaconCart.class, "BeaconCart", 2, INSTANCE, 256, 2, true);
		}
		proxy.initEntityRendering();
		
		//CraftingHelper.register(new ResourceLocation(ModInfo.MODID, "minecart_enabled"), new MinecartEnabled());
		//System.out.println(new ResourceLocation(ModInfo.MODID, "minecart_enabled"));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(ModItems.mod_minecart);
		for (ItemModMinecart.Type type: ItemModMinecart.Type.values()) {
			if (type.config.getAsBoolean()) {
				proxy.registerItemRenderer(ModItems.mod_minecart, type.ordinal(), "mod_minecart_"+type.toString().toLowerCase(Locale.US));
			}
		}
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		if (Config.beacon_cart) {
			for (int i=1; i<=8; ++i) {
				event.getRegistry().register(new BeaconCartLevelRecipe(i));
			}
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
