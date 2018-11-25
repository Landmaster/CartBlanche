package landmaster.cartblanche.entity;

import java.lang.invoke.*;

import com.google.common.base.*;

import landmaster.cartblanche.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;

public class EntityEnderChestCart extends EntityMinecartContainer {
	public EntityEnderChestCart(World worldIn) {
		super(worldIn);
	}
	
	public EntityEnderChestCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}
	
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItemWithOffset(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1, 0.0F);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 0; // unused
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerChest(playerInventory, playerIn.getInventoryEnderChest(), playerIn);
	}
	
	@Override
	public String getGuiID() {
		return "minecraft:chest";
	}
	
	@Override
	public Type getType() {
		return null; // not used
	}
	
	@Override
	public IBlockState getDefaultDisplayTile() {
		return Blocks.ENDER_CHEST.getDefaultState().withProperty(BlockEnderChest.FACING, EnumFacing.NORTH);
	}
	
	@Override
	public int getDefaultDisplayTileOffset() {
		return 8;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return null; // no item handler capability
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && super.hasCapability(capability, facing);
	}
	
	private static final MethodHandle processInitialInteractHandle;
	static {
		try {
			processInitialInteractHandle = Utils.IMPL_LOOKUP.findSpecial(EntityMinecart.class, "func_184230_a",
					MethodType.methodType(boolean.class, EntityPlayer.class, EnumHand.class),
					EntityMinecartContainer.class);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		try {
			if ((boolean) processInitialInteractHandle.invokeExact((EntityMinecartContainer)this, player, hand)) {
				return true;
			}
			if (!this.world.isRemote) {
				player.displayGUIChest(new InventoryInteractionObject(player.getInventoryEnderChest(), this));
			}
			return true;
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
}
