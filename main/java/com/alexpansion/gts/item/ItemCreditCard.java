package com.alexpansion.gts.item;

public class ItemCreditCard extends ItemGTS{

	public ItemCreditCard(String name,int limit){
		super(name);
		this.setMaxStackSize(1);
		this.setMaxDamage(limit);
	}
	
	public ItemCreditCard(){
		this("credit_card",1000);
	}
	
}
