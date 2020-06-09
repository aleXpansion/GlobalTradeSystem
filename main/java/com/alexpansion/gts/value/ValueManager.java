package com.alexpansion.gts.value;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ValueManager {


	protected World world;
	private static ValueManagerClient clientInstance;
	private static ValueManagerServer serverInstance;

	public ValueManager(World world) {
		this.world = world;
	}

	public static ValueManagerClient getClientVM(){
		if(clientInstance != null){
			return clientInstance;
		}else{
			GTS.LOGGER.error("Client instance not ready.");
			return null;
		}
	}
	
	public static ValueManager getVM(World world) {

		if (world.isRemote()) {
			if (clientInstance != null) {
				return clientInstance;
			}else{
				clientInstance = new ValueManagerClient(world);
				return clientInstance;
			}
		} else {
			if (serverInstance != null) {
				return serverInstance;
			}
			else{
				
				serverInstance = new ValueManagerServer(world);
				return serverInstance;
			}
		}

	}

	
	public abstract ValuesBean getBean();

	public Double getValue(Item target) {
		if (!canISell(target)) {
			return (double) 0.0;
		} else if (!canIBuy(target)) {
			return (double) getBaseValue(target);
		} else {
			return getBean().getValueMap().get(target);
		}
	}

	public int getBaseValue(Item target) {
		if( !canISell(target)){
			return 0;
		}else{
			return getBean().getBaseMap().get(target);
		}
	}

	public boolean canISell(Item item) {
		//if we haven't loaded values yet, return false for now
		if(getBean() == null){
			return false;
		}
		if(getBean().getBaseMap().containsKey(item)){
			return true;
		}else{
			if(getBean().getValueMap().containsKey(item) ){
				//GTS.LOGGER.info(item.toString()+" has a value but no base value");
			}
			return false;
		}
		//return getBean().getBaseMap().containsKey(item);
	}

	public boolean canIBuy(Item item) {
		if (getBean() != null) {
			return getBean().getValueMap().containsKey(item);
		} else {
			return false;
		}
	}
	
	public ArrayList<Item> getAllSellableItems(){
		return new ArrayList<Item>(getBean().getBaseMap().keySet());
	}

	public ArrayList<Item> getAllBuyableItems(){
		return new ArrayList<Item>(getBean().getValueMap().keySet());
	}
	
	public ArrayList<Item> getAllBuyableItems(int limit) {
		ArrayList<Item> items = getAllBuyableItems();
		ArrayList<Item> newItems = new ArrayList<Item>();
		for (Item item : items) {
			if (getValue(item) <= limit) {
				newItems.add(item);
			}
		}
		return newItems;
	}
	
	public ArrayList<Item> getAllBuyableItemsSorted(int limit){
		ArrayList<Item> oldList = getAllBuyableItems(limit);
		ArrayList<Item> newList = new ArrayList<Item>();
		while(oldList.size()>0){
			Double top = (double) 0;
			Item topItem = null;
			ArrayList<Item> badList = new ArrayList<Item>();
			for(Item item:oldList){
				Double value = getValue(item);
				if(value == 0.0){
					badList.add(item);
					continue;
				}
				if(getValue(item)>top){
					top = getValue(item);
					topItem = item;
				}
			}
			oldList.removeAll(badList);
			if(topItem == null){
				GTS.LOGGER.error("topItem was null in ValueManager.getAllBuyableItems");
				return newList;
			}
			newList.add(topItem);
			oldList.remove(topItem);
		}
		return newList;
	}

	public void addValueSold(Item Item, double itemValue, World worldObj) {
	}

	public double getValue(ItemStack stack){
		return getValue(stack.getItem());
	}

}
