package com.alexpansion.gts.utility;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SItem {
	
	private Item wrappedItem;
	
	public SItem(Item item){
		wrappedItem = item;
	}
	
	public SItem(String inString){
		this(GTSUtil.getItemFromRegistryName(inString));
	}
	
	public ItemStack getStack(int amount){
		return new ItemStack(wrappedItem,amount);
	}
	
	@Override
	public String toString(){
		return wrappedItem.getRegistryName().toString();
	}

}
