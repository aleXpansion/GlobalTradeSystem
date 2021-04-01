package com.alexpansion.gts.network;

import com.alexpansion.gts.GTS;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {return ID++;}

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(GTS.MOD_ID, "global_trade_system")
            , () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
            ValuesPacket.class,
            ValuesPacket::toBytes,
            ValuesPacket::new,
            ValuesPacket::handle);
        INSTANCE.registerMessage(nextID(),
            ValuesRequestPacket.class,
            ValuesRequestPacket::toBytes,
            ValuesRequestPacket::new,
            ValuesRequestPacket::handle);
        INSTANCE.registerMessage(nextID(),
            BaseValuePacket.class,
            BaseValuePacket::toBytes,
            BaseValuePacket::new,
            BaseValuePacket::handle);
        INSTANCE.registerMessage(nextID(),
            CatalogRefreshPacket.class,
            CatalogRefreshPacket::toBytes,
            CatalogRefreshPacket::new,
            CatalogRefreshPacket::handle);
        INSTANCE.registerMessage(nextID(),
            RefreshRequestPacket.class,
            RefreshRequestPacket::toBytes,
            RefreshRequestPacket::new,
            RefreshRequestPacket::handle);
    }
}