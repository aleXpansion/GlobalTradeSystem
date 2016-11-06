package com.alexpansion.gts.item;

import java.util.List;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemCreditCard extends ItemGTS implements IValueContainer{
	
	private int limit;
	private static final String VALUE_KEY = "value";
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4){
		list.add("Value: "+getValue(stack));
	}

	public ItemCreditCard(String name,int limit){
		super(name);
		this.setMaxStackSize(1);
		this.setMaxDamage(1000);
		this.limit = limit;
	}
	
	public ItemCreditCard(){
		this("credit_card",1000);
	}
	
	public ItemStack addValue(ItemStack stack,int toAdd)throws ValueOverflowException{
		if(stack.getItem()!= this){
			LogHelper.error("W01");
			return null;
		}
		int value = getValue(stack);
		if(value+toAdd>limit){
			toAdd -= (limit-value);
			value = limit;
			stack.getTagCompound().setInteger(VALUE_KEY, limit);
			updateDisplay(stack);
			throw new ValueOverflowException(stack,toAdd);
		}else{
			stack.getTagCompound().setInteger(VALUE_KEY, value+toAdd);
		}
		updateDisplay(stack);
		return stack;
	}
	
	public int getValue(ItemStack stack){
		if(stack.getItem()!= this){
			LogHelper.error("W02");
			return 0;
		}
		if(stack.getTagCompound() == null){
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound().getInteger(VALUE_KEY);
		
	}

	@Override
	public ItemStack removeValue(ItemStack stack, int toRemove) throws ValueOverflowException {
		int value = getValue(stack);
		if(stack.getItem()!= this){
			LogHelper.error("W03");
			return null;
		}if(toRemove>value){
			stack.getTagCompound().setInteger(VALUE_KEY, 0);
			throw new ValueOverflowException(stack,toRemove-getValue(stack));
		}
		stack.getTagCompound().setInteger(VALUE_KEY, value-toRemove);
		updateDisplay(stack);
		return stack;
	}
	
	private void updateDisplay(ItemStack stack){
		stack.setItemDamage(stack.getMaxDamage()-((int) ((getValue(stack)/(double)limit) * stack.getMaxDamage())));
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
