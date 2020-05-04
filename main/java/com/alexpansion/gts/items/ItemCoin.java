package com.alexpansion.gts.items;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.exceptions.ValueOverflowException;

import net.minecraft.item.ItemStack;

public class ItemCoin extends ItemBase implements IValueContainer{

	public ItemCoin() {
		super();
	}

	@Override
	public int getValue(ItemStack stack) {
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W04");
			return 0;
		}
		return stack.getCount();
	}

	@Override
	public ItemStack addValue(ItemStack stack, int toAdd) throws ValueOverflowException {
		if(stack == null || stack.getItem()!= this){
			GTS.LOGGER.error("W05");
			return null;
		}
		int space = stack.getMaxStackSize()-stack.getCount();
		if(toAdd>space){
			toAdd -= space;
			stack.setCount(stack.getMaxStackSize());
			throw new ValueOverflowException(stack,toAdd);
		}else{
			stack.setCount(stack.getCount() + toAdd);
		}
		return stack;
		
	}

	@Override
	public ItemStack removeValue(ItemStack stack, int toRemove) throws ValueOverflowException {
		if(stack == null ||stack.getItem()!= this){
			GTS.LOGGER.error("W06");
			return null;
		}
		if(toRemove > stack.getCount()){
			toRemove -= stack.getCount();
			stack.setCount(0); 
			throw new ValueOverflowException(stack,toRemove);
		}else if(toRemove == stack.getCount()){
			stack = ItemStack.EMPTY;
			return stack;
		}else{
			stack.setCount(stack.getCount()-toRemove);
			return stack;
		}
		
	}

	@Override
	public int getLimit() {
		return 64;
	}

	@Override
	public int getSpace(ItemStack stack) {
		return 64-stack.getCount();
	}


}
