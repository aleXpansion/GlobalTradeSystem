package com.alexpansion.gts.util;

import com.alexpansion.gts.blocks.BlockTrader;
import com.alexpansion.gts.blocks.PowerPlant;
import com.alexpansion.gts.blocks.PowerPlantContainer;
import com.alexpansion.gts.blocks.PowerPlantTile;
import com.alexpansion.gts.items.ItemBase;
import com.alexpansion.gts.items.ItemCatalog;
import com.alexpansion.gts.setup.ModSetup;

import static com.alexpansion.gts.GTS.MOD_ID;
import static com.alexpansion.gts.GTS.PROXY;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MOD_ID);
    

    public static void init(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    //Blocks
    public static final RegistryObject<BlockTrader> TRADER = BLOCKS.register("trader", BlockTrader::new);
    public static final RegistryObject<Item> TRADER_ITEM = ITEMS.register("trader", 
        () -> new BlockItem(TRADER.get(), new Item.Properties().group(ModSetup.GROUP)));


    public static final RegistryObject<PowerPlant> POWER_PLANT = BLOCKS.register("power_plant", PowerPlant::new);
    public static final RegistryObject<Item> POWER_PLANT_ITEM = ITEMS.register("power_plant", 
        () -> new BlockItem(POWER_PLANT.get(), new Item.Properties().group(ModSetup.GROUP)));
    public static final RegistryObject<TileEntityType<PowerPlantTile>> POWER_PLANT_TILE = TILES.register("power_plant",
        () -> TileEntityType.Builder.create(PowerPlantTile::new, POWER_PLANT.get()).build(null));
    public static final RegistryObject<ContainerType<PowerPlantContainer>> POWER_PLANT_CONTAINER = CONTAINERS.register("power_plant", 
        () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PowerPlantContainer(windowId, PROXY.getClientWorld(), pos, inv,PROXY.getClientPlayer());
            }));

    //Items
    public static final RegistryObject<Item> CATALOG = ITEMS.register("catalog", ItemCatalog::new);
    public static final RegistryObject<Item> CREDIT = ITEMS.register("credit", ItemBase::new);
}