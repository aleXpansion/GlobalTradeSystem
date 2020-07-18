package com.alexpansion.gts.blocks.Trader;

import com.alexpansion.gts.blocks.ContainerGTS;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.setup.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TraderContainer extends ContainerGTS {

    public TraderContainer(int id, World world, BlockPos pos, PlayerInventory playerInventory) {
        super(RegistryHandler.TRADER_CONTAINER.get(), id, world, pos, playerInventory);
        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            //target slot, slot 0
            addSlot(new SlotItemHandler(h, 0, 8, 18));
            //credit slot, slot 1
            addSlot(new SlotItemHandler(h, 1, 152, 18));
            //selling slots, slots 2-10
            addSlotRange(h, 2, 8, 54, 9, 18);
            //purchase buffer slots, slots 11-28
            addSlotBox(h, 11, 8, 90, 9, 18, 2, 18);
        });
        //player inventory. main slots 29-55, hotbar 56-64
        layoutPlayerInventorySlots(8, 140);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), playerIn,
                RegistryHandler.TRADER.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            //if it's any of the trader slots, try to put in the player's inventory.
            if (index <= 29) {
                if (!this.mergeItemStack(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                //if it can hold value, try and put it in the credit slot
                if (stack.getItem() instanceof IValueContainer) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                //otherwise, try and put it in the selling slots
                } else if (index <= 64) {
                    if (!this.mergeItemStack(stack, 2, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                } 
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

}