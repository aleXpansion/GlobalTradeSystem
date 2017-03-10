package com.alexpansion.gts.utility;

import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SItem {

	private Item wrappedItem;
	private int meta;
	private static ArrayList<SItem> list = new ArrayList<SItem>();

	public static SItem getSItem(Item item, int inMeta) {
		SItem target = new SItem(item, inMeta);
		for (SItem test : list) {
			if (test.equals(target)) {
				return test;
			}
		}
		return new SItem(item, inMeta);
	}

	@Deprecated
	public static SItem getSItem(Item item) {
		return getSItem(item, 0);
	}

	public static SItem getSItem(ItemStack stack) {
		return getSItem(stack.getItem(), stack.getMetadata());
	}

	public static SItem getSItem(String inString) {
		String[] splitString = inString.split("/");
		if (splitString.length >= 2) {
			Item item = GTSUtil.getItemFromRegistryName(splitString[0]);
			int meta = Integer.valueOf("0");
			return getSItem(item, meta);
		} else {
			LogHelper.error("GetSItem(string) got a string without metadata");
			return getSItem(GTSUtil.getItemFromRegistryName(splitString[0]));
		}
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
		return wrappedItem == target.wrappedItem && meta == target.meta;
	}

	public boolean equals(ItemStack stack) {
		return this == SItem.getSItem(stack);
	}

	public String getDisplayName() {
		return getStack(1).getDisplayName();
	}

}
