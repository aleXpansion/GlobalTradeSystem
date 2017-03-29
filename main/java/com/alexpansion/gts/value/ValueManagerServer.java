package com.alexpansion.gts.value;

import java.util.HashMap;

import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.world.World;

public class ValueManagerServer extends ValueManager {

	public static HashMap<SItem, Integer> baseValueMap = new HashMap<SItem, Integer>();
	public HashMap<SItem, Integer> valueSoldMap = new HashMap<SItem, Integer>();
	public HashMap<SItem, Double> changeMap = new HashMap<SItem, Double>();
	public HashMap<SItem, Double> valueMap;
	public int totalValueSold = 0;
	private boolean valuesLoaded = false;
	private int calcCount = 0;
	private SItem toRemove = null;

	public ValueManagerServer(World inWorld) {
		super(inWorld);
		valueMap = new HashMap<SItem, Double>();
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
		ValueSavedData data = ValueSavedData.get(world);
		if (!data.areValuesLoaded()) {
			valuesLoaded = false;
			data.markDirty();
			return;
		} else {
			valueSoldMap = data.getValues();
			totalValueSold = data.getTotal();
		}

		// load the base values from GTSUtil
		// TODO move this logic here
		baseValueMap = BaseValueManager.baseValueMap;

		calculateValues();
	}

	public void calculateValues() {
		LogHelper.info("recalculating all values");
		for (SItem key : valueSoldMap.keySet()) {
			calculateValue(key);
		}
		if (toRemove != null) {
			valueSoldMap.remove(toRemove);
			toRemove = null;
		}
		calcCount = 0;
	}

	public void calculateValue(SItem item) {
		if (totalValueSold == 0) {
			totalValueSold = 1;
		}
		if (valueMap == null) {
			valueMap = new HashMap<SItem, Double>();
		}
		if (valueSoldMap.containsKey(item)) {
			int rampUp = ConfigurationHandler.rampUpCredits;
			double multiplier = ConfigurationHandler.depreciationMultiplier;
			// multiplier = (totalValueSold / 15000) + 1;
			multiplier = 1;
			rampUp = 10000;
			int valueSold = valueSoldMap.get(item);
			if (baseValueMap.get(item) == null) {
				toRemove = item;
				return;
			}
			double newValue = baseValueMap.get(item);
			double loss = 0;
			if (totalValueSold < rampUp) {
				newValue = ((rampUp - totalValueSold) / (double) rampUp) * newValue
						+ (totalValueSold / (double) rampUp) * ((totalValueSold - valueSold) / (totalValueSold));
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

	public void addValueSold(SItem item, int value, World world) {
		ValueSavedData data = ValueSavedData.get(world);
		if (!valueSoldMap.containsKey(item)) {
			valueSoldMap.put(item, value);
		} else {
			valueSoldMap.put(item, valueSoldMap.get(item) + value);
		}
		totalValueSold += value;
		LogHelper.info("Total value is now at " + totalValueSold);
		LogHelper.info("Added " + value + " value to " + item.getUnlocalizedName() + " for a total of "
				+ valueSoldMap.get(item) + ". "
				+ (int) Math.floor((double) valueSoldMap.get(item) / totalValueSold * 100)
				+ " percent of total sales.");

		calculateValue(item);
		data.saveValues(valueSoldMap);
		data.setTotal(totalValueSold);

	}

	public void addValueSold(SItem item, double value, World world) {
		if (changeMap.containsKey(item)) {
			value += changeMap.get(item);
		}
		if (value > 1 || value < -1) {
			addValueSold(item, (int) value, world);
		}
		changeMap.put(item, value % 1);
		if (calcCount++ > 5) {
			calculateValues();
		}
	}

}
