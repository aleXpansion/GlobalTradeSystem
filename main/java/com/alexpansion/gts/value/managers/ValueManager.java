package com.alexpansion.gts.value.managers;

import java.util.ArrayList;
import java.util.Map;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.setup.RegistryHandler;
import com.alexpansion.gts.value.wrappers.ValueWrapper;
import com.alexpansion.gts.value.wrappers.ValueWrapperItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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

	public static ValueManagerServer getServerVM(){
		return serverInstance;
	}

	public static ValueManagerServer getVM(ServerWorld world){
		if (serverInstance != null) {
			return serverInstance;
		}
		else{
			serverInstance = new ValueManagerServer(world);
			return serverInstance;
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

	private float getValue(Item target) {
		if (!canISell(target)) {
			return 0.0F;
		} else if (!canIBuy(target)) {
			if(getWrapper(target) == null){
				return 0.0f;
			}
			return getWrapper(target).getBaseValue();
		} else {
			if(getWrapper(target) == null){
				return 0.0f;
			}
			return getWrapper(target).getValue();
		}
	}

	public ValueWrapperItem getWrapper(Item target){
		return (ValueWrapperItem)getWrapper("Item",target.getRegistryName().toString());
	}

	public ValueWrapper getWrapper(String target){
		ValueWrapper test = ValueWrapper.get(target,isRemote());
		return getWrapper(test.getType(), test.getLabel());
	}

	public ValueWrapper getWrapper(String category,String label){
		return getBean().getWrapper(category, label);
	}

	public Double getValue(ItemStack stack){
		if(stack.isEmpty()) return 0.0;
		double value = getValue(stack.getItem());
		
		if(stack.getItem() instanceof IValueContainer){
			IValueContainer item = (IValueContainer)stack.getItem();
			int held = item.getValue(stack,world);
			if(item == RegistryHandler.CREDIT.get()){
				return (double)held;
			}else{
				return value + held;
			}
		}

		if(value != 0){
			if (stack.isDamageable()){
				int max = stack.getMaxDamage();
				int left = max - stack.getDamage();
				value *= (double)left/(double)max;
			}
		}
		return value;
	}

	public int getAmtSold(Item target){
		ValueWrapperItem wrapper = getWrapper(target);
		if(wrapper == null){
			return 0;
		}else{
			return wrapper.getSoldAmt();
		}
	}

	public int getBaseValue(Item target) {
		ValueWrapperItem wrapper = getWrapper(target);
		if(wrapper == null){
			return 0;
		}else{
			return wrapper.getBaseValue();
		}
	}

	public abstract boolean canISell(Item item);

	public boolean canIBuy(Item item) {
		if (getBean() != null) {
			return getBean().wrapperMap.get("Item").containsKey(item.getRegistryName().toString());
		} else {
			return false;
		}
	}
	
	public ArrayList<Item> getAllSellableItems(){
		ArrayList<Item> newList = new ArrayList<Item>();
		for(ValueWrapper wrapper: getBean().getWrappers("Item").values()){
			if(wrapper.canSell()){
				newList.add(((ValueWrapperItem)wrapper).getItem());
			}
		}
		return newList;
	}

	public ArrayList<Item> getAllBuyableItems(){
		ArrayList<Item> newList = new ArrayList<Item>();
		Map<String,ValueWrapper> wrappers = getBean().getWrappers("Item");
		if(wrappers == null){
			return newList;
		}
		for(ValueWrapper wrapper: getBean().getWrappers("Item").values()){
			if(wrapper.canBuy()){
				newList.add(((ValueWrapperItem)wrapper).getItem());
			}
		}
		return newList;
	}
	
	public ArrayList<Item> getAllBuyableItems(int limit) {
		ArrayList<Item> items = getAllBuyableItems();
		ArrayList<Item> newItems = new ArrayList<Item>();
		for (Item item : items) {
			if(item == ItemStack.EMPTY.getItem()) continue;
			if (getValue(item) <= limit) {
				newItems.add(item);
			}
		}
		return newItems;
	}

	public ArrayList<Item> getBuyableItemsTargeted(Item target, int amt, int limit){
		ArrayList<Item> allItems = getAllBuyableItems(limit);
		allItems = sortItems(allItems);
		if(allItems.size() <= amt || !allItems.contains(target)){
			return allItems;
		}
		int targetIndex = allItems.indexOf(target);
		int startIndex = Math.max(0,targetIndex - amt/2);
		ArrayList<Item> outList = new ArrayList<Item>(allItems.subList(startIndex, allItems.size()));
		return outList;
	}

	public ArrayList<Item> getAllBuyableItemsSorted(int limit){
		ArrayList<Item> rawList = getAllBuyableItems(limit);
		return sortItems(rawList);
	}
	
	public ArrayList<Item> sortItems(ArrayList<Item> inList){
		ArrayList<Item> newList = new ArrayList<Item>();
		while(inList.size()>0){
			float top = (float) 0;
			Item topItem = null;
			ArrayList<Item> badList = new ArrayList<Item>();
			for(Item item:inList){
				float value = getValue(item);
				if(value == 0.0){
					badList.add(item);
					continue;
				}
				if(getValue(item)>top){
					top = getValue(item);
					topItem = item;
				}
			}
			inList.removeAll(badList);
			if(topItem == null){
				return newList;
			}
			newList.add(topItem);
			inList.remove(topItem);
		}
		return newList;
	}

	public abstract boolean isRemote();

	public void addValueSold(Item Item,int amt, double itemValue, World worldObj) {
	}

}
