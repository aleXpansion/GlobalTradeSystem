package com.alexpansion.gts;

import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.init.ModBlocks;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.proxy.IProxy;
import com.alexpansion.gts.reference.Reference;
import com.alexpansion.gts.tileentity.TileEntitySeller;
import com.alexpansion.gts.utility.GTSUtil;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME, guiFactory = Reference.GUI_FACTORY_CLASS)
public class GlobalTradeSystem {
	
	@Mod.Instance(Reference.MOD_ID)
	public static GlobalTradeSystem instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
		
		ModItems.init();
		ModBlocks.init();
		GTSUtil.initItemValues();
		GameRegistry.registerTileEntity(TileEntitySeller.class, "gts:seller");
		
		LogHelper.info("Pre Initialization Complete.");
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		
		LogHelper.info("Initialization Complete.");
		
	}
	
	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event){
		
		LogHelper.info("Post  Initialization Complete.");
		
	}
}

