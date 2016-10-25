package com.alexpansion.gts;

import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.proxy.CommonProxy;
import com.alexpansion.gts.reference.Reference;
import com.alexpansion.gts.tileentity.TileEntityTrader;
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
	public static CommonProxy proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event);
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
		
		//GTSUtil.initItemValues();
		GameRegistry.registerTileEntity(TileEntityTrader.class, "gts:seller");
		
		LogHelper.info("Pre Initialization Complete.");
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		LogHelper.info("Initialization Complete.");
		
	}
	
	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event){
		proxy.postInit(event);
		LogHelper.info("Post  Initialization Complete.");
		
	}
}

