package com.alexpansion.gts.items;

import com.alexpansion.gts.GTS;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCoin extends ItemBase implements IValueContainer{

	public ItemCoin() {
		super();
	}

	@Override
	public ItemStack setValue(ItemStack stack, int value, World world) {
		if(value > stack.getMaxStackSize()){
			GTS.LOGGER.error("Attempted to set coin value to "+value+". Max is "+stack.getMaxStackSize()+".");
			stack.setCount(stack.getMaxStackSize());
		}else if(value < 0){
			GTS.LOGGER.error("Attempted to set coin value less than 0");
			stack = ItemStack.EMPTY;
		}else{
			stack.setCount(value);
		}
		return stack;
	}

	@Override
	public int getValue(ItemStack stack, World world) {
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W04");
			return 0;
		}
		return stack.getCount();
	}

	@Override
	public int getLimit(ItemStack stack,World world) {
		return 64;
	}

	@Override
	public int getSpace(ItemStack stack, World world) {
		return 64-stack.getCount();
	}


}
