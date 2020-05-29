package com.alexpansion.gts.setup;

import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.tools.TooltipHandler;
import com.alexpansion.gts.util.RegistryHandler;
import com.alexpansion.gts.value.BaseValueManager;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModSetup {

    public static final ItemGroup GROUP = new ItemGroup("gts") {
    
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.CREDIT.get());
        }
    };
    
    public void init(){
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        MinecraftForge.EVENT_BUS.register(new TooltipHandler());
        Networking.registerMessages();
        BaseValueManager.initItemValues();
    }
}