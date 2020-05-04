package com.alexpansion.gts.items;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import net.minecraft.item.ItemStack;

public interface IValueContainer {

	public int getValue(ItemStack stack);
	
	public ItemStack addValue(ItemStack stack,int toAdd)throws ValueOverflowException;
	
	public ItemStack removeValue(ItemStack stack,int toRemove)throws ValueOverflowException;
	
	public int getLimit();
	
	public int getSpace(ItemStack stack);
	
}
