package landmaster.cartblanche.util;

import java.lang.invoke.*;
import java.util.Arrays;

import com.google.common.base.*;

import cpw.mods.ironchest.client.gui.chest.*;
import cpw.mods.ironchest.common.blocks.chest.IronChestType;
import landmaster.cartblanche.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;

public class IronChestStuff {
	public static Container getChestContainerForMinecart(EntityPlayer player, World world, int entityID) {
		Entity entity = world.getEntityByID(entityID);
		if (entity instanceof EntityIronChestCart) {
			EntityIronChestCart ironChestCart = (EntityIronChestCart)entity;
			return ironChestCart.createContainer(player.inventory, player);
		} else {
			return null;
		}
	}
	
	private static final MethodHandle guiChestConstructorHandle;
	static {
		try {
			guiChestConstructorHandle = Utils.IMPL_LOOKUP.findConstructor(GUIChest.class, MethodType.methodType(void.class, GUIChest.GUI.class, IInventory.class, IInventory.class));
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	public static Object getChestGuiForMinecart(EntityPlayer player, World world, int entityID) {
		Entity entity = world.getEntityByID(entityID);
		if (entity instanceof EntityIronChestCart) {
			EntityIronChestCart ironChestCart = (EntityIronChestCart)entity;
			try {
				return (GUIChest)guiChestConstructorHandle.invokeExact(GUIChest.GUI.values()[ironChestCart.getChestType().ordinal()], (IInventory)player.inventory, (IInventory)ironChestCart);
			} catch (Throwable e) {
				Throwables.throwIfUnchecked(e);
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	
	public static int[] getIronChestTypes() {
		return Arrays.stream(IronChestType.VALUES)
		.filter(IronChestType::isValidForCreativeMode)
		.mapToInt(IronChestType::ordinal)
		.toArray();
	}
	
	public static String ironChestStringFromInt(int val) {
		return IronChestType.VALUES[val].name();
	}
}
