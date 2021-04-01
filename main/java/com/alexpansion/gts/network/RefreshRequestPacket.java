package com.alexpansion.gts.network;

import java.util.function.Supplier;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.items.Catalog.CatalogContainer;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RefreshRequestPacket{
	
	public RefreshRequestPacket(){}

	public RefreshRequestPacket(PacketBuffer buf) {
	}

	public void toBytes(PacketBuffer buf) {
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
			if(ctx.get().getSender().openContainer instanceof CatalogContainer){
				CatalogContainer cont = (CatalogContainer)ctx.get().getSender().openContainer;
				cont.refresh();
				GTS.LOGGER.info("Sending refresh per request to "+ctx.get().getSender().getName().getString());
			}
        });
        ctx.get().setPacketHandled(true);
    }
}
