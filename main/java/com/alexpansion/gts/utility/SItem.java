package com.alexpansion.gts.utility;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SItem {
	
	private Item wrappedItem;
	private static HashMap<Item,SItem> map = new HashMap<Item,SItem>();
	
	public static SItem getSItem(Item item){
		if(map.containsKey(item)){
			return map.get(item);
		}else{
			return new SItem(item);
		}
	}
	
	public static SItem getSItem(String inString){
		return getSItem(GTSUtil.getItemFromRegistryName(inString));
	}
	
	private SItem(Item item){
		wrappedItem = item;
		if(wrappedItem == null){
			LogHelper.error("Null item in SItem.<init>");
		}else{
			map.put(item, this);
			
		}
	}
	
	public ItemStack getStack(int amount){
		return new ItemStack(wrappedItem,amount);
	}
	
	@Override
	public String toString(){
		return wrappedItem.getRegistryName().toString();
	}

}
