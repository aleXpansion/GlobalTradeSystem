package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ValueManagerServer extends ValueManager {

	public Map<String,Map<String,ValueWrapper>> wrapperMap = new HashMap<String,Map<String,ValueWrapper>>();
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
		return new ValuesBean(wrapperMap);
	}

	public void addWrapper(ValueWrapper wrapper, String label,String category){
		Map<String,ValueWrapper> innerMap = wrapperMap.get(category);
		if(innerMap == null){
			innerMap = new HashMap<String,ValueWrapper>();
			wrapperMap.put(category, innerMap);
		}
		if(innerMap.containsValue(wrapper)){
			GTS.LOGGER.error("Attempted to add wrapper already in list");
			return;
		}
		innerMap.put(label,wrapper);
	}

	private void loadValues() {
		valuesLoaded = true;
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if (!data.areValuesLoaded()) {
			valuesLoaded = false;
			data.markDirty();
			return;
		} else {
			wrapperMap = data.getBean().getWrappers();
			totalValueSold = data.getTotal();
		}

		calculateValues();
	}

	public void calculateValues() {
		GTS.LOGGER.info("recalculating all values");
		for( Map<String,ValueWrapper> map : wrapperMap.values()){
			for( Entry<String,ValueWrapper> entry : map.entrySet()){
				ValueWrapper wrapper = entry.getValue();
				if(wrapper == null){
					map.remove(entry.getKey());
					GTS.LOGGER.info("null wrapper in ValueManagerServer#calculateValues");
				}else{
					wrapper.calculateValue(totalValueSold);
				}
			}
		}
	}

	@Override
	public boolean canISell(Item item) {
		return getBean().getWrappers("Item").containsKey(item.getRegistryName().toString());
	}


	public void addValueSold(ValueWrapper wrapper, float value, int amt, World world) {
		if(wrapper == null){
			return;
		}
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		Map<String, ValueWrapper> innerMap = wrapperMap.get(wrapper.getType());
		if(innerMap == null){
			innerMap = new HashMap<String, ValueWrapper>();
			wrapperMap.put(wrapper.getType(),innerMap);
		}
		if (!innerMap.containsValue(wrapper)) {
			innerMap.put(wrapper.getLabel(),wrapper);
		}
		wrapper.addSold(value, amt);;
		totalValueSold += value;
		wrapper.calculateValue(totalValueSold);
		data.saveBean(getBean());
		data.setTotal(totalValueSold);
		//calculateValues();
	}

	@Deprecated
	public void addValueSold(Item item,int amt, double value, World world) {
		addValueSold(getWrapper(item),(float)value,amt,world);
	}
	
	public void resetValues(){
		GTS.LOGGER.info("Resetting Values");
		BaseValueManager.initItemValues();
		wrapperMap.clear();
		wrapperMap.put("Item",BaseValueManager.wrapperMap);
		calculateValues();
	}

	public boolean isRemote(){
		return false;
	}

}
