package com.alexpansion.gts;

import java.util.List;

import com.alexpansion.gts.value.managers.BaseValueManager;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class Config{

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_ENERGY_VALUES = "energy_values";
    public static final String SUBCATEGORY_POWER_PLANT = "power_plant";
    public static final String CATEGORY_VALUES = "values";
    public static final String SUBCATEGORY_DEFAULT_VALUES = "default_values";
    public static final String SUBCATEGORY_VALUE_CALCULATION = "value_calculation";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<String>> DEFAULT_ITEM_VALUES;
    public static ForgeConfigSpec.ConfigValue<List<String>> DEFAULT_TAG_VALUES;
    public static ForgeConfigSpec.IntValue ENERGY_BASE_VALUE;
    public static ForgeConfigSpec.IntValue ENERGY_MAX_AMT;
    public static ForgeConfigSpec.IntValue POWER_PLANT_MAXPOWER;
    public static ForgeConfigSpec.IntValue POWER_PLANT_GENERATE;
    public static ForgeConfigSpec.IntValue POWER_PLANT_SEND;
    public static ForgeConfigSpec.IntValue POWER_PLANT_TICKS;

    public static ForgeConfigSpec.IntValue CHANNEL_LIMIT;

    public static ForgeConfigSpec.BooleanValue NON_OPS_CAN_SEND_VALUES;
    public static ForgeConfigSpec.IntValue SOLD_ITEMS_DECAY;
    public static ForgeConfigSpec.IntValue SOLD_ITEMS_MAX;
    public static ForgeConfigSpec.IntValue BOUGHT_ITEMS_DOUBLE;
    public static ForgeConfigSpec.IntValue BOUGHT_ITEMS_MAX;


    static {

        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("Value Settings").push(CATEGORY_VALUES);

        setupDefaultValuesConfig(COMMON_BUILDER);
        setupValueCalculationConfig(COMMON_BUILDER);

        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        CHANNEL_LIMIT = COMMON_BUILDER.comment("Maximum amount a value channel can hold")
                .defineInRange("channel_limit", 1000000000, 0, Integer.MAX_VALUE);

        NON_OPS_CAN_SEND_VALUES = COMMON_BUILDER.comment("Whether non-ops can send calculated values."+
        " These are calculated in the background by JEI, but could introduce exploits.")
        .define("non_ops_can_send_values", true);

        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupPowerPlantConfig(COMMON_BUILDER, CLIENT_BUILDER);

        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupPowerPlantConfig(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Energy Value Settings. Applies to Forge Energy.").push(SUBCATEGORY_ENERGY_VALUES);

        ENERGY_BASE_VALUE = COMMON_BUILDER.comment("Base value for Forge Energy")
                .defineInRange("energy_base_value",1000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ENERGY_MAX_AMT = COMMON_BUILDER.comment("Sale cap for Forge Energy")
        .defineInRange("energy_max_amt",100000000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power Plant settings").push(SUBCATEGORY_POWER_PLANT);

        POWER_PLANT_MAXPOWER = COMMON_BUILDER.comment("Maximum power for the Power Plant generator")
                .defineInRange("maxPower", 1000000, 0, Integer.MAX_VALUE);
        POWER_PLANT_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send", 10000, 0, Integer.MAX_VALUE);

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

    private static void setupValueCalculationConfig(ForgeConfigSpec.Builder COMMON_BUILDER){
        COMMON_BUILDER.comment("Values that affect the calculation of an item's value.")
                .push(SUBCATEGORY_VALUE_CALCULATION);

        SOLD_ITEMS_DECAY = COMMON_BUILDER.comment("Controls the rate at which the value of an item will decay based on how many have been sold."
                +"When this number has been sold it will be at half base value, double this will be a quarter, and so on.")
                .defineInRange("sold_items_decay", 128, 1, Integer.MAX_VALUE);

        SOLD_ITEMS_MAX = COMMON_BUILDER.comment("Maximum amount of a given item that can be sold. "+
                "Price will gradually decrease until it hits 0 when this many have been sold."+
                "0 means there will be no limit.")
                .defineInRange("sold_items_max", 0, 0, Integer.MAX_VALUE);

        BOUGHT_ITEMS_MAX = COMMON_BUILDER.comment("Maximum number of a given item that can be bought."
                +" The item will not be available once the amount sold is less that 0 - <this>")
                .defineInRange("bought_items_max", 640, 0, Integer.MAX_VALUE);

        BOUGHT_ITEMS_DOUBLE = COMMON_BUILDER.comment("If the amount sold is less than 0, "
                +"the price will be double the original price when this value is reached.")
                .defineInRange("bought_items_double", 64, 1, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}