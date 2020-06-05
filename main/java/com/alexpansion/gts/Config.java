package com.alexpansion.gts;

import java.util.List;

import com.alexpansion.gts.value.BaseValueManager;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class Config{

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_POWER_PLANT = "power_plant";
    public static final String CATEGORY_VALUES = "values";
    public static final String SUBCATEGORY_DEFAULT_VALUES = "default_values";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<String>> DEFAULT_ITEM_VALUES;
    public static ForgeConfigSpec.ConfigValue<List<String>> DEFAULT_TAG_VALUES;
    public static ForgeConfigSpec.IntValue POWER_PLANT_MAXPOWER;
    public static ForgeConfigSpec.IntValue POWER_PLANT_GENERATE;
    public static ForgeConfigSpec.IntValue POWER_PLANT_SEND;
    public static ForgeConfigSpec.IntValue POWER_PLANT_TICKS;


    static {

        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("Value Settings").push(CATEGORY_VALUES);

        setupDefaultValuesConfig(COMMON_BUILDER);

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupPowerPlantConfig(COMMON_BUILDER, CLIENT_BUILDER);

        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupPowerPlantConfig(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Power Plant settings").push(SUBCATEGORY_POWER_PLANT);

        POWER_PLANT_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the Power Plant generator")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        POWER_PLANT_GENERATE = COMMON_BUILDER.comment("Power generation per credit")
                .defineInRange("generate", 1000, 0, Integer.MAX_VALUE);
        POWER_PLANT_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);
        POWER_PLANT_TICKS = COMMON_BUILDER.comment("Ticks per credit")
                .defineInRange("ticks", 20, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    private static void setupDefaultValuesConfig(ForgeConfigSpec.Builder COMMON_BUILDER){
        COMMON_BUILDER.comment("Default Value settings. These are only run on world load, can be overriden by commands")
                .push(SUBCATEGORY_DEFAULT_VALUES);

        BaseValueManager.initDefaultValues();
        
        DEFAULT_ITEM_VALUES = COMMON_BUILDER.comment("Default item values")
                .define("default_item_values", BaseValueManager.getItemDefaults());
                
        DEFAULT_TAG_VALUES = COMMON_BUILDER.comment("Default tagvalues")
                .define("default_tag_values", BaseValueManager.getTagDefaults());

                
        COMMON_BUILDER.pop();

    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}