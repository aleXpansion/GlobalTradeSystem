package com.alexpansion.gts.inventory;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.item.ItemCatalog;
import com.alexpansion.gts.utility.GTSUtil;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

public class InventoryCatalog implements IInventory {

	private String name = "Catalog";
	private final ItemStack invItem;
	public static final int INV_SIZE = 37;
	private ItemStack[] inventory = new ItemStack[INV_SIZE];
	private double change;

	public InventoryCatalog(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemCatalog)) {
			LogHelper.error("Attempted to create an InventoryCatalog with an ItemStack that isn't a catalog!");
		}
		invItem = stack;

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}

		readFromNBT(stack.getTagCompound());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return name.length() > 0;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(name, new Object());
	}

	@Override
	public int getSizeInventory() {
		return INV_SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			} else {
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inventory[index];
		inventory[index] = null;
		markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		markDirty();

	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}

		writeToNBT(invItem.getTagCompound());

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		sellItem();
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		sellItem();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return !(stack.getItem() instanceof ItemCatalog);
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		inventory = new ItemStack[INV_SIZE];
		markDirty();
	}

	public void writeToNBT(NBTTagCompound tagCompound) {

		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); i++) {

			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);

				items.appendTag(item);

			}
		}

		tagCompound.setTag("ItemInventory", items);
		tagCompound.setDouble("Change", change);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		change = compound.getDouble("change");

		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if (slot >= 0 && slot < getSizeInventory()) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		//sellItem();
	}

	private void sellItem() {
		if (inventory[0] != null && GTSUtil.canISell(inventory[0].getItem())) {
			ItemStack stack = inventory[0];
			Item item = stack.getItem();
			ItemCatalog catalog = (ItemCatalog) invItem.getItem();
			double value = GTSUtil.getValue(item);
			if (value + catalog.getValue(invItem) + change <= catalog.getLimit()) {
				try {
					catalog.addValue(invItem, (int) (GTSUtil.getValue(item) + change));
					change = (value + change) % 1;
					decrStackSize(0, 1);
				} catch (ValueOverflowException e) {
					LogHelper.error("ValueOverflowException in InventoryCatalog.sellItem");
					e.printStackTrace();
				}
			}
		}
	}

}
