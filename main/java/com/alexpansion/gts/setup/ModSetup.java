package com.alexpansion.gts.setup;

import com.alexpansion.gts.util.RegistryHandler;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

    public static final ItemGroup GROUP = new ItemGroup("gts") {
    
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.CREDIT.get());
        }
    };
    
    public void init(){

    }
}