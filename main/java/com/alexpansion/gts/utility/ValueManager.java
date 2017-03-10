package com.alexpansion.gts.utility;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ValueManager {

	protected ValuesBean bean;
	protected World world;
	private static ValueManager clientInstance;
	private static ValueManager serverInstance;

	public ValueManager(World inWorld) {
		world = inWorld;
		bean = getBean();
	}

	public static ValueManager getManager(World inWorld) {

		if (inWorld.isRemote) {
			if (clientInstance != null && clientInstance.world != null && clientInstance.world.equals(inWorld)) {
				return clientInstance;
			}else{
				clientInstance = new ValueManagerClient(inWorld);
				return clientInstance;
			}
		} else {
			if (serverInstance != null && serverInstance.world != null && serverInstance.world.equals(inWorld)) {
				return serverInstance;
			}
			else{
				serverInstance = new ValueManagerServer(inWorld);
				return serverInstance;
			}
		}

	}

	public abstract ValuesBean getBean();

	public void setBean(ValuesBean inBean) {
		bean = inBean;
	}

	public Double getValue(SItem target) {
		if (!canISell(target)) {
			return (double) 0;
		} else if (!canIBuy(target)) {
			return (double) getBaseValue(target);
		} else {
			return getBean().getValueMap().get(target);
		}
	}

	public int getBaseValue(SItem target) {
		return getBean().getBaseMap().get(target);
	}

	public boolean canISell(SItem item) {
		if (getBean() != null) {
			return getBean().getBaseMap().containsKey(item);
		} else {
			return false;
		}
	}

	public boolean canIBuy(SItem item) {
		if (bean != null) {
			return getBean().getValueMap().containsKey(item);
		} else {
			return false;
		}
	}
	
	public ArrayList<SItem> getAllSellableItems(){
		return new ArrayList<SItem>(getBean().getBaseMap().keySet());
	}

	public ArrayList<SItem> getAllBuyableItems(){
		return new ArrayList<SItem>(getBean().getValueMap().keySet());
	}
	
	public ArrayList<SItem> getAllBuyableItems(int limit) {
		ArrayList<SItem> items = getAllBuyableItems();
		ArrayList<SItem> newItems = new ArrayList<SItem>();
		for (SItem item : items) {
			if (getValue(item) <= limit) {
				newItems.add(item);
			}
		}
		return newItems;
	}
	
	public ArrayList<SItem> getAllBuyableItemsSorted(int limit){
		ArrayList<SItem> oldList = getAllBuyableItems(limit);
		ArrayList<SItem> newList = new ArrayList<SItem>();
		while(oldList.size()>0){
			Double top = (double) 0;
			SItem topItem = null;
			for(SItem item:oldList){
				if(getValue(item)>top){
					top = getValue(item);
					topItem = item;
				}
			}
			if(topItem == null){
				LogHelper.error("topItem was null in ValueManager.getAllSellableItems");
				return newList;
			}
			newList.add(topItem);
			oldList.remove(topItem);
		}
		return newList;
	}
	
	
	// TODO remove these and properly migrate to SItem
	@Deprecated
	public double getValue(Item target) {
		return getValue(SItem.getSItem(target));
	}

	@Deprecated
	public int getBaseValue(Item target) {
		return getBaseValue(SItem.getSItem(target));
	}

	@Deprecated
	public boolean canISell(Item item) {
		return canISell(SItem.getSItem(item));
	}

	@Deprecated
	public boolean canIBuy(Item item) {
		return canIBuy(SItem.getSItem(item));
	}

	public void addValueSold(SItem sItem, double itemValue, World worldObj) {}
	
	public double getValue(ItemStack stack){
		return getValue(SItem.getSItem(stack));
	}

}
