package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alexpansion.gts.GTS;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ValueManagerServer extends ValueManager {

	public List<ValueWrapper> wrapperList = new ArrayList<ValueWrapper>();
	public Map<String,List<ValueWrapper>> wrappersMap = new HashMap<String,List<ValueWrapper>>();
	public int totalValueSold = 0;
	private boolean valuesLoaded = false;

	public ValueManagerServer(World world) {
		super(world);
		if(world.isRemote){
			GTS.LOGGER.error("Client world sent to ValueManagerServer.");
		}
		loadValues();
	}

	@Override
	public ValuesBean getBean() {
		if (!valuesLoaded) {
			loadValues(); 
		}
		return new ValuesBean(wrappersMap);
	}

	public void addWrapper(ValueWrapper wrapper, String label){
		if(wrapperList.contains(wrapper)){
			GTS.LOGGER.error("Attempted to add wrapper already in list");
			return;
		}
		wrapperList.add(wrapper);
		wrappersMap.get(label).add(wrapper);
		if(wrapper instanceof ValueWrapperItem){
			itemMap.put(((ValueWrapperItem)wrapper).getItem(), (ValueWrapperItem)wrapper);
		}
	}

	private void loadValues() {
		valuesLoaded = true;
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if (!data.areValuesLoaded()) {
			valuesLoaded = false;
			data.markDirty();
			return;
		} else {
			wrapperList = data.getWrappers();
			for(ValueWrapper wrapper :wrapperList){
				if(wrapper instanceof ValueWrapperItem){
					itemMap.put(((ValueWrapperItem)wrapper).getItem(), (ValueWrapperItem)wrapper);
				}
			}
			totalValueSold = data.getTotal();
			wrappersMap.put("item", new ArrayList<ValueWrapper>(itemMap.values()));
		}

		calculateValues();
	}

	public void calculateValues() {
		GTS.LOGGER.info("recalculating all values");
		for( ValueWrapper wrapper : wrapperList){
			wrapper.calculateValue(totalValueSold);
		}
	}

	@Override
	public boolean canISell(Item item) {
		return itemMap.containsKey(item);
	}


	public void addValueSold(ValueWrapper wrapper, float value, int amt, World world) {
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if (!wrapperList.contains(wrapper)) {
			wrapperList.add(wrapper);
		}
		wrapper.addSold(value, amt);;
		totalValueSold += value;
		wrapper.calculateValue(totalValueSold);
		data.saveWrapper(wrapper);
		data.setTotal(totalValueSold);
		calculateValues();
	}

	@Deprecated
	public void addValueSold(Item item,int amt, double value, World world) {
		addValueSold(getWrapper(item),(float)value,amt,world);
	}
	
	public void resetValues(){
		GTS.LOGGER.info("Resetting Values");
		BaseValueManager.initItemValues();
		wrapperList = (ArrayList<ValueWrapper>) BaseValueManager.wrapperList;
		itemMap.clear();
		for(ValueWrapper wrapper :wrapperList){
			if(wrapper instanceof ValueWrapperItem){
				itemMap.put(((ValueWrapperItem)wrapper).getItem(), (ValueWrapperItem)wrapper);
			}
		}
		calculateValues();
	}

	public boolean isRemote(){
		return false;
	}

}
