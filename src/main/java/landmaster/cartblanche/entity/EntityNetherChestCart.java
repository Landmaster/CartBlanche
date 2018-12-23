package landmaster.cartblanche.entity;

import java.lang.invoke.*;

import javax.annotation.Nullable;

import com.google.common.base.*;

import landmaster.cartblanche.*;
import landmaster.cartblanche.gui.*;
import landmaster.cartblanche.item.*;
import landmaster.cartblanche.util.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;
import netherchest.common.*;
import netherchest.common.blocks.*;
import netherchest.common.inventory.*;
import netherchest.common.tileentity.*;

public class EntityNetherChestCart extends EntityMinecartContainer {
	private ExtendedItemStackHandler handler;
	private TileEntityNetherChest fakeTE;
	
	public static class Factory implements ItemModMinecart.IReducedMinecartFactory {
		@Override
		public EntityMinecart create(World worldIn, double x, double y, double z) {
			return new EntityNetherChestCart(worldIn,x,y,z);
		}
	}
	
	public EntityNetherChestCart(World worldIn) {
		super(worldIn);
		init();
	}
	
	public EntityNetherChestCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		init();
	}
	
	private static final MethodHandle itemHandlerHandle;
	static {
		try {
			itemHandlerHandle = Utils.IMPL_LOOKUP.findSetter(TileEntityNetherChest.class, "itemHandler", ExtendedItemStackHandler.class);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	private void init() {
		this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		this.handler = new ExtendedItemStackHandler(this.minecartContainerItems) {
			@Override
			public void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				EntityNetherChestCart.this.markDirty();
			}
			
			@Override
			public void setSize(int sz) {
				super.setSize(sz);
				EntityNetherChestCart.this.minecartContainerItems = this.stacks;
			}
		};
		this.fakeTE = new TileEntityNetherChest() {
			{
				try {
					itemHandlerHandle.invokeExact((TileEntityNetherChest)this, EntityNetherChestCart.this.handler);
				} catch (Throwable e) {
					Throwables.throwIfUnchecked(e);
					throw new RuntimeException(e);
				}
			}
			@Override
			public void openInventory(EntityPlayer player) {
				this.markDirty();
			}
			@Override
			public void closeInventory(EntityPlayer player) {
				this.markDirty();
			}
			@Override
			public void markDirty() {
				EntityNetherChestCart.this.markDirty();
			}
			@Override
			public String getName() {
				return EntityNetherChestCart.this.getName();
			}
			@Override
			public boolean hasCustomName() {
				return EntityNetherChestCart.this.hasCustomName();
			}
			@Override
			public ITextComponent getDisplayName() {
				return EntityNetherChestCart.this.getDisplayName();
			}
			@Override
			public void setCustomName(String p_190575_1_) {
				EntityNetherChestCart.this.setCustomNameTag(p_190575_1_);
			}
		};
	}
	
	@Override
	public int getInventoryStackLimit() {
		return super.getInventoryStackLimit() * 8;
	}
	
	@Override
	public int getSizeInventory() {
		return 27;
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		this.addLoot(playerIn);
		return new ContainerNetherChest(fakeTE, playerIn);
	}
	
	@Override
	public String getGuiID() {
		return null; // unused
	}
	
	@Override
	public Type getType() {
		return null; // unused
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItemWithOffset(Content.NETHER_CHEST_ITEM, 1, 0.0F);
		}
	}
	
	@Override
	public IBlockState getDefaultDisplayTile() {
		return Content.NETHER_CHEST.getDefaultState().withProperty(BlockNetherChest.FACING, EnumFacing.NORTH);
	}
	
	@Override
	public int getDefaultDisplayTileOffset() {
		return 8;
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
				if (this.world.provider.isNether() && netherchest.common.Config.NETHER_EXPLOSION) {
					this.dropContentsWhenDead = false;
					this.world.createExplosion(null, this.posX, this.posY, this.posZ, netherchest.common.Config.EXPLOSION_RADIUS, true);
					this.setDead();
				} else {
					player.openGui(CartBlanche.INSTANCE, CBGuiHandler.NETHER_CHEST, world, this.getEntityId(), 0, 0);
				}
			}
			return true;
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.fakeTE.setPos(getPosition());
	}
	
	private static final MethodHandle setDeadHandle;
	static {
		try {
			setDeadHandle = Utils.IMPL_LOOKUP.findSpecial(EntityMinecart.class, "func_70106_y",
					MethodType.methodType(void.class), EntityMinecartContainer.class);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setDead() {
		try {
			setDeadHandle.invokeExact((EntityMinecartContainer) this);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
		if (!world.isRemote && this.dropContentsWhenDead) {
			InventoryHelper.dropInventoryItems(this.world, this, this);
		}
		// System.out.println("SZ: "+this.getSizeInventory());
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.merge(handler.serializeNBT());
		//System.out.println(compound);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		handler.deserializeNBT(compound);
		/*
		for (int i=0; i<this.minecartContainerItems.size(); ++i) {
			System.out.println(i+", "+this.minecartContainerItems.get(i).getCount()+", "+handler.getStackInSlot(i).getCount());
		}*/
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T)handler;
		}
		return super.getCapability(capability, facing);
	}
}
