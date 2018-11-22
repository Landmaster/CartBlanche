package landmaster.cartblanche.config;

import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.common.event.*;

public class Config extends Configuration {
	public static boolean ender_chest_cart;
	public static boolean jukebox_cart;
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
	}
	
	public void init() {
		ender_chest_cart = this.getBoolean("ender_chest_cart", "carts", true, "Enable the Ender Chest Cart");
		jukebox_cart = this.getBoolean("jukebox_cart", "carts", true, "Enable the Jukebox Cart");
	}
	
	public void update() {
		if (hasChanged()) save();
	}
}
