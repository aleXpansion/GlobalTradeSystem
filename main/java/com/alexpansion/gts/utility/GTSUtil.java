package com.alexpansion.gts.utility;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.handler.ConfigurationHandler;

import net.minecraft.item.Item;

public class GTSUtil {

	public static HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	public static HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	public static int totalValueSold = 0;

	public static boolean canISell(Item item) {
		return baseValueMap.containsKey(item);

	}

	public static double getValue(Item item) {
		if (valueSoldMap.containsKey(item)) {
			int rampUp = ConfigurationHandler.rampUpCredits;
			double multiplier = ConfigurationHandler.depreciationMultiplier;
			multiplier = (totalValueSold/15000)+1;
			rampUp = 10000;
			int valueSold = valueSoldMap.get(item);
			double newValue = baseValueMap.get(item);
			double loss = 0;
			if (totalValueSold < rampUp) {
				newValue = ((rampUp - totalValueSold) / (double)rampUp) * newValue
						+ (totalValueSold / (double)rampUp) * ((totalValueSold - valueSold) / (totalValueSold));
			} else {
				loss = newValue * ((valueSold) / ((double)totalValueSold)) * multiplier;
				newValue -= loss;
			}
			//LogHelper.info(item.getUnlocalizedName() + " is worth " + newValue);
			return newValue;
		} else if (baseValueMap.containsKey(item)) {
			return baseValueMap.get(item);
		} else {
			return 0;
		}
	}

	public static void addSellableItem(Item item, int value) {
		baseValueMap.put(item, value);
	}

	public static void initItemValues() {
		addSellableItemById(3, 1);
		addSellableItemById(4, 1);
		addSellableItemById(5, 8);
		addSellableItemById(57, 73728);
		addSellableItemById(264, 8192);
	}

	public static void addSellableItemById(int id, int value) {
		addSellableItem(Item.getItemById(id), value);
	}

	public static void addValueSold(Item item, int value) {
		if (!valueSoldMap.containsKey(item)) {
			valueSoldMap.put(item, value);
		} else {
			valueSoldMap.put(item, valueSoldMap.get(item) + value);
		}
		totalValueSold += value;
		LogHelper.info("Total value is now at " + totalValueSold);
		LogHelper.info("Added " + value + " value to " + item.getUnlocalizedName() + " for a total of "
				+ valueSoldMap.get(item) + ". "+ (int)Math.floor((double)valueSoldMap.get(item)/totalValueSold*100) + " percent of total sales.");
	}

	public static void printAllValues() {
		for (Item item : baseValueMap.keySet()) {

		}
	}

}
