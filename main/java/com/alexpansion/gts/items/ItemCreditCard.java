package com.alexpansion.gts.items;

import com.alexpansion.gts.GTS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

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

	@Override
	public ItemStack setValue(ItemStack stack, int value, World world) {
		stack.getTag().putInt(VALUE_KEY, value);
		updateDisplay(stack, world);
		return stack;
	}
	
	public ItemStack addValue(ItemStack stack,int toAdd, World world){
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W01");
			return null;
		}
		int value = getValue(stack,world);
		if(value+toAdd>limit){
			toAdd -= (limit-value);
			value = limit;
			stack.getTag().putInt(VALUE_KEY, limit);
			GTS.LOGGER.error("attempted to add "+toAdd+" to card with room for "+(limit-value)+".");
		}else{
			stack.getTag().putInt(VALUE_KEY, value+toAdd);
		}
		updateDisplay(stack, world);
		return stack;
	}
	
	public int getValue(ItemStack stack, World world){
		if(stack.getItem()!= this){
			GTS.LOGGER.error("W02");
			return 0;
		}
		if(stack.getTag() == null){
			stack.setTag(new CompoundNBT());
		}
		return stack.getTag().getInt(VALUE_KEY);
		
	}
	
	private void updateDisplay(ItemStack stack, World world){
		stack.setDamage(stack.getMaxDamage()-((int) ((getValue(stack,world)/(double)limit) * stack.getMaxDamage())));
	}

	@Override
	public int getLimit(ItemStack stack,World world) {
		return limit;
	}

	@Override
	public int getSpace(ItemStack stack, World world) {
		return limit - getValue(stack,world);
	}
	
}
