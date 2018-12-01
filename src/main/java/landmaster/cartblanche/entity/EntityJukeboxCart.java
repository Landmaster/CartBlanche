package landmaster.cartblanche.entity;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import landmaster.cartblanche.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;

public class EntityJukeboxCart extends EntityMinecart {
	private ItemStackHandler record = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getItem() instanceof ItemRecord;
		}
		
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			return 1;
		}
	};
	
	private static final DataParameter<Boolean> DISABLED = EntityDataManager.createKey(EntityJukeboxCart.class,
			DataSerializers.BOOLEAN);
	private static final Map<Integer, Object> soundMap = new MapMaker().weakValues().makeMap();
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(DISABLED, false);
	}
	
	public EntityJukeboxCart(World worldIn) {
		super(worldIn);
	}
	
	public EntityJukeboxCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}
	
	@Override
	public Type getType() {
		return null; // unused
	}
	
	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		this.getDataManager().set(DISABLED, receivingPower);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.getDataManager().set(DISABLED, compound.getBoolean("Disabled"));
		record.deserializeNBT(compound.getCompoundTag("Record"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Disabled", this.getDataManager().get(DISABLED));
		compound.setTag("Record", record.serializeNBT());
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (super.processInitialInteract(player, hand)) {
			return true;
		}
		ItemStack stack = player.getHeldItem(hand).copy();
		if (!stack.isEmpty()) {
			if (record.isItemValid(0, stack)) {
				ItemStack newStack = record.insertItem(0, stack, false);
				player.setHeldItem(hand, newStack);
				return !ItemStack.areItemStacksEqual(stack, newStack);
			}
		} else {
			stack = record.extractItem(0, 1, false);
			player.setHeldItem(hand, stack);
			return !stack.isEmpty();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) record;
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	public boolean isDisabled() {
		return this.getDataManager().get(DISABLED);
	}
	
	public void removeSound(Object sound) {
		soundMap.remove(this.getEntityId(), sound);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		synchronized (soundMap) {
			if (this.world.isRemote) {
				ItemStack stack = this.record.getStackInSlot(0);
				if (!EntityJukeboxCart.this.getDataManager().get(DISABLED) && !stack.isEmpty()
						&& stack.getItem() instanceof ItemRecord
						&& !soundMap.containsKey(EntityJukeboxCart.this.getEntityId())) {
					Object sound = CartBlanche.proxy.playJukeboxCartSound(((ItemRecord) stack.getItem()).getSound(),
							SoundCategory.MUSIC, EntityJukeboxCart.this);
					soundMap.put(EntityJukeboxCart.this.getEntityId(), sound);
				}
			}
		}
	}
	
	@Override
	public IBlockState getDefaultDisplayTile() {
		return Blocks.JUKEBOX.getDefaultState();
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItemWithOffset(Item.getItemFromBlock(Blocks.JUKEBOX), 1, 0.0F);
			this.entityDropItem(this.record.getStackInSlot(0), 0);
		}
	}
}
