package com.alexpansion.gts.tileentity;

import com.alexpansion.gts.guicontainer.ContainerTileEntityTrader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class TileEntityTrader extends TileEntityGTS {

	public TileEntityTrader(World worldIn) {
		super(worldIn, 29);
	}

	public TileEntityTrader() {
		super(29);
	}

	public String getGuiID() {
		return "gts:trader";
	}

	@Override
	public void init() {
		targetSlot = 0;
		creditSlot = 1;
		sellSlotFirst = 2;
		sellSlotLast = 10;
		buySlotFirst = 11;
		buySlotLast = 28;
	}
	
	/**
	 * Get the name of this object.
	 */
	public String getName() {
		return "container.trader";
	}
	
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerTileEntityTrader(playerInventory, this);
	}

}
