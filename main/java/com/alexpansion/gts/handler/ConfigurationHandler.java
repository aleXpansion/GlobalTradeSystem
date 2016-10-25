package com.alexpansion.gts.handler;

import java.io.File;

import com.alexpansion.gts.reference.Reference;
import com.alexpansion.gts.utility.GTSUtil;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {

	public static Configuration configuration;
	public static int rampUpCredits;
	public static double depreciationMultiplier;
	public static String[] configValues;
	
	public static final String DEPRECIATION_CATAGORY = "depreciation";

	public static void init(File configFile) {

		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}

	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {

		rampUpCredits = configuration.getInt("rampUpCredits", DEPRECIATION_CATAGORY, 1000, 0, Integer.MAX_VALUE,
				"The ramp-up time, in credits. Until the total amount sold reaches this value, the depreciation affect will gradually increase.");

		depreciationMultiplier = configuration.getFloat("depreciationMultiplier", DEPRECIATION_CATAGORY, 2, 0, 100,
				"The multiplier used when calculating depreciation. Note: If this is less than 0, value will increase over time.");
		
		configValues = configuration.getStringList("Values", DEPRECIATION_CATAGORY, GTSUtil.getDefaultValues(), "The base values for all the items.");
		//TODO add values config

		if (configuration.hasChanged()) {
			configuration.save();
		}

	}
}
