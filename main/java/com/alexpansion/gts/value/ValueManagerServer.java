package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.HashMap;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.tools.JEIloader;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ValueManagerServer extends ValueManager {

	public HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	public HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	public HashMap<Item, Double> changeMap = new HashMap<Item, Double>();
	public HashMap<Item, Double> valueMap;
	public int totalValueSold = 0;
	private boolean valuesLoaded = false;
	private int calcCount = 0;
	private Item toRemove = null;
	private ArrayList<Item> nonBuyable = new ArrayList<Item>();

	public ValueManagerServer(World world) {
		super(world);
		if(world.isRemote){
			GTS.LOGGER.error("Client world sent to ValueManagerServer.");
		}
		valueMap = new HashMap<Item, Double>();
		loadValues();
	}

	@Override
	public ValuesBean getBean() {
		if (!valuesLoaded) {
			loadValues(); 
		}
		return new ValuesBean(baseValueMap, valueMap);
	}

	private void loadValues() {
		valuesLoaded = true;
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if (!data.areValuesLoaded()) {
			valuesLoaded = false;
			data.markDirty();
			return;
		} else {
			valueSoldMap = data.getValues();
			totalValueSold = data.getTotal();
		}

		baseValueMap = data.getBaseValues();

		calculateValues();
	}

	public void calculateValues() {
		GTS.LOGGER.info("recalculating all values");
		for (Item key : valueSoldMap.keySet()) {
			calculateValue(key);
		}
		if (toRemove != null) {
			valueSoldMap.remove(toRemove);
			toRemove = null;
		}
		calcCount = 0;
	}

	public void calculateValue(Item item) {
		if (totalValueSold == 0) {
			totalValueSold = 1;
		}
		if (valueMap == null) {
			valueMap = new HashMap<Item, Double>();
		}
		if (valueSoldMap.containsKey(item)) {
			//int rampUp = ConfigurationHandler.rampUpCredits;
			//double multiplier = ConfigurationHandler.depreciationMultiplier;
			// multiplier = (totalValueSold / 15000) + 1;
			double multiplier = 1;
			int rampUp = 120;
			int valueSold = valueSoldMap.get(item);
			if (baseValueMap.get(item) == null && getCrafingValue(item) == 0) {
				toRemove = item;
				return;
			}
			double baseValue = baseValueMap.get(item);
			double newValue = baseValue;
			double loss = 0;
			if (totalValueSold < rampUp) {
				newValue = ((rampUp - totalValueSold) / (double) rampUp) * baseValue
						+ (totalValueSold / (double) rampUp) * ((totalValueSold - valueSold) / (double)(totalValueSold));
			} else {
				loss = newValue * ((valueSold) / ((double) totalValueSold)) * multiplier;
				newValue -= loss;
			}
			// LogHelper.info(item.getUnlocalizedName() + " is worth " +
			// newValue);
			valueMap.put(item, newValue);
		} /*else if (baseValueMap.containsKey(item)) {
			int base = baseValueMap.get(item);
			valueMap.put(item, (double) base);
		}*/else{
			valueMap.remove(item);
			return;
		}
	}

	@Override
	public boolean canISell(Item item) {
		//If we have a base value for it, return true
		if(baseValueMap.containsKey(item)){
			return true;
		}
		//if it's been cached as unbuyable, return false
		if(nonBuyable.contains(item)){
			return false;
		}
		//If JEI is installed and loaded, use it to calculate the value
		return getCrafingValue(item) != 0;
	}

	private int getCrafingValue(Item item){
		if(JEIloader.isLoaded()){
			int value = JEIloader.getCrafingValue(this,item);
			if(value <= 0){
				nonBuyable.add(item);
				return 0;
			}else{
				setBaseValue(item, value);
				return value;
			}
		}
		return 0;
	}

	public void addValueSold(Item item, int value, World world) {
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if (!valueSoldMap.containsKey(item)) {
			valueSoldMap.put(item, value);
		} else {
			valueSoldMap.put(item, valueSoldMap.get(item) + value);
		}
		totalValueSold += value;
		calculateValue(item);
		data.saveValues(valueSoldMap);
		data.setTotal(totalValueSold);

	}

	public void addValueSold(Item item, double value, World world) {
		if (changeMap.containsKey(item)) {
			value += changeMap.get(item);
		}
		if (value >= 1 || value <= -1) {
			addValueSold(item, (int) value, world);
		}
		changeMap.put(item, value % 1);
		if (calcCount++ > 5) {
			calculateValues();
		}
	}

	//sets the base value of the given item. If value is 0 or less, removes base value for that item.
	public void setBaseValue(Item key, int value){
		ValueSavedData data = ValueSavedData.get((ServerWorld) world);
		if(value > 0){
			baseValueMap.put(key, value);
			calculateValue(key);
		}else if(baseValueMap.containsKey(key)){
			baseValueMap.remove(key);
		}
		data.setBaseValue(key,value);
	}

	
	@SuppressWarnings("unchecked")
	public void resetValues(){
		GTS.LOGGER.info("Resetting Values");
		BaseValueManager.initItemValues();
		nonBuyable.clear();
		baseValueMap = (HashMap<Item, Integer>) BaseValueManager.baseValueMap.clone();
		calculateValues();
	}

}
