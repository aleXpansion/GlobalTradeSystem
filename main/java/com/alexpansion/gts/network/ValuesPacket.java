package com.alexpansion.gts.network;

import java.util.function.Supplier;

import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerClient;
import com.alexpansion.gts.value.managers.ValuesBean;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ValuesPacket{

	private ValuesBean bean;
	
	
	public ValuesPacket(ValuesBean inBean){
		bean = inBean;
	}
	
	
	public ValuesPacket(PacketBuffer buf) {
		bean = ValuesBean.create(buf,true);
	}
	
	public ValuesBean getBean(){
		return bean;
	}

	public void toBytes(PacketBuffer buf) {
		if(bean == null){
			bean = new ValuesBean();
		}
		buf = bean.toBytes(buf);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Context get = ctx.get();
        get.enqueueWork(() -> {
			ValueManagerClient vm = ValueManager.getClientVM();
			vm.setBean(this.getBean());
		});
        ctx.get().setPacketHandled(true);
    }

}
