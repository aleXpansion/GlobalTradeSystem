package com.alexpansion.gts.handler;

import com.alexpansion.gts.client.gui.GuiItemCatalog;
import com.alexpansion.gts.client.gui.GuiTileEntityTrader;
import com.alexpansion.gts.guicontainer.ContainerItemCatalog;
import com.alexpansion.gts.guicontainer.ContainerTileEntityTrader;
import com.alexpansion.gts.inventory.InventoryCatalog;
import com.alexpansion.gts.item.ItemCatalog;
import com.alexpansion.gts.tileentity.TileEntityTrader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

	public static final int TILE_ENTITY_TRADER_GUI = 0;
	public static final int ITEM_CATALOG_GUI = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == TILE_ENTITY_TRADER_GUI) {
			return new ContainerTileEntityTrader(player.inventory,
					(TileEntityTrader) world.getTileEntity(new BlockPos(x, y, z)));
		} else if (ID == ITEM_CATALOG_GUI) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemCatalog) {
				ContainerItemCatalog container= new ContainerItemCatalog(player.inventory, new InventoryCatalog(player.getHeldItemMainhand()));
				player.openContainer = container;
				return container;
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == TILE_ENTITY_TRADER_GUI) {
			return new GuiTileEntityTrader(player.inventory,
					(TileEntityTrader) world.getTileEntity(new BlockPos(x, y, z)));
		} else if (ID == ITEM_CATALOG_GUI) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemCatalog) {
				return new GuiItemCatalog(player.inventory, new InventoryCatalog(player.getHeldItemMainhand()));
			}
		}
		return null;
	}

}
