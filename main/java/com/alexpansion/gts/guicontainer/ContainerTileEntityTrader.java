package com.alexpansion.gts.guicontainer;

import com.alexpansion.gts.tileentity.TileEntityTrader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTileEntityTrader extends Container {

	private TileEntityTrader te;

	public ContainerTileEntityTrader(IInventory playerInv, TileEntityTrader te) {
		this.te = te;

		// Trader, Slot 0, Slot ID 0 (Target Slot)
		this.addSlotToContainer(new Slot(te, 0, 8, 18));

		// Trader, Slot 1, Slot ID 1 (Credit Slot)
		this.addSlotToContainer(new Slot(te, 1, 152, 18));

		// Trader, Slots 2-10, Slot IDs 2-10 (Selling Slots)
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(te, 2 + x, 8 + x * 18, 54));
		}

		// Trader, Slots 11-28, Slot IDs 11-28 (Purchase Slots)
		for (int y = 0; y < 2; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(te, x + y * 9 + 11, 8 + x * 18, 90 + y * 18));
			}
		}

		// Player Inventory, Slot 9-35, Slot IDs 29-55
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 141 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 56-62
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 198));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();

			if (fromSlot < 29) {
				// from TE inventory to Player Inventory
				if (!this.mergeItemStack(current, 26, 62, true)) {
					return null;
				}
			} else {
				// from Player Inventory to TE Inventory
				if (!this.mergeItemStack(current, 2, 11, false)) {
					return null;
				}
			}

			if (current.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if (current.stackSize == previous.stackSize)
				return null;
			slot.onPickupFromSlot(playerIn, current);
		}
		return previous;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.te.isUseableByPlayer(playerIn);
	}

}
