package com.alexpansion.gts.network;

import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValuesBean;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ValuesPacket implements IMessage {

	private ValuesBean bean;
	
	public ValuesPacket(){}
	
	public ValuesPacket(ValuesBean inBean){
		bean = inBean;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		bean = new ValuesBean(ByteBufUtils.readUTF8String(buf));

	}
	
	public ValuesBean getBean(){
		return bean;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, bean.toString());
	}
	
	public static class Handler implements IMessageHandler<ValuesPacket,IMessage>{

		@Override
		public IMessage onMessage(final ValuesPacket message, MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable(){
				@Override
				public void run(){
					//LogHelper.info("ValuesPacket recieved");
					ValueManager manager = ValueManager.getManager(Minecraft.getMinecraft().theWorld);
					manager.setBean(message.getBean());
				}
			});
			return null;
		}
		
	}

}
