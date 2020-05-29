package com.alexpansion.gts.blocks.PowerPlant;

import com.alexpansion.gts.blocks.TileBlock;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class PowerPlantBlock extends TileBlock {

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PowerPlantTile();
    }
    
}