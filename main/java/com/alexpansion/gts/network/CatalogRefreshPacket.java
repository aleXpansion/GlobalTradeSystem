package com.alexpansion.gts.network;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.items.Catalog.CatalogContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CatalogRefreshPacket{

	private ArrayList<ItemStack> stacks;
	
	
	public CatalogRefreshPacket(ArrayList<ItemStack> inStacks){
		stacks = inStacks;
	}
	
	
	public CatalogRefreshPacket(PacketBuffer buf) {
		stacks = new ArrayList<ItemStack>();
		int size = buf.readInt();
		ItemStack stack;
		for(int i =0;i<size;i++){
			stack = buf.readItemStack();
			stacks.add(stack);
		}
	}
	
	public ArrayList<ItemStack> getStackes(){
		return stacks;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(stacks.size());
		for(ItemStack stack : stacks){
			buf.writeItemStack(stack);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Context get = ctx.get();
        get.enqueueWork(() -> {
			Minecraft instance = Minecraft.getInstance();
			Container open = instance.player.openContainer;
			if(open instanceof CatalogContainer){
				CatalogContainer cont = (CatalogContainer)open;
				cont.refresh(stacks);
			}else{
				GTS.LOGGER.info("Skipping refresh catalog per not open");
			}
		});
        ctx.get().setPacketHandled(true);
    }

}
