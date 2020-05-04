package com.alexpansion.gts.blocks.Trader;

import javax.annotation.Nullable;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.util.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class TraderTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);

    public TraderTile() {
        super(RegistryHandler.TRADER_TILE.get());
    }

    @Override
    public void tick() {

        handler.ifPresent(h -> {

            ItemStack targetStack = h.getStackInSlot(0);
            ItemStack creditStack = h.getStackInSlot(1);

            // if either of the top slots is empty, we can't do anything.
            if (targetStack.isEmpty() || creditStack.isEmpty()) {
                return;
            }

            // if the item in the credit slot can't hold credits, we can't do anything. It
            // also causes crashes if we go further.
            if (!(creditStack.getItem() instanceof IValueContainer)) {
                return;
            }
            IValueContainer creditItem = (IValueContainer) creditStack.getItem();

            // get the value of the target stack. Currently using 1 as a placeholder
            int targetValue = 1;

            int creditsAvailable = creditItem.getValue(creditStack);

            if (targetValue > creditsAvailable) {
                return;
            }

            // add one of the target item to the sold buffer
            ItemStack newStack = new ItemStack(targetStack.getItem());

            int slot = 11;
            while (slot <= 28 && !newStack.isEmpty()) {
                newStack = h.insertItem(slot, newStack, false);
                slot++;
            }

            if (newStack.isEmpty()) {
                try {
                    creditStack = creditItem.removeValue(creditStack, targetValue);
                    h.setStackInSlot(1,creditStack);
                } catch (ValueOverflowException e) {
                    //this will only throw if we try to extract more than it has. We check for this above, so it should never happen.
                    e.printStackTrace();
                }
            }

            

        });
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT invTag = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", invTag);
        });
        return super.write(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(29) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                //if it's the credit slot, only something that can store credits
                if(slot == 1){
                    return stack.getItem() instanceof IValueContainer;
                //if it's the target slot or a selling slot, allow anything
                //}else if(slot <=10){
                   // return true;
                //if it's the purchase buffer, don't allow anything
                }else{
                    //return false;
                    return true;
                }
            }
        };

    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new TraderContainer(i, world, pos, playerInventory);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
}