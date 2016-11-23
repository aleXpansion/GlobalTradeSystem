package com.alexpansion.gts.network;

import com.alexpansion.gts.item.IValueContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCatalogOpened implements IMessage {
	
	private int storedValue;
	
	public PacketCatalogOpened(ItemStack catalog){
		storedValue = ((IValueContainer) catalog.getItem()).getValue(catalog);
	}
	
	public PacketCatalogOpened(){
		storedValue = 5177;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		storedValue = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(storedValue);
	}
	
	public static class Handler implements IMessageHandler<PacketCatalogOpened,IMessage>{

		@Override
		public IMessage onMessage(PacketCatalogOpened message, MessageContext ctx) {
			//FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		/*
		private void handle(PacketCatalogOpened message, MessageContext ctx){
			LogHelper.info("A catalog with value "+message.storedValue+" has been opened!");
		}*/
	}

}
