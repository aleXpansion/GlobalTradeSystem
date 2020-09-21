package com.alexpansion.gts.blocks.PowerPlant;

import static com.alexpansion.gts.setup.RegistryHandler.POWER_PLANT_TILE;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.alexpansion.gts.Config;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.setup.RegistryHandler;
import com.alexpansion.gts.tools.CustomEnergyStorage;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerServer;
import com.alexpansion.gts.value.ValueWrapperEnergy;

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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PowerPlantTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public PowerPlantTile() {
        super(POWER_PLANT_TILE.get());
    }

    @Override
    public void tick() {
        int stored = energy.map(e -> ((CustomEnergyStorage)e).getEnergyStored()).orElse(0);
        int realMax = energy.map(e -> ((CustomEnergyStorage)e).getMaxEnergyStored()).orElse(0);
        ValueWrapperEnergy wrapper = ValueWrapperEnergy.get("Forge", this.getWorld().isRemote());
        int energyValue = (int)wrapper.getValue();

        int max = stored == 0 ? realMax : realMax/2;

        //buy energy
        if(stored <= max-energyValue && wrapper.isAvailable()){
            handler.ifPresent(h -> {
                ItemStack valueStack = h.getStackInSlot(0);
                //If the stack in the credit slot doesn't hold credits, do nothing.
                if(!(valueStack.getItem() instanceof IValueContainer)){
                    return;
                }
                IValueContainer container = (IValueContainer)valueStack.getItem();
                int credits = container.getValue(valueStack);
                int space = max - stored;
                int count = 0;
                while(credits >= 1 && energyValue <=space && energyValue > 0 &&count < 100){
                    energy.ifPresent(e -> ((CustomEnergyStorage)e).addEnergy(energyValue));
                    credits -= 1;
                    space -= energyValue;
                    count++;
                    container.setValue(valueStack, credits);
                    if(!this.world.isRemote){
                        ((ValueManagerServer)ValueManager.getVM(this.world)).addValueSold(wrapper, -1, 0-energyValue, world);
                    }
                }
            });
        }

        //sell energy
        if(stored > (max + energyValue)){
            handler.ifPresent(h -> {
                ItemStack valueStack = h.getStackInSlot(0);
                //get information about the valueContainer
                int space = 0;
                int credits = 0;
                IValueContainer container;
                if(!(valueStack.getItem() instanceof IValueContainer)){
                    //If the stack in the credit slot doesn't hold credits, check if it's empty
                    if( valueStack.isEmpty()){
                        //if it is, place a credit there
                        space = 64;
                        valueStack = new ItemStack(RegistryHandler.CREDIT.get());
                        container = (IValueContainer)valueStack.getItem();
                        h.insertItem(0, valueStack, false);
                    }
                    //if it's neither a ValueContainer or empty, something's wrong, do nothing.
                    return;
                }else{
                    //if it's already a ValueContainer, make sure it's not full.
                    container = (IValueContainer)valueStack.getItem();
                    if(container.getSpace(valueStack) < 1){
                        return;
                    }else{
                        credits = container.getValue(valueStack);
                        space = container.getSpace(valueStack);
                    }
                }

                //calculate how many cycles to run, then record the results
                int toInsert = Math.min(space,Math.min(1000,(stored-max)/energyValue));
                int toExtract = energyValue * toInsert;
                container.setValue(valueStack, credits+toInsert);
                energy.ifPresent(e -> ((CustomEnergyStorage)e).consumeEnergy(toExtract));
                if(!this.world.isRemote){
                    ((ValueManagerServer)ValueManager.getVM(this.world)).addValueSold(wrapper, toInsert, energyValue+toInsert, world);
                }
            });
        }

        /*
        if(counter >0){
            counter--;
            int toAdd = Config.POWER_PLANT_GENERATE.get()/Config.POWER_PLANT_TICKS.get();
            energy.ifPresent(e -> ((CustomEnergyStorage)e).addEnergy(toAdd));
            markDirty();
        } else{
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                //If the stack in the credit slot doesn't hold credits, do nothing.
                if(!(stack.getItem() instanceof IValueContainer)){
                    return;
                }
                IValueContainer container = (IValueContainer)stack.getItem();
                int credits = container.getValue(stack);
                //int stored = energy.map(e -> ((CustomEnergyStorage)e).getEnergyStored()).orElse(0);
                int max = energy.map(e -> ((CustomEnergyStorage)e).getMaxEnergyStored()).orElse(0);
                if(credits > 0 && stored < max){
                    container.setValue(stack, credits-1);
                    counter = Config.POWER_PLANT_TICKS.get();
                }else{
                }
            });
        }
        */
        if(stored > 0){
            sendOutPower();
        }
        
    }

    private void sendOutPower() {
        energy.ifPresent(energy ->{
            AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
            if(capacity.get() > 0 ){
                for(Direction direction: Direction.values()){
                    TileEntity te = world.getTileEntity(pos.offset(direction));
                    if(te != null){
                        te.getCapability(CapabilityEnergy.ENERGY, direction).ifPresent(handler -> {
                            if(handler.canReceive()){
                                int received = handler.receiveEnergy(Math.min(capacity.get(),10000), false);
                                capacity.addAndGet(-received);
                                ((CustomEnergyStorage)energy).consumeEnergy(received);
                                markDirty();
                            }
                        });
                        if(capacity.get() <= 0){
                            return;
                        }
                    }

                }
            }
        });
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public void read(BlockState state,CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        CompoundNBT energyTag = compound.getCompound("energy");
        energy.ifPresent( h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));
        super.read(state, compound);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT invTag = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", invTag);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });
        return super.write(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof IValueContainer;
            }
        };

    }

    private IEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.POWER_PLANT_MAXPOWER.get(), 10000);
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }else if(cap == CapabilityEnergy.ENERGY){
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new PowerPlantContainer(i, world, pos, playerInventory, playerEntity);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

}