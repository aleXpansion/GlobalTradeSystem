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

		int numRows = te.getSizeInventory() / 9;
		int i = (numRows - 4) * 18;

		// Tile Entity, Slot 0-26, Slot IDs 0-26
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(te, x + y * 9, 8 + x * 18, 18 + y * 18));
			}
		}

		// Player Inventory, Slot 9-35, Slot IDs 26-52
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + i));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 53-61
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 161 + i));
		}
	}
	
	@Override 
	public ItemStack transferStackInSlot(EntityPlayer playerIn,int fromSlot){
		ItemStack previous = null;
	    Slot slot = (Slot) this.inventorySlots.get(fromSlot);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack current = slot.getStack();
	        previous = current.copy();

	        if(fromSlot<27){
	        	//from TE inventory to Player Inventory
	        	if(!this.mergeItemStack(current, 26, 62, true)){
	        		return null;
	        	}
	        }else{
	        	//from Player Inventory to TE Inventory
	        	if(!this.mergeItemStack(current, 0, 27, false)){
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
