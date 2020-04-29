package com.alexpansion.gts.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import static com.alexpansion.gts.util.RegistryHandler.POWER_PLANT_TILE;

import javax.annotation.Nullable;

import com.alexpansion.gts.GlobalTradeSystem;


public class PowerPlantTile extends TileEntity implements ITickableTileEntity{

    public static TileEntityType<PowerPlantTile> TYPE;
    private ItemStackHandler handler;

    public PowerPlantTile() {
        super(POWER_PLANT_TILE.get());
        GlobalTradeSystem.LOGGER.info("Power Plant tile created!");
    }

    @Override
    public void tick() {
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inv");
        getHandler().deserializeNBT(invTag);
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT comp = getHandler().serializeNBT();
        compound.put("inv", comp);
        return super.write(compound);
    }

    private ItemStackHandler getHandler(){
        if(handler == null){
            handler = new ItemStackHandler(1){
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return stack.getItem() == Items.DIAMOND;
                }
            };
        }
        return handler;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return LazyOptional.of(() -> (T) getHandler());
        }
        return super.getCapability(cap, side);
    }

}