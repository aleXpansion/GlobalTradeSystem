package com.alexpansion.gts.value;

import java.util.ArrayList;

import com.alexpansion.gts.utility.GTSUtil;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SItem {

	private final Item wrappedItem;
	private final int meta;
	private static ArrayList<SItem> list = new ArrayList<SItem>();

	@SuppressWarnings("unchecked")
	public static SItem getSItem(Item item, int inMeta) {
		if (item == null) {
			return null;
		}
		SItem target = new SItem(item, inMeta);
		ArrayList<SItem> listCopy;
		synchronized (list) {
			listCopy = (ArrayList<SItem>) list.clone();
		}

		for (SItem test : listCopy) {
			if (test.equals(target)) {
				return test;
			}
		}
		return new SItem(item, inMeta);
	}

	
	public static SItem getSItem(ItemStack stack) {
		return getSItem(stack.getItem(), stack.getMetadata());
	}

	public static SItem getSItem(String inString) {
		String[] splitString = inString.split("/");
		if (splitString.length >= 2) {
			Item item = GTSUtil.getItemFromRegistryName(splitString[0]);
			String metaString = "0";
			if(splitString.length >1){
				metaString = splitString[1];
			}
			int meta = Integer.valueOf(metaString);
			return getSItem(item, meta);
		} else {
			LogHelper.error("GetSItem(string) got a string without metadata. String: "+inString);
			return getSItem(GTSUtil.getItemFromRegistryName(splitString[0]),0);
		}
	}
	
	public static SItem getSItem(String registryName,int meta){
			Item item = GTSUtil.getItemFromRegistryName(registryName);
			return getSItem(item, meta);
	}

	private SItem(Item item, int inMeta) {
		wrappedItem = item;
		meta = inMeta;
		if (wrappedItem == null) {
			LogHelper.error("Null item in SItem.<init>");
		} else {
			list.add(this);
		}
	}

	public ItemStack getStack(int amount) {
		return new ItemStack(wrappedItem, amount, meta);
	}

	@Override
	public String toString() {
		return wrappedItem.getRegistryName().toString() + "/" + meta;
	}

	public Item getWrappedItem() {
		return wrappedItem;
	}

	public String getUnlocalizedName() {
		return wrappedItem.getUnlocalizedName();
	}

	public boolean equals(SItem target) {
		return (wrappedItem == target.wrappedItem && meta == target.meta);
	}

	public boolean equals(ItemStack stack) {
		return this == SItem.getSItem(stack);
	}

	public String getDisplayName() {
		return getStack(1).getDisplayName();
	}

}
