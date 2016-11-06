package com.alexpansion.gts.handler;

import com.alexpansion.gts.client.gui.GuiTileEntityTrader;
import com.alexpansion.gts.guicontainer.ContainerTileEntityTrader;
import com.alexpansion.gts.tileentity.TileEntityTrader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
	
	public static final int TILE_ENTITY_TRADER_GUI = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == TILE_ENTITY_TRADER_GUI){
			return new ContainerTileEntityTrader(player.inventory,(TileEntityTrader)world.getTileEntity(new BlockPos(x,y,z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == TILE_ENTITY_TRADER_GUI){
			return new GuiTileEntityTrader(player.inventory,(TileEntityTrader)world.getTileEntity(new BlockPos(x,y,z)));
		}
		return null;
	}

}
