package com.alexpansion.gts.handler;

import com.alexpansion.gts.tileentity.TileEntityGTS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class GTSItemHandler extends ItemStackHandler {

	private TileEntityGTS te;

	public GTSItemHandler(TileEntityGTS te) {
		this.te = te;
	}

	@Override
	public int getSlots() {
		return te.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return te.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// check if it's in the selling slots
		if (slot >= te.sellSlotFirst && slot <= te.sellSlotLast) {

			// check for null or size 0 stacks.
			if (stack == null || stack.stackSize == 0) {
				return null;
			}

			ItemStack existing = te.getStackInSlot(slot);
			int limit = stack.getMaxStackSize();

			if (existing != null) {
				if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
					return stack;

				limit -= existing.stackSize;
			}

			if (limit <= 0) {
				return stack;
			}

			boolean reachedLimit = stack.stackSize > limit;

			if (!simulate) {
				if (existing == null) {
					te.setInventorySlotContents(slot,
							reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				} else {
					existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}

			return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;

		} else {
			return stack;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		if (slot >= te.buySlotFirst && slot <= te.buySlotLast) {
			if (amount == 0) {
				return null;
			}

			ItemStack existing = te.getStackInSlot(slot);

			if (existing == null) {
				return null;
			}

			int toExtract = Math.min(amount, existing.getMaxStackSize());

			if (existing.stackSize <= toExtract) {
				if (!simulate) {
					te.setInventorySlotContents(slot, null);
				}
				return existing;
			} else {
				if (!simulate) {
					te.setInventorySlotContents(slot,
							ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
				}

				return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
			}

		} else {
			return null;
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (ItemStack.areItemStacksEqual(this.getStackInSlot(slot), stack))
			return;
		te.setInventorySlotContents(slot, stack);
	}

}
