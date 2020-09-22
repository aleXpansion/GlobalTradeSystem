package com.alexpansion.gts.items;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IValueContainer {

	public int getValue(ItemStack stack, World world);
	
	public ItemStack setValue(ItemStack stack,int value, World world);
	
	public int getLimit(World world);
	
	public int getSpace(ItemStack stack, World world);
	
}
