package com.alexpansion.gts.blocks.Trader;

import javax.annotation.Nullable;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.items.ValueStack;
import com.alexpansion.gts.setup.RegistryHandler;
import com.alexpansion.gts.value.managers.ValueManager;

import net.minecraft.block.BlockState;
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

    private Double change;

    private ValueManager vm;

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);

    public TraderTile() {
        super(RegistryHandler.TRADER_TILE.get());
        change = 0.0;
    }

    @Override
    public void tick() {

        if(world.isRemote){
            return;
        }else{
            vm = ValueManager.getVM(world);
        }

        //GTS.LOGGER.info("TraderTile tick");
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        
        ItemStack targetStack = h.getStackInSlot(targetSlot);

        //if there's an item in the target slot, buy more of them.
        buy:
        if(!targetStack.isEmpty()){
            Double targetValue = vm.getValue(targetStack);

            if(!vm.canIBuy(targetStack.getItem())){
                break buy;
            }

            if (targetValue > getValue()) {
                break buy;
            }

            // add one of the target item to the sold buffer
            ItemStack newStack = new ItemStack(targetStack.getItem());

            int slot = bufferSlotFirst;
            while (slot <= bufferSlotLast && !newStack.isEmpty()) {
                ItemStack existing = h.getStackInSlot(slot);
                if(existing.isEmpty()){
                    existing = newStack;
                    newStack = ItemStack.EMPTY;
                    h.setStackInSlot(slot, existing);;
                }else if(existing.getItem() == newStack.getItem()){
                    if(existing.getCount() < existing.getMaxStackSize()){
                        existing.setCount(existing.getCount()+1);
                        newStack = ItemStack.EMPTY;
                        h.setStackInSlot(slot, existing);;
                    }
                }
                slot++;
            }

            //remove the value from the available credits
            if (newStack.isEmpty()) {
                removeValue(targetValue);
                vm.addValueSold(targetStack.getItem(),-1, 0-targetValue, world);
            }
        }

        //try to sell items in the sell slots, remove value from relevant items.
        for(int slot = sellSlotFirst;slot <= sellSlotLast;slot++){
            ItemStack stack = h.getStackInSlot(slot);
            if(stack.isEmpty()){
                continue;
            }
            if(stack.getItem() instanceof IValueContainer){
                ValueStack itemValue = new ValueStack(stack, world);
                if(itemValue.getValue()>0 && getSpace() >0){  
                    //get the lowest of transfer speed, credits available in target, and space available.
                    int toTransfer = Math.min(valueTransferSpeed, Math.min(itemValue.getValue(), getSpace()));
                    stack = itemValue.removeValue(toTransfer);
                    h.setStackInSlot(slot, stack);
                    addValue(toTransfer);
                }
            }else if(vm.canISell(stack.getItem())){
                Double value = vm.getValue(stack);

                if(getSpace() >= value){
                    int count = stack.getCount() -1;
                    addValue(value);
                    vm.addValueSold(stack.getItem(), 1,value,world);

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
                ValueStack itemValue = new ValueStack(stack, world);
                if(itemValue.getSpace()>0 && getValue() >0){  
                    //get the lowest of transfer speed, credits available, and space available in target stack.
                    int toTransfer = Math.min(valueTransferSpeed, Math.min(itemValue.getSpace(), getValue()));
                    itemValue.addValue(toTransfer);
                    h.setStackInSlot(slot, stack);
                    removeValue(toTransfer);
                }
            }
        }
    }

    private void addValue(Double toAdd){
        change += toAdd%1;
        if(change > 1){
            change --;
            toAdd ++;
        }
        if(toAdd >= 1){
            addValue(toAdd.intValue());
        }
    }

    private void addValue(int toAdd){
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
            ItemStack creditStack = h.getStackInSlot(creditSlot);

            if(creditStack.isEmpty()){
                if(toAdd > 64){
                    GTS.LOGGER.error("Attempted to add more than 64 to empty credit slot in Trader");
                    toAdd = 64;
                }
                creditStack = new ItemStack(RegistryHandler.CREDIT.get(),toAdd);
            }else if(creditStack.getItem() instanceof IValueContainer){
                ValueStack item = new ValueStack(creditStack, world);
                creditStack = item.addValue(toAdd);
            }else{
                GTS.LOGGER.error("Attempted to add value to non-ValueContainer in Trader");
            }
            h.setStackInSlot(creditSlot, creditStack);
    }

    private void removeValue(Double toRemove){
        int outRemove;
        if(toRemove%1 <= change){
            change -= toRemove;
            outRemove = toRemove.intValue();
        }else{
            change += 1-toRemove%1;
            outRemove = toRemove.intValue()+1;
        }
        if(outRemove > 0){
            removeValue(outRemove);
        }
    }

    private void removeValue(int toRemove){
        ItemStackHandler h = handler.orElseThrow(() -> new RuntimeException("Trader's handler is missing!"));
        //handler.ifPresent(h ->{
            ItemStack creditStack = h.getStackInSlot(creditSlot);

            if(creditStack.isEmpty()){
                GTS.LOGGER.error("Attempted to remove value from empty stack in Trader");
            }else if(creditStack.getItem() instanceof IValueContainer){
                ValueStack item = new ValueStack(creditStack, world);
                creditStack = item.removeValue(toRemove);
            }else{
                GTS.LOGGER.error("Attempted to remove value from non-ValueContainer in Trader");
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
            return ((IValueContainer)stack.getItem()).getSpace(stack,world);
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
            return ((IValueContainer)stack.getItem()).getValue(stack, world);
        }else{
            return 0;
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        super.read(state, compound);
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
                }else if(slot <=sellSlotLast){
                   return true;
                //if it's the purchase buffer, don't allow anything that's not a valueContainer
                }else if(slot >= bufferSlotFirst && slot <= bufferSlotLast){
                    return stack.getItem() instanceof IValueContainer;
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