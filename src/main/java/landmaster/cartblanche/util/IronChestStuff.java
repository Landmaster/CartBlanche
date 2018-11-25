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
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;

public class IronChestStuff {
	static {
		MinecraftForge.EVENT_BUS.register(IronChestStuff.class);
	}
	
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getWorld().isRemote) {
			return;
		}
		
		if (event.getItemStack().getItem() instanceof ItemChestChanger) {
			boolean worked = false;
			
			ItemChestChanger changer = (ItemChestChanger)event.getItemStack().getItem();
			if (changer.type.canUpgrade(IronChestType.WOOD) && event.getTarget() instanceof EntityMinecartChest) {
				EntityMinecartChest oldCart = (EntityMinecartChest)event.getTarget();
				oldCart.setDropItemsWhenDead(false);
				
				EntityIronChestCart newCart = new EntityIronChestCart(oldCart.world);
				
				NBTTagCompound compound = new NBTTagCompound();
				oldCart.writeToNBT(compound);
				compound.setInteger("IronChestType", changer.type.target.ordinal());
				newCart.readFromNBT(compound);
				
				event.getWorld().removeEntity(oldCart);
				MinecraftForge.EVENT_BUS.register(new Object() {
					@SubscribeEvent
					public void onWorldTick(TickEvent.WorldTickEvent event0) {
						if (event.getWorld() instanceof WorldServer
								&& ((WorldServer)event.getWorld()).getEntityFromUuid(oldCart.getUniqueID()) == null) {
							event.getWorld().spawnEntity(newCart);
							MinecraftForge.EVENT_BUS.unregister(this);
						}
					}
				});
				worked = true;
			} else if (event.getTarget() instanceof EntityIronChestCart) {
				EntityIronChestCart cart = (EntityIronChestCart)event.getTarget();
				if (changer.type.canUpgrade(cart.getChestType())) {
					cart.setChestType(changer.type.target);
					worked = true;
				}
			}
			
			if (worked) {
				if (!event.getEntityPlayer().capabilities.isCreativeMode) {
					event.getItemStack().shrink(1);
				}
				
				event.setCancellationResult(EnumActionResult.SUCCESS);
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
