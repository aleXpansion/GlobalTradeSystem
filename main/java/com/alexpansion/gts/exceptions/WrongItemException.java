package com.alexpansion.gts.exceptions;

import net.minecraft.item.Item;

public class WrongItemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3037872582893750168L;
	private Item givenItem;
	private Item targetItem;
	
	public WrongItemException(Item givenItem,Item targetItem) {
		this.givenItem = givenItem;
		this.targetItem = targetItem;
	}
	
	public Item getGivenItem(){
		return givenItem;
	}
	
	public Item getTargetItem(){
		return targetItem;
	}
	

}
