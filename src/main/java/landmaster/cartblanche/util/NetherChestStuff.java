package landmaster.cartblanche.util;

import landmaster.cartblanche.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;
import netherchest.client.gui.*;
import netherchest.common.inventory.ContainerNetherChest;

public class NetherChestStuff {
	public static Container getChestContainerForMinecart(EntityPlayer player, World world, int entityID) {
		Entity entity = world.getEntityByID(entityID);
		if (entity instanceof EntityNetherChestCart) {
			EntityNetherChestCart cart = (EntityNetherChestCart)entity;
			return cart.createContainer(player.inventory, player);
		} else {
			return null;
		}
	}
	
	public static Object getChestGuiForMinecart(EntityPlayer player, World world, int entityID) {
		Container container = getChestContainerForMinecart(player, world, entityID);
		if (container instanceof ContainerNetherChest) {
			return new GuiNetherChest((ContainerNetherChest)getChestContainerForMinecart(player, world, entityID), player.inventory);
		} else {
			return null;
		}
	}
}
