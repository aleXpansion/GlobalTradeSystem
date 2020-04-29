package com.alexpansion.gts;

import com.alexpansion.gts.setup.ClientProxy;
import com.alexpansion.gts.setup.IProxy;
import com.alexpansion.gts.setup.ModSetup;
import com.alexpansion.gts.setup.ServerProxy;
import com.alexpansion.gts.util.RegistryHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("gts")
public class GlobalTradeSystem {

    public static IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static ModSetup SETUP = new ModSetup();

    // Directly reference a log4j logger.
    //commented out so I stop getting warnings, left it in in case I need it later.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "gts";

    public GlobalTradeSystem() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        RegistryHandler.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event){
        SETUP.init();
        PROXY.init();
    }

    
}
