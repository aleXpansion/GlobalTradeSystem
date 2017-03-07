package com.alexpansion.gts.network;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.utility.ValueManager;
import com.alexpansion.gts.utility.ValuesBean;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ValuesRequestPacket implements IMessage {
	
	public ValuesRequestPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<ValuesRequestPacket,IMessage>{

		@Override
		public IMessage onMessage(ValuesRequestPacket message, final MessageContext ctx) {
			final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			IThreadListener mainThread = (WorldServer) player.worldObj;
			mainThread.addScheduledTask( new Runnable(){
				@Override
				public void run(){
					System.out.println(ctx.getServerHandler().playerEntity.getDisplayNameString());
					//EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					ValueManager manager = ValueManager.getManager(player.worldObj);
					ValuesBean bean= manager.getBean();
					GlobalTradeSystem.network.sendTo(new ValuesPacket(bean), player);
				}
			});
			return null;
		}
		
	}
}
