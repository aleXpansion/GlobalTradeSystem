package com.alexpansion.gts.proxy;

import com.alexpansion.gts.crafting.ModRecipes;
import com.alexpansion.gts.init.ModBlocks;
import com.alexpansion.gts.init.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy implements IProxy{
	
	public void preInit(FMLPreInitializationEvent e){
		ModItems.init();
		ModBlocks.init();
		ModRecipes.init();
	}
	
	public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }

	public void registerItemRenderer(Item item, int i, String name) {
		
	}
}
