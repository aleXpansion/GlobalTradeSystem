package com.alexpansion.gts.exceptions;

import net.minecraft.item.ItemStack;

public class ValueOverflowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ItemStack stack;
	private int excessValue;

	public ValueOverflowException(ItemStack stack, int excessValue) {
		this.stack = stack;
		this.excessValue = excessValue;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getExcessValue() {
		return excessValue;
	}

	public String getMessage(){
		return "Overflow: A stack of "+stack.getCount()+ " "+stack.getDisplayName()+" plus a remaining value of"+excessValue;
	}
}
