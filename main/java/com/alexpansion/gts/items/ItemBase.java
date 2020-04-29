package com.alexpansion.gts.items;

import com.alexpansion.gts.setup.ModSetup;

import net.minecraft.item.Item;

public class ItemBase extends Item{

    public ItemBase() {
        super( new Item.Properties().group(ModSetup.GROUP));
    }

    public ItemBase(Properties prop){
        super(prop.group(ModSetup.GROUP));
    }

}