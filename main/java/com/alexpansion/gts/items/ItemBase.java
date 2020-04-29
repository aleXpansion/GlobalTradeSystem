package com.alexpansion.gts.items;

import com.alexpansion.gts.GlobalTradeSystem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemBase extends Item{

    public ItemBase() {
        super( new Item.Properties().group(GlobalTradeSystem.TAB));
    }

}