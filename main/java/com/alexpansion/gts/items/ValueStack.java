package com.alexpansion.gts.items;

import com.alexpansion.gts.GTS;

import net.minecraft.item.ItemStack;

public class ValueStack{

    private ItemStack stack;
    private IValueContainer item;

    public ValueStack(ItemStack stack){
        if(!(stack.getItem() instanceof IValueContainer)){
            GTS.LOGGER.error("Attempted to create ValueStack with stack of item "+stack.getItem());
            this.stack = null;
        }else{
            item= (IValueContainer) stack.getItem();
            this.stack = stack;
        }
    }

    public ItemStack getStack(){
        return stack;
    }

    public int getValue(){
        return item.getValue(stack);
    }
	
	public ItemStack addValue(int toAdd){
        int value = getValue();
        value += toAdd;
        return setValue(value);
    }
	
	public ItemStack removeValue(int toRemove){
        int value = getValue();
        value -= toRemove;
        return setValue(value);
    }

	public ItemStack setValue(int value){
        if(value > getLimit()){
            GTS.LOGGER.error("Attempted to set Value higher than limit.");
            value = getLimit();
        }else if(value < 0){
            GTS.LOGGER.error("Attempted to set value less than 0");
            value = 0;
        }
        return item.setValue(stack, value);
    }
	
	public int getLimit(){
        return item.getLimit();
    }
	
	public int getSpace(){
        return item.getSpace(stack);
    }
}