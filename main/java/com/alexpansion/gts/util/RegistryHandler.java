package com.alexpansion.gts.util;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.items.ItemBase;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,GlobalTradeSystem.MOD_ID);

    public static void init(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    //Items
    public static final RegistryObject<Item> CATALOG = ITEMS.register("catalog", ItemBase::new);
    public static final RegistryObject<Item> CREDIT = ITEMS.register("credit", ItemBase::new);
}