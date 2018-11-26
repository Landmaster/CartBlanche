package landmaster.cartblanche.gui;

import landmaster.cartblanche.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;

public class CBGuiHandler implements IGuiHandler {
	public static final int IRON_CHEST = 0;
	public static final int NETHER_CHEST = 1; // TODO implement nether chest GUI
	
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case IRON_CHEST:
			return IronChestStuff.getChestContainerForMinecart(player, world, x); // HACK: "x" stores the Entity ID
		case NETHER_CHEST:
			return NetherChestStuff.getChestContainerForMinecart(player, world, x);
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case IRON_CHEST:
			return IronChestStuff.getChestGuiForMinecart(player, world, x);
		case NETHER_CHEST:
			return NetherChestStuff.getChestGuiForMinecart(player, world, x);
		}
		return null;
	}
	
}
