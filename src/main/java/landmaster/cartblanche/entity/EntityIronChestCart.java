package landmaster.cartblanche.entity;

import java.lang.invoke.*;
import java.util.*;

import com.google.common.base.*;

import cpw.mods.ironchest.common.blocks.chest.*;
import cpw.mods.ironchest.common.core.*;
import cpw.mods.ironchest.common.gui.chest.*;
import landmaster.cartblanche.*;
import landmaster.cartblanche.gui.*;
import landmaster.cartblanche.net.*;
import landmaster.cartblanche.util.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;

public class EntityIronChestCart extends EntityMinecartContainer {
	private static final DataParameter<Integer> IRON_CHEST_TYPE = EntityDataManager.createKey(EntityJukeboxCart.class,
			DataSerializers.VARINT);
	
	private static final MethodHandle minecartContainerItemsHandle;
	static {
		try {
			minecartContainerItemsHandle = Utils.IMPL_LOOKUP.findSetter(EntityMinecartContainer.class, "field_94113_a",
					NonNullList.class);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	private static final int IRON_CHEST_TYPE_UNINIT = (int) Byte.MAX_VALUE;
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(IRON_CHEST_TYPE, IRON_CHEST_TYPE_UNINIT);
	}
	
	private boolean invTouched;
	
	private List<ItemStack> topStacks;
	
	public EntityIronChestCart(World worldIn) {
		super(worldIn);
		upsizeInv();
		topStacks = NonNullList.withSize(8, ItemStack.EMPTY);
	}
	
	public EntityIronChestCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		upsizeInv();
		topStacks = NonNullList.withSize(8, ItemStack.EMPTY);
	}
	
	private static final int MAX_IRON_CHEST_SIZE = Arrays.stream(IronChestStuff.getIronChestTypes())
			.mapToObj(val -> IronChestType.VALUES[val]).mapToInt(ch -> ch.size).max().orElse(0);
	
