package com.alexpansion.gts.client.gui;

import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.reference.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {

	public ModGuiConfig(GuiScreen guiScreen){
	super(guiScreen,new ConfigElement(ConfigurationHandler.configuration.getCategory(ConfigurationHandler.DEPRECIATION_CATAGORY)).getChildElements(),
			Reference.MOD_ID,false,false,GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
	}
}
