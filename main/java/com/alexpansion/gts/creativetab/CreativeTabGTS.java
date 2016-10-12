package com.alexpansion.gts.creativetab;

import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.reference.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabGTS {

	
	public static final CreativeTabs GTS_TAB = new CreativeTabs(Reference.MOD_ID){
		
		@Override
		public Item getTabIconItem(){
			return ModItems.CATALOG;
		}
	};
}
