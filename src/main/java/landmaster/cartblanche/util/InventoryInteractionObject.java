package landmaster.cartblanche.util;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;

public class InventoryInteractionObject implements IInteractionObject, IInventory {
	private IInventory inv;
	private IInteractionObject intobj;
	public InventoryInteractionObject(IInventory inv, IInteractionObject intobj) {
		this.inv = inv;
		this.intobj = intobj;
	}
	
	@Override
	public String getName() {
		return inv.getName();
	}
	
	@Override
	public boolean hasCustomName() {
		return inv.hasCustomName();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return inv.getDisplayName();
	}
	
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}
	
	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}
	
	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}
	
	@Override
	public void markDirty() {
		inv.markDirty();
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return inv.isUsableByPlayer(player);
	}
	
	@Override
	public void openInventory(EntityPlayer player) {
		inv.openInventory(player);
	}
	
	@Override
	public void closeInventory(EntityPlayer player) {
		inv.closeInventory(player);
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}
	
	@Override
	public int getField(int id) {
		return inv.getField(id);
	}
	
	@Override
	public void setField(int id, int value) {
		inv.setField(id, value);
	}
	
	@Override
	public int getFieldCount() {
		return inv.getFieldCount();
	}
	
	@Override
	public void clear() {
		inv.clear();
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return intobj.createContainer(playerInventory, playerIn);
	}
	
	@Override
	public String getGuiID() {
		return intobj.getGuiID();
	}
	
}
