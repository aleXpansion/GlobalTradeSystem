package com.alexpansion.gts.blocks.PowerPlant;

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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.util.INBTSerializable;

import static com.alexpansion.gts.setup.RegistryHandler.POWER_PLANT_TILE;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.alexpansion.gts.Config;
import com.alexpansion.gts.tools.CustomEnergyStorage;
import com.alexpansion.gts.setup.RegistryHandler;

public class PowerPlantTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int counter;

    public PowerPlantTile() {
        super(POWER_PLANT_TILE.get());
    }

    @Override
    public void tick() {
        if(counter >0){
            counter--;
            int toAdd = Config.POWER_PLANT_GENERATE.get()/Config.POWER_PLANT_TICKS.get();
            energy.ifPresent(e -> ((CustomEnergyStorage)e).addEnergy(toAdd));
            markDirty();
        } else{
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                int stored = energy.map(e -> ((CustomEnergyStorage)e).getEnergyStored()).orElse(0);
                int max = energy.map(e -> ((CustomEnergyStorage)e).getMaxEnergyStored()).orElse(0);
                if(stack.getItem() == RegistryHandler.CREDIT.get() && stored < max){
                    h.extractItem(0, 1, false);
                    counter = Config.POWER_PLANT_TICKS.get();
                }else{
                }
            });
        }

        sendOutPower();
        
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
                                int received = handler.receiveEnergy(Math.min(capacity.get(),100), false);
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
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        CompoundNBT energyTag = compound.getCompound("energy");
        energy.ifPresent( h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));
        super.read(compound);
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
                return stack.getItem() == RegistryHandler.CREDIT.get();
            }
        };

    }

    private IEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.POWER_PLANT_MAXPOWER.get(), 0);
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