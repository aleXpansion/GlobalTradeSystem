package com.alexpansion.gts.blocks.Trader;

import javax.annotation.Nullable;

import com.alexpansion.gts.GTS;
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

    private int targetSlot = 0;
    private int creditSlot = 1;
    private int sellSlotFirst = 2;
    private int sellSlotLast = 10;
    private int bufferSlotFirst = 11;
    private int bufferSlotLast = 28;
    private int valueTransferSpeed = 1;

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);

    public TraderTile() {
        super(RegistryHandler.TRADER_TILE.get());
    }

    @Override
    public void tick() {
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        
        ItemStack targetStack = h.getStackInSlot(targetSlot);

        //if there's an item in the target slot, buy more of them.
        if(!targetStack.isEmpty()){
            // get the value of the target stack. Currently using 1 as a placeholder
            int targetValue = 1;

            if (targetValue > getValue()) {
                return;
            }

            // add one of the target item to the sold buffer
            ItemStack newStack = new ItemStack(targetStack.getItem());

            int slot = bufferSlotFirst;
            while (slot <= bufferSlotLast && !newStack.isEmpty()) {
                newStack = h.insertItem(slot, newStack, false);
                slot++;
            }

            //remove the value from the available credits
            if (newStack.isEmpty()) {
                try {
                    removeValue(targetValue);
                } catch (ValueOverflowException e) {
                    //this will only throw if we try to extract more than it has. We check for this above, so it should never happen.
                    e.printStackTrace();
                }
            }
        }

        //try to sell items in the sell slots, remove value from relevant items.
        for(int slot = sellSlotFirst;slot <= sellSlotLast;slot++){
            ItemStack stack = h.getStackInSlot(slot);
            if(stack.isEmpty()){
                continue;
            }
            if(stack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) stack.getItem();
                if(item.getValue(stack)>0 && getSpace() >0){  
                    //get the lowest of transfer speed, credits available in target, and space available.
                    int toTransfer = Math.min(valueTransferSpeed, Math.min(item.getValue(stack), getSpace()));
                    try {
                        stack = item.removeValue(stack, toTransfer);
                        h.setStackInSlot(slot, stack);
                        addValue(toTransfer);
                    } catch (ValueOverflowException e) {
                        GTS.LOGGER.error("Trader attempted to transfer more than it could.");
                        e.printStackTrace();
                    }
                }
            }else{
                //this is where I would get the value of the item I'm considering selling. Again, I'm using 1 as a placeholder
                int value = 1;

                if(getSpace() >= value){
                    int count = stack.getCount() -1;

                    try {
                        addValue(value);
                    } catch (ValueOverflowException e) {
                        e.printStackTrace();
                        return;
                    }

                    if(count <= 0){
                        h.setStackInSlot(slot, ItemStack.EMPTY);
                    }else{
                        stack.setCount(count);
                        h.setStackInSlot(slot, stack);
                    }
                }
            }
        }
            
        //go through the buffer slots and add value to items that can take it
        for(int slot = bufferSlotFirst;slot <= bufferSlotLast;slot++){
            ItemStack stack = h.getStackInSlot(slot);
            if(stack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) stack.getItem();
                if(item.getSpace(stack)>0 && getValue() >0){  
                    //get the lowest of transfer speed, credits available, and space available in target stack.
                    int toTransfer = Math.min(valueTransferSpeed, Math.min(item.getSpace(stack), getValue()));
                    try {
                        item.addValue(stack, toTransfer);
                        h.setStackInSlot(slot, stack);
                        removeValue(toTransfer);
                    } catch (ValueOverflowException e) {
                        GTS.LOGGER.error("Trader attempted to transfer more than it could.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addValue(int toAdd) throws ValueOverflowException{
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        //handler.ifPresent(h ->{
            ItemStack creditStack = h.getStackInSlot(creditSlot);

            if(creditStack.isEmpty()){
                if(toAdd > 64){
                    throw new ValueOverflowException(creditStack, toAdd-64);
                }
                creditStack = new ItemStack(RegistryHandler.CREDIT.get(),toAdd);
            }else if(creditStack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) creditStack.getItem();
                creditStack = item.addValue(creditStack, toAdd);
            }else{
                throw new ValueOverflowException(creditStack, toAdd);
            }
            h.setStackInSlot(creditSlot, creditStack);
        //});
    }

    private void removeValue(int toRemove) throws ValueOverflowException{
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        //handler.ifPresent(h ->{
            ItemStack creditStack = h.getStackInSlot(creditSlot);

            if(creditStack.isEmpty()){
                    throw new ValueOverflowException(creditStack, 0-toRemove);
            }else if(creditStack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) creditStack.getItem();
                creditStack = item.removeValue(creditStack, toRemove);
            }else{
                throw new ValueOverflowException(creditStack, 0-toRemove);
            }
            h.setStackInSlot(creditSlot, creditStack);
        //});
    }

    private int getSpace(){
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        ItemStack stack = h.getStackInSlot(creditSlot);

        if(stack.isEmpty()){
            return 64;
        }else if(stack.getItem() instanceof IValueContainer){
            return ((IValueContainer)stack.getItem()).getSpace(stack);
        }else{
            return 0;
        }

    }

    private int getValue(){
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        ItemStack stack = h.getStackInSlot(creditSlot);

        if(stack.isEmpty()){
            return 0;
        }else if(stack.getItem() instanceof IValueContainer){
            return ((IValueContainer)stack.getItem()).getValue(stack);
        }else{
            return 0;
        }
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
                if(slot == creditSlot){
                    return stack.getItem() instanceof IValueContainer;
                //if it's the target slot or a selling slot, allow anything
                //}else if(slot <=sellSlotLast){
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