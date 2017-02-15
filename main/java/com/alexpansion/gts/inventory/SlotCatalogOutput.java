package com.alexpansion.gts.inventory;

import com.alexpansion.gts.utility.GTSUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SlotCatalogOutput extends Slot {
	
	InventoryCatalog inventory;
	ItemStack formerStack = null;
	World world;

	public SlotCatalogOutput(InventoryCatalog inventoryIn, int index, int xPosition, int yPosition,World world) {
		super(inventoryIn, index, xPosition, yPosition);
		inventory = inventoryIn;
		this.world = world;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
		this.putStack(stack);
		if(GTSUtil.getValue(stack.getItem())>inventory.getStoredValue()&&!world.isRemote){
			this.putStack(null);
			formerStack = null;
			return;
		}
		ItemStack copyStack = stack.copy();
		copyStack.stackSize = 1;
		formerStack = copyStack;
        super.onPickupFromSlot(playerIn, stack);
        inventory.buyItem(this.slotNumber);
        this.putStack(copyStack);
    }
	
	public void resetSlot(){
		this.putStack(formerStack.copy());
		inventory.refreshSellables();
	}
	
	@Override
	public ItemStack decrStackSize(int amount)
    {
        return this.getStack();
    }
}
