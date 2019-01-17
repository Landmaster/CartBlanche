package landmaster.cartblanche.util;

import java.lang.invoke.*;
import java.util.*;

import com.google.common.base.*;

import cpw.mods.ironchest.client.gui.chest.*;
import cpw.mods.ironchest.common.blocks.chest.*;
import cpw.mods.ironchest.common.items.chest.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.minecart.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.relauncher.Side;

public class IronChestStuff {
	static {
		MinecraftForge.EVENT_BUS.register(IronChestStuff.class);
	}
	
	@SubscribeEvent
	public static void onEntityInteract(MinecartInteractEvent event) {
		if (event.getMinecart().world.isRemote) {
			return;
		}
		
		if (event.getItem().getItem() instanceof ItemChestChanger) {
			boolean worked = false;
			
			ItemChestChanger changer = (ItemChestChanger)event.getItem().getItem();
			if (changer.type.canUpgrade(IronChestType.WOOD) && event.getMinecart() instanceof EntityMinecartChest) {
				EntityMinecartChest oldCart = (EntityMinecartChest)event.getMinecart();
				oldCart.setDropItemsWhenDead(false);
				
				EntityIronChestCart newCart = new EntityIronChestCart(oldCart.world);
				
				NBTTagCompound compound = new NBTTagCompound();
				oldCart.writeToNBT(compound);
				compound.setInteger("IronChestType", changer.type.target.ordinal());
				newCart.readFromNBT(compound);
				
				oldCart.world.removeEntity(oldCart);
				MinecraftForge.EVENT_BUS.register(new Object() {
					@SubscribeEvent
					public void onServerTick(TickEvent.ServerTickEvent event0) {
						if (oldCart.world instanceof WorldServer
								&& ((WorldServer)oldCart.world).getEntityFromUuid(oldCart.getUniqueID()) == null) {
							oldCart.world.spawnEntity(newCart);
							MinecraftForge.EVENT_BUS.unregister(this);
						}
					}
				});
				worked = true;
			} else if (event.getMinecart() instanceof EntityIronChestCart) {
				EntityIronChestCart cart = (EntityIronChestCart)event.getMinecart();
				if (changer.type.canUpgrade(cart.getChestType())) {
					cart.setChestType(changer.type.target);
					worked = true;
				}
			}
			
			if (worked) {
				if (!event.getPlayer().capabilities.isCreativeMode) {
					event.getItem().shrink(1);
				}
				
				event.setCanceled(true);
			}
		}
	}
	
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
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				guiChestConstructorHandle = Utils.IMPL_LOOKUP.findConstructor(GUIChest.class, MethodType.methodType(void.class, GUIChest.GUI.class, IInventory.class, IInventory.class));
			} else {
				guiChestConstructorHandle = null;
			}
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
