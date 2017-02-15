package com.alexpansion.gts.guicontainer;

import com.alexpansion.gts.inventory.InventoryCatalog;
import com.alexpansion.gts.inventory.SlotCatalogOutput;
import com.alexpansion.gts.utility.GTSUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerItemCatalog extends Container {

	private static final int INV_START = InventoryCatalog.INV_SIZE, INV_END = INV_START + 26,
			HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	private InventoryCatalog itemInv;

	public ContainerItemCatalog(InventoryPlayer playerInv, InventoryCatalog itemInv,EntityPlayer player) {
		this.itemInv = itemInv;

		// Catalog, Slot 0, Slot ID 0 (Selling Slot)
		this.addSlotToContainer(new Slot(itemInv, 0, 8, 18));

		// Catalog, Slot 1, Slot ID 1 (Target Slot)
		this.addSlotToContainer(new Slot(itemInv, 1, 152, 18));

		// Catalog, Slots 2-37, Slot IDs 2-37 (Purchase Slots)
		for (int y = 0; y < 4; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new SlotCatalogOutput(itemInv, x + y * 9 + 2, 8 + x * 18, 54 + y * 18,itemInv.getWorld()));
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

				if (!this.mergeItemStack(previous, INV_START, HOTBAR_END, true)) {
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
		/*if(slotId>=2&&slotId<=37){
			ItemStack itemstack = null;
	        InventoryPlayer inventoryplayer = player.inventory;

	        
	        if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1))
	        {
	           
	            if (clickTypeIn == ClickType.QUICK_MOVE)
	            {
	                

	                Slot fromSlot = (Slot)this.inventorySlots.get(slotId);

	                if (fromSlot != null && fromSlot.canTakeStack(player))
	                {
	                    ItemStack stackFromSlot = fromSlot.getStack();

	                    /*if (stackFromSlot != null && stackFromSlot.stackSize <= 0)
	                    {
	                        itemstack = stackFromSlot.copy();
	                        fromSlot.putStack((ItemStack)null);
	                    }*/

	                    /*ItemStack itemstack11 = this.transferStackInSlot(player, slotId);

	                    if (itemstack11 != null)
	                    {
	                        Item item = itemstack11.getItem();
	                        itemstack = itemstack11.copy();

	                        if (fromSlot.getStack() != null && fromSlot.getStack().getItem() == item)
	                        {
	                            this.retrySlotClick(slotId, dragType, true, player);
	                        }
	                    }
	                }
	            }
	            else
	            {
	                

	                Slot fromSlot2 = (Slot)this.inventorySlots.get(slotId);

	                if (fromSlot2 != null)
	                {
	                    ItemStack stackInSlot = fromSlot2.getStack();
	                    ItemStack stackOnMouse = inventoryplayer.getItemStack();

	                    if (stackInSlot != null)
	                    {
	                        itemstack = stackInSlot.copy();
	                    }
/*
	                    if (stackInSlot == null)
	                    {
	                        /*if (stackOnMouse != null && fromSlot2.isItemValid(stackOnMouse))
	                        {
	                            int l2 = dragType == 0 ? stackOnMouse.stackSize : 1;

	                            if (l2 > fromSlot2.getItemStackLimit(stackOnMouse))
	                            {
	                                l2 = fromSlot2.getItemStackLimit(stackOnMouse);
	                            }

	                            fromSlot2.putStack(stackOnMouse.splitStack(l2));

	                            if (stackOnMouse.stackSize == 0)
	                            {
	                                inventoryplayer.setItemStack((ItemStack)null);
	                            }
	                        }
	                    }
	                    if (fromSlot2.canTakeStack(player))
	                    {
	                    	
	                    	
	                        if (stackOnMouse == null)
	                        {
	                            if (stackInSlot.stackSize > 0)
	                            {
	                                
	                                //inventoryplayer.setItemStack(fromSlot2.getStack().copy());

	                                if (stackInSlot.stackSize <= 0)
	                                {
	                                    //fromSlot2.putStack((ItemStack)null);
	                                }

	                                //fromSlot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
	                            }
	                            else
	                            {
	                                //fromSlot2.putStack((ItemStack)null);
	                                //inventoryplayer.setItemStack((ItemStack)null);
	                            }
	                        }
	                        /*else if (fromSlot2.isItemValid(stackOnMouse))
	                        {
	                            if (stackInSlot.getItem() == stackOnMouse.getItem() && stackInSlot.getMetadata() == stackOnMouse.getMetadata() && ItemStack.areItemStackTagsEqual(stackInSlot, stackOnMouse))
	                            {
	                                int j2 = dragType == 0 ? stackOnMouse.stackSize : 1;

	                                if (j2 > fromSlot2.getItemStackLimit(stackOnMouse) - stackInSlot.stackSize)
	                                {
	                                    j2 = fromSlot2.getItemStackLimit(stackOnMouse) - stackInSlot.stackSize;
	                                }

	                                if (j2 > stackOnMouse.getMaxStackSize() - stackInSlot.stackSize)
	                                {
	                                    j2 = stackOnMouse.getMaxStackSize() - stackInSlot.stackSize;
	                                }

	                                stackOnMouse.splitStack(j2);

	                                if (stackOnMouse.stackSize == 0)
	                                {
	                                    inventoryplayer.setItemStack((ItemStack)null);
	                                }

	                                stackInSlot.stackSize += j2;
	                            }
	                            else if (stackOnMouse.stackSize <= fromSlot2.getItemStackLimit(stackOnMouse))
	                            {
	                                fromSlot2.putStack(stackOnMouse);
	                                inventoryplayer.setItemStack(stackInSlot);
	                            }
	                        }
	                        else if (stackInSlot.getItem() == stackOnMouse.getItem() && stackOnMouse.getMaxStackSize() > 1 && (!stackInSlot.getHasSubtypes() || stackInSlot.getMetadata() == stackOnMouse.getMetadata()) && ItemStack.areItemStackTagsEqual(stackInSlot, stackOnMouse))
	                        {
	                            int i2 = stackInSlot.stackSize;

	                            if (i2 > 0 && i2 + stackOnMouse.stackSize <= stackOnMouse.getMaxStackSize())
	                            {
	                                stackOnMouse.stackSize += i2;
	                                stackInSlot = fromSlot2.decrStackSize(i2);

	                                if (stackInSlot.stackSize == 0)
	                                {
	                                    fromSlot2.putStack((ItemStack)null);
	                                }

	                                fromSlot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
	                            }
	                        }
	                    }

	                    //fromSlot2.onSlotChanged();
	                }
	            }
	        }
	        else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
	        {
	            Slot slot5 = (Slot)this.inventorySlots.get(slotId);
	            ItemStack itemstack7 = inventoryplayer.getStackInSlot(dragType);

	            if (itemstack7 != null && itemstack7.stackSize <= 0)
	            {
	                itemstack7 = null;
	                inventoryplayer.setInventorySlotContents(dragType, (ItemStack)null);
	            }

	            ItemStack itemstack10 = slot5.getStack();

	            if (itemstack7 != null || itemstack10 != null)
	            {
	                if (itemstack7 == null)
	                {
	                    if (slot5.canTakeStack(player))
	                    {
	                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
	                        slot5.putStack((ItemStack)null);
	                        slot5.onPickupFromSlot(player, itemstack10);
	                    }
	                }
	                else if (itemstack10 == null)
	                {
	                    if (slot5.isItemValid(itemstack7))
	                    {
	                        int k1 = slot5.getItemStackLimit(itemstack7);

	                        if (itemstack7.stackSize > k1)
	                        {
	                            slot5.putStack(itemstack7.splitStack(k1));
	                        }
	                        else
	                        {
	                            slot5.putStack(itemstack7);
	                            inventoryplayer.setInventorySlotContents(dragType, (ItemStack)null);
	                        }
	                    }
	                }
	                else if (slot5.canTakeStack(player) && slot5.isItemValid(itemstack7))
	                {
	                    int l1 = slot5.getItemStackLimit(itemstack7);

	                    if (itemstack7.stackSize > l1)
	                    {
	                        slot5.putStack(itemstack7.splitStack(l1));
	                        slot5.onPickupFromSlot(player, itemstack10);

	                        if (!inventoryplayer.addItemStackToInventory(itemstack10))
	                        {
	                            player.dropItem(itemstack10, true);
	                        }
	                    }
	                    else
	                    {
	                        slot5.putStack(itemstack7);
	                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
	                        slot5.onPickupFromSlot(player, itemstack10);
	                    }
	                }
	            }
	        }
	        else if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && slotId >= 0)
	        {
	            Slot slot4 = (Slot)this.inventorySlots.get(slotId);

	            if (slot4 != null && slot4.getHasStack())
	            {
	                if (slot4.getStack().stackSize > 0)
	                {
	                    ItemStack itemstack6 = slot4.getStack().copy();
	                    itemstack6.stackSize = itemstack6.getMaxStackSize();
	                    inventoryplayer.setItemStack(itemstack6);
	                }
	                else
	                {
	                    slot4.putStack((ItemStack)null);
	                }
	            }
	        }
	        else if (clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack() == null && slotId >= 0)
	        {
	            Slot slot3 = (Slot)this.inventorySlots.get(slotId);

	            if (slot3 != null && slot3.getHasStack() && slot3.canTakeStack(player))
	            {
	                ItemStack itemstack5 = slot3.decrStackSize(dragType == 0 ? 1 : slot3.getStack().stackSize);
	                slot3.onPickupFromSlot(player, itemstack5);
	                player.dropItem(itemstack5, true);
	            }
	        }
	        else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0)
	        {
	            Slot slot2 = (Slot)this.inventorySlots.get(slotId);
	            ItemStack itemstack4 = inventoryplayer.getItemStack();

	            if (itemstack4 != null && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player)))
	            {
	                int i1 = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
	                int j1 = dragType == 0 ? 1 : -1;

	                for (int i3 = 0; i3 < 2; ++i3)
	                {
	                    for (int j3 = i1; j3 >= 0 && j3 < this.inventorySlots.size() && itemstack4.stackSize < itemstack4.getMaxStackSize(); j3 += j1)
	                    {
	                        Slot slot8 = (Slot)this.inventorySlots.get(j3);

	                        if (slot8.getHasStack() && canAddItemToSlot(slot8, itemstack4, true) && slot8.canTakeStack(player) && this.canMergeSlot(itemstack4, slot8) && (i3 != 0 || slot8.getStack().stackSize != slot8.getStack().getMaxStackSize()))
	                        {
	                            int l = Math.min(itemstack4.getMaxStackSize() - itemstack4.stackSize, slot8.getStack().stackSize);
	                            ItemStack itemstack2 = slot8.decrStackSize(l);
	                            itemstack4.stackSize += l;

	                            if (itemstack2.stackSize <= 0)
	                            {
	                                slot8.putStack((ItemStack)null);
	                            }

	                            slot8.onPickupFromSlot(player, itemstack2);
	                        }
	                    }
	                }
	            }

	            this.detectAndSendChanges();
	        }

	        return itemstack;
		}*/
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}
