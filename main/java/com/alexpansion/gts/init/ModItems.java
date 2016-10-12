package com.alexpansion.gts.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.alexpansion.gts.item.*;
import com.alexpansion.gts.reference.Reference;

//import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModItems {

	private static final List<ItemGTS> ITEMS = new ArrayList<ItemGTS>();
	
	public static final ItemGTS CATALOG = new ItemCatalog();
	public static final ItemGTS CREDIT = new ItemCoin();
	public static final ItemGTS CREDIT_CARD = new ItemCreditCard();
	
	
	public static void init(){
		
		GameRegistry.register(CATALOG);
		GameRegistry.registerItem(CREDIT, "gts:credit");
		GameRegistry.registerItem(CREDIT_CARD, "gts:creditCard");
	}
	
	public static Collection<ItemGTS> getItems(){
		return ITEMS;
	}
	
	public static void register(ItemGTS item){
		ITEMS.add(item);
	}
	
}
