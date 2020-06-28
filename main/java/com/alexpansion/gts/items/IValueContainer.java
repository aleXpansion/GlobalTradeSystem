package com.alexpansion.gts.items;

import net.minecraft.item.ItemStack;

public interface IValueContainer {

	public int getValue(ItemStack stack);
	
	public ItemStack setValue(ItemStack stack,int value);
	
	public int getLimit();
	
	public int getSpace(ItemStack stack);
	
}
