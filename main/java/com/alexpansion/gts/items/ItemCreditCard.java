package com.alexpansion.gts.items;


import com.alexpansion.gts.GTS;
import com.alexpansion.gts.exceptions.ValueOverflowException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemCreditCard extends ItemBase implements IValueContainer{
	
	private int limit;
	private static final String VALUE_KEY = "value";
	
	//This method is supposed to add information to the tooltip. Just needs to be updated.
	/*
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add("Value: "+getValue(stack)+"/"+((ItemCreditCard) stack.getItem()).getLimit());
	}*/

	public ItemCreditCard(int limit){
		super(new Item.Properties()
			.maxStackSize(1)
			.maxDamage(1000));
		this.limit = limit;
	}
	
	public ItemCreditCard(){
		this(1000);
	}
	
	public ItemStack addValue(ItemStack stack,int toAdd)throws ValueOverflowException{
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W01");
			return null;
		}
		int value = getValue(stack);
		if(value+toAdd>limit){
			toAdd -= (limit-value);
			value = limit;
			stack.getTag().putInt(VALUE_KEY, limit);
			updateDisplay(stack);
			throw new ValueOverflowException(stack,toAdd);
		}else{
			stack.getTag().putInt(VALUE_KEY, value+toAdd);
		}
		updateDisplay(stack);
		return stack;
	}
	
	public int getValue(ItemStack stack){
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W02");
			return 0;
		}
		if(stack.getTag() == null){
			stack.setTag(new CompoundNBT());
		}
		return stack.getTag().getInt(VALUE_KEY);
		
	}

	@Override
	public ItemStack removeValue(ItemStack stack, int toRemove) throws ValueOverflowException {
		int value = getValue(stack);
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W03");
			return null;
		}if(toRemove>value){
			stack.getTag().putInt(VALUE_KEY, 0);
			throw new ValueOverflowException(stack,toRemove-getValue(stack));
		}
		stack.getTag().putInt(VALUE_KEY, value-toRemove);
		updateDisplay(stack);
		return stack;
	}
	
	private void updateDisplay(ItemStack stack){
		stack.setDamage(stack.getMaxDamage()-((int) ((getValue(stack)/(double)limit) * stack.getMaxDamage())));
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public int getSpace(ItemStack stack) {
		return limit - getValue(stack);
	}
	
}
