package com.alexpansion.gts.value;

import java.util.HashMap;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ValueManagerServer extends ValueManager {

	public static HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	public HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	public HashMap<Item, Double> changeMap = new HashMap<Item, Double>();
	public HashMap<Item, Double> valueMap;
	public int totalValueSold = 0;
	private boolean valuesLoaded = false;
	private int calcCount = 0;
	private Item toRemove = null;

	public ValueManagerServer(World world) {
		super(world);
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

		baseValueMap = BaseValueManager.baseValueMap;

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
			if (baseValueMap.get(item) == null) {
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
		} else if (baseValueMap.containsKey(item)) {
			int base = baseValueMap.get(item);
			valueMap.put(item, (double) base);
		}
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

}
