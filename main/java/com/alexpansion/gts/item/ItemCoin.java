package com.alexpansion.gts.item;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.item.ItemStack;

public class ItemCoin extends ItemGTS implements IValueContainer{

	public ItemCoin() {
		super("credit");
	}

	@Override
	public int getValue(ItemStack stack) {
		if(stack.getItem()!= this){
			LogHelper.error("W04");
			return 0;
		}
		return stack.stackSize;
	}

	@Override
	public ItemStack addValue(ItemStack stack, int toAdd) throws ValueOverflowException {
		if(stack == null || stack.getItem()!= this){
			LogHelper.error("W05");
			return null;
		}
		int space = stack.getMaxStackSize()-stack.stackSize;
		if(toAdd>space){
			toAdd -= space;
			stack.stackSize = stack.getMaxStackSize();
			throw new ValueOverflowException(stack,toAdd);
		}else{
			stack.stackSize += toAdd;
		}
		return stack;
		
	}

	@Override
	public ItemStack removeValue(ItemStack stack, int toRemove) throws ValueOverflowException {
		if(stack == null ||stack.getItem()!= this){
			LogHelper.error("W06");
			return null;
		}
		if(toRemove > stack.stackSize){
			toRemove -= stack.stackSize;
			stack.stackSize = 0;
			throw new ValueOverflowException(stack,toRemove);
		}else if(toRemove == stack.stackSize){
			stack = null;
			return null;
		}else{
			stack.stackSize -= toRemove;
			return stack;
		}
		
	}

	@Override
	public int getLimit() {
		return 64;
	}

	@Override
	public int getSpace(ItemStack stack) {
		return 64-stack.stackSize;
	}


}
