package com.alexpansion.gts.network;

import java.util.function.Supplier;

import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValuesBean;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ValuesRequestPacket{
	
	public ValuesRequestPacket(){}

	public ValuesRequestPacket(PacketBuffer buf) {
	}

	public void toBytes(PacketBuffer buf) {
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
			ValueManager vm = ValueManager.getVM(ctx.get().getSender().world);
			ValuesBean bean = vm.getBean();
			Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new ValuesPacket(bean));
        });
        ctx.get().setPacketHandled(true);
    }
}
