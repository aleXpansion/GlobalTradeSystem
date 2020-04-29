package com.alexpansion.gts.util;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.blocks.BlockTrader;
import com.alexpansion.gts.items.ItemBase;
import com.alexpansion.gts.setup.ModSetup;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, GlobalTradeSystem.MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, GlobalTradeSystem.MOD_ID);
    

    public static void init(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    //Blocks
    public static final RegistryObject<BlockTrader> TRADER = BLOCKS.register("trader", BlockTrader::new);
    public static final RegistryObject<Item> TRADER_ITEM = ITEMS.register("trader", () -> new BlockItem(TRADER.get(), new Item.Properties().group(ModSetup.GROUP)));

    //Items
    public static final RegistryObject<Item> CATALOG = ITEMS.register("catalog", ItemBase::new);
    public static final RegistryObject<Item> CREDIT = ITEMS.register("credit", ItemBase::new);
}