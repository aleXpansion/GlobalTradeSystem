package com.alexpansion.gts.guicontainer;

import com.alexpansion.gts.inventory.InventoryCatalog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerItemCatalog extends Container {

	private static final int INV_START = InventoryCatalog.INV_SIZE, INV_END = INV_START + 26,
			HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	private InventoryCatalog itemInv;
	
	public ContainerItemCatalog(IInventory playerInv, InventoryCatalog itemInv) {
		this.itemInv = itemInv;
		
		// Catalog, Slot 0, Slot ID 0 (Selling Slot)
		this.addSlotToContainer(new Slot(itemInv, 0, 8, 18));

		// Catalog, Slots 1-36, Slot IDs 1-36 (Purchase Slots)
		for (int y = 0; y < 4; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(itemInv, x + y * 9 + 1, 8 + x * 18, 54 + y * 18));
			}
		}

		// Player Inventory, Slot 9-35, Slot IDs 37-63
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 64-72
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 198));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return itemInv.isUseableByPlayer(playerIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer player,int fromSlot){
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);
		
		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();
			if(fromSlot<37){
				if(!this.mergeItemStack(current, INV_START, HOTBAR_END, true)){
					return null;
				}
			}else{
				if(!this.mergeItemStack(current, 0, 1, false)){
					return null;
				}
			}
			
			if(current.stackSize == 0){
				slot.putStack(null);
			}else{
				slot.onSlotChanged();
			}
			
			if(current.stackSize == previous.stackSize){
				return null;
			}
			slot.onPickupFromSlot(player, current);
		}
		return previous;
		
		
	}
	
	@Override
	public ItemStack slotClick(int slot,int dragType,ClickType clickTypeIn,EntityPlayer player){
		if(slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()){
			return null;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
	
}
