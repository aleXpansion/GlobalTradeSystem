package com.alexpansion.gts.item;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.creativetab.CreativeTabGTS;
import com.alexpansion.gts.reference.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGTS extends Item implements ItemModelProvider{
	
	protected String name;

	public ItemGTS(String name) {
		super();
		this.name = name;
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabGTS.GTS_TAB);
		
		//ModItems.register(this);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", Reference.MOD_ID.toLowerCase() + ":",
				getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
	
	 
	@Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return String.format("item.%s%s", Reference.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

	@Override
	public void registerItemModel(Item item) {
		GlobalTradeSystem.proxy.registerItemRenderer(this,0,name);
	}
	
	
	
}
