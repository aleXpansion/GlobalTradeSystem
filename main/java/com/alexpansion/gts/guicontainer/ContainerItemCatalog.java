package com.alexpansion.gts.guicontainer;

import com.alexpansion.gts.inventory.InventoryCatalog;
import com.alexpansion.gts.inventory.SlotCatalogOutput;
import com.alexpansion.gts.utility.GTSUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerItemCatalog extends Container {

	private static final int INV_START = InventoryCatalog.INV_SIZE, INV_END = INV_START + 26,
			HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	private InventoryCatalog itemInv;

	public ContainerItemCatalog(InventoryPlayer playerInv, InventoryCatalog itemInv, EntityPlayer player) {
		this.itemInv = itemInv;

		// Catalog, Slot 0, Slot ID 0 (Selling Slot)
		this.addSlotToContainer(new Slot(itemInv, 0, 8, 18));

		// Catalog, Slot 1, Slot ID 1 (Target Slot)
		this.addSlotToContainer(new Slot(itemInv, 1, 152, 18));

		// Catalog, Slots 2-37, Slot IDs 2-37 (Purchase Slots)
		for (int y = 0; y < 4; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(
						new SlotCatalogOutput(itemInv, x + y * 9 + 2, 8 + x * 18, 54 + y * 18, itemInv.getWorld()));
			}
		}

		// Player Inventory, Slot 9-35, Slot IDs 38-64
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 65-73 (hotbar)
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 198));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return itemInv.isUseableByPlayer(playerIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();
			if (fromSlot <= 1) {
				if (!this.mergeItemStack(current, INV_START, HOTBAR_END, true)) {
					return null;
				}
			} else if (fromSlot <= 37) {

				itemInv.buyItem(fromSlot);
				int i = 0;
				while (itemInv.getStoredValue() > GTSUtil.getValue(current.getItem())
						&& current.stackSize < current.getMaxStackSize() && i < 64) {
					current.stackSize++;
					itemInv.buyItem(fromSlot);
					i++;
				}

				// previous.stackSize = previous.getMaxStackSize();

				if (!this.mergeItemStack(current, INV_START, HOTBAR_END, true)) {
					return null;
				}
			} else {
				if (GTSUtil.canISell(current.getItem())) {
					if (!this.mergeItemStack(current, 0, 1, false)) {
						return null;
					}
				}
			}

			if (current.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (current.stackSize == previous.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, current);
		}
		return previous;

	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId >= 0 && getSlot(slotId) != null && getSlot(slotId).getStack() == player.getHeldItemMainhand()) {
			return null;
		}
		if (slotId >= 2 && slotId <= 37) {
			ItemStack itemstack = null;

			if (clickTypeIn == ClickType.QUICK_MOVE) {

				Slot fromSlot = (Slot) this.inventorySlots.get(slotId);

				if (fromSlot != null && fromSlot.canTakeStack(player)) {
					ItemStack stackFromSlot = fromSlot.getStack();

					if (stackFromSlot != null && stackFromSlot.stackSize <= 0) {
						itemstack = stackFromSlot.copy();
						fromSlot.putStack((ItemStack) null);
					}

					ItemStack itemstack11 = this.transferStackInSlot(player, slotId);

					if (itemstack11 != null) {
						itemstack = itemstack11.copy();

					}
				}
				return itemstack;

			}
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}