	private void upsizeInv() {
		try {
			minecartContainerItemsHandle.invokeExact((EntityMinecartContainer) this,
					NonNullList.withSize(MAX_IRON_CHEST_SIZE, ItemStack.EMPTY));
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
    public ItemStack getStackInSlot(int index) {
		this.invTouched = true;
		return super.getStackInSlot(index);
	}
	
	public EntityIronChestCart setChestType(int type) {
		this.getDataManager().set(IRON_CHEST_TYPE, type);
		return this;
	}
	
	public EntityIronChestCart setChestType(IronChestType type) {
		return this.setChestType(type.ordinal());
	}
	
	public IronChestType getChestType() {
		int val = this.getDataManager().get(IRON_CHEST_TYPE);
		if (val == IRON_CHEST_TYPE_UNINIT) {
			return null;
		}
		return IronChestType.VALUES[val];
	}
	
	@Override
	public int getSizeInventory() {
		IronChestType type = getChestType();
		if (type == null) {
			return MAX_IRON_CHEST_SIZE;
		}
		return this.getChestType().size;
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		this.addLoot(playerIn);
		IronChestType type = getChestType();
		return new ContainerIronChest(playerInventory, this, type, type.xSize, type.ySize);
	}
	
	@Override
	public String getGuiID() {
		return null; // also unused
	}
	
	@Override
	@Deprecated
	public Type getType() {
		return null; // unused
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		// System.out.println("SZ: "+this.getSizeInventory());
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.entityDropItem(new ItemStack(IronChestBlocks.ironChestBlock, 1, this.getChestType().ordinal()), 0.0F);
		}
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
	public IBlockState getDefaultDisplayTile() {
		return IronChestBlocks.ironChestBlock.getDefaultState().withProperty(BlockIronChest.VARIANT_PROP,
				this.getChestType());
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
			if ((boolean) processInitialInteractHandle.invokeExact((EntityMinecartContainer) this, player, hand)) {
				return true;
			}
			if (!this.world.isRemote) {
				player.openGui(CartBlanche.INSTANCE, CBGuiHandler.IRON_CHEST, world, this.getEntityId(), 0, 0);
			}
			return true;
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.getDataManager().set(IRON_CHEST_TYPE, compound.getInteger("IronChestType"));
		this.sortTopStacks();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("IronChestType", this.getDataManager().get(IRON_CHEST_TYPE));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getName() {
		if (this.hasCustomName()) {
			return this.getCustomNameTag();
		} else {
			String s = EntityList.getEntityString(this);
			
			if (s == null) {
				s = "generic";
			}
			
			return I18n.translateToLocal(
					"entity." + s + "." + this.getChestType().name().toLowerCase(Locale.US) + ".name");
		}
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		this.sortTopStacks();
	}
	
	private boolean hadStuff;
	
	public List<ItemStack> getTopStacks() { return this.topStacks; }
	
	protected void sortTopStacks() {
		if (!this.getChestType().isTransparent() || (this.world != null && this.world.isRemote)) {
			return;
		}
		
		NonNullList<ItemStack> tempCopy = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		
		boolean hasStuff = false;
		
		int compressedIdx = 0;
		
		mainLoop: for (int i = 0; i < this.getSizeInventory(); i++) {
			ItemStack itemStack = this.getStackInSlot(i);
			
			if (!itemStack.isEmpty()) {
				for (int j = 0; j < compressedIdx; j++) {
					ItemStack tempCopyStack = tempCopy.get(j);
					
					if (ItemStack.areItemsEqualIgnoreDurability(tempCopyStack, itemStack)) {
						if (itemStack.getCount() != tempCopyStack.getCount()) {
							tempCopyStack.grow(itemStack.getCount());
						}
						
						continue mainLoop;
					}
				}
				
				tempCopy.set(compressedIdx, itemStack.copy());
				
				compressedIdx++;
				
				hasStuff = true;
			}
		}
		
		if (!hasStuff && this.hadStuff) {
			this.hadStuff = false;
			
			for (int i = 0; i < this.topStacks.size(); i++) {
				this.topStacks.set(i, ItemStack.EMPTY);
			}
			
			/*
			 * if (this.world != null) { IBlockState iblockstate =
			 * this.world.getBlockState(this.pos);
			 * 
			 * this.world.notifyBlockUpdate(this.pos, iblockstate, iblockstate,
			 * 3); }
			 */
			
			return;
		}
		
		this.hadStuff = true;
		
		Collections.sort(tempCopy, (ItemStack stack1, ItemStack stack2) -> {
			if (stack1.isEmpty()) {
				return 1;
			} else if (stack2.isEmpty()) {
				return -1;
			} else {
				return stack2.getCount() - stack1.getCount();
			}
		});
		
		int p = 0;
		
		for (ItemStack element : tempCopy) {
			if (!element.isEmpty() && element.getCount() > 0) {
				if (p == this.topStacks.size()) {
					break;
				}
				
				this.topStacks.set(p, element);
				
				p++;
			}
		}
		
		for (int i = p; i < this.topStacks.size(); i++) {
			this.topStacks.set(i, ItemStack.EMPTY);
		}
		
		/*
		 * if (this.world != null) { IBlockState iblockstate =
		 * this.world.getBlockState(this.pos);
		 * 
		 * this.world.notifyBlockUpdate(this.pos, iblockstate, iblockstate, 3);
		 * }
		 */
		
		sendTopStacksPacket();
	}
	
	public void setTopStacks(List<ItemStack> stacks) {
		this.topStacks = stacks;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote && this.invTouched) {
			this.invTouched = false;
			this.sortTopStacks();
		}
	}
	
	protected void sendTopStacksPacket() {
		CartBlanche.HANDLER.sendToAllAround(new PacketUpdateTopStacks(this.getEntityId(), this.topStacks),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.posX, this.posY, this.posZ, 128));
	}
	
}
