package com.alexpansion.gts.setup;

import com.alexpansion.gts.blocks.Trader.TraderBlock;
import com.alexpansion.gts.blocks.Trader.TraderContainer;
import com.alexpansion.gts.blocks.TileBlock;
import com.alexpansion.gts.blocks.PowerPlant.PowerPlantBlock;
import com.alexpansion.gts.blocks.PowerPlant.PowerPlantContainer;
import com.alexpansion.gts.blocks.PowerPlant.PowerPlantTile;
import com.alexpansion.gts.blocks.Trader.TraderTile;
import com.alexpansion.gts.items.Catalog.ItemCatalog;
import com.alexpansion.gts.items.Catalog.ItemEnderCatalog;
import com.alexpansion.gts.items.ItemCoin;
import com.alexpansion.gts.items.ItemCreditCard;
import com.alexpansion.gts.items.ItemEnderCard;
import com.alexpansion.gts.items.Catalog.CatalogContainer;

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

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    protected static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
    

    public static void init(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    //Blocks
    public static final RegistryObject<TileBlock> TRADER = BLOCKS.register("trader", TraderBlock::new);
    public static final RegistryObject<Item> TRADER_ITEM = ITEMS.register("trader", 
        () -> new BlockItem(TRADER.get(), new Item.Properties().group(ModSetup.GROUP)));
    public static final RegistryObject<TileEntityType<TraderTile>> TRADER_TILE = TILES.register("trader",
        () -> TileEntityType.Builder.create(TraderTile::new, TRADER.get()).build(null));
    public static final RegistryObject<ContainerType<TraderContainer>> TRADER_CONTAINER = CONTAINERS.register("trader", 
    () -> IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new TraderContainer(windowId, PROXY.getClientWorld(), pos, inv);
        }));

    //Blocks
    /*
    public static final RegistryObject<TileBlock> MARKET = BLOCKS.register("market", TileBlock::new);
    public static final RegistryObject<Item> MARKET_ITEM = ITEMS.register("market", 
        () -> new BlockItem(MARKET.get(), new Item.Properties().group(ModSetup.GROUP)));
    public static final RegistryObject<TileEntityType<TraderTile>> MARKET_TILE = TILES.register("market",
        () -> TileEntityType.Builder.create(TraderTile::new, TRADER.get()).build(null));
    public static final RegistryObject<ContainerType<TraderContainer>> MARKET_CONTAINER = CONTAINERS.register("market", 
    () -> IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new TraderContainer(windowId, PROXY.getClientWorld(), pos, inv);
        }));*/

    public static final RegistryObject<TileBlock> POWER_PLANT = BLOCKS.register("power_plant", PowerPlantBlock::new);
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
    public static final RegistryObject<ContainerType<CatalogContainer>> CATALOG_CONTAINER = CONTAINERS.register("catalog", 
        () -> IForgeContainerType.create((windowId,inv,data) -> {
            return new CatalogContainer(windowId, PROXY.getClientPlayer(), data);
        }));
    public static final RegistryObject<Item> CREDIT = ITEMS.register("credit", ItemCoin::new);
    public static final RegistryObject<Item> CREDIT_CARD = ITEMS.register("credit_card", ItemCreditCard::new);
    public static final RegistryObject<Item> CREDIT_CARD2 = ITEMS.register("credit_card2", () -> new ItemCreditCard(100000));
    public static final RegistryObject<Item> ENDER_CARD = ITEMS.register("ender_card", ItemEnderCard::new);
    public static final RegistryObject<Item> ENDER_CATALOG = ITEMS.register("ender_catalog", ItemEnderCatalog::new);

}