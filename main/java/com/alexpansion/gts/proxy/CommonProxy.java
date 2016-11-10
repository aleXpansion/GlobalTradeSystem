package com.alexpansion.gts.proxy;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.crafting.ModRecipes;
import com.alexpansion.gts.handler.ModGuiHandler;
import com.alexpansion.gts.init.ModBlocks;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.network.PacketHandler;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public abstract class CommonProxy implements IProxy{
	
	public void preInit(FMLPreInitializationEvent e){
		ModItems.init();
		ModBlocks.init();
		ModRecipes.init();
		PacketHandler.registerMessages("modgts");
	}
	
	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(GlobalTradeSystem.instance, new ModGuiHandler());

    }

    public void postInit(FMLPostInitializationEvent e) {

    }

	public void registerItemRenderer(Item item, int i, String name) {
		
	}
}
