package com.alexpansion.gts.setup;

import com.alexpansion.gts.blocks.PowerPlant.PowerPlantScreen;
import com.alexpansion.gts.blocks.Trader.TraderScreen;
import com.alexpansion.gts.items.Catalog.CatalogScreen;
import com.alexpansion.gts.util.RegistryHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

	@Override
	@SuppressWarnings("resource")
	public World getClientWorld() {
		return Minecraft.getInstance().world;
	}

	@Override
	public void init() {
		ScreenManager.registerFactory(RegistryHandler.POWER_PLANT_CONTAINER.get(), PowerPlantScreen::new);
		ScreenManager.registerFactory(RegistryHandler.TRADER_CONTAINER.get(), TraderScreen::new);
		ScreenManager.registerFactory(RegistryHandler.CATALOG_CONTAINER.get(), CatalogScreen::new);
	}

	@Override
	@SuppressWarnings("resource")
	public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
	}

	@Override
	public boolean isRemote() {
		return true;
	}


}
