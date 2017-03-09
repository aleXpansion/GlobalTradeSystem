package com.alexpansion.gts.utility;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ValueManagerServer extends ValueManager{
	
	public ValueManagerServer(World inWorld) {
		super(inWorld);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ValuesBean getBean() {
		if (!GTSUtil.areValuesLoaded()) {
			GTSUtil.loadValues(world);
		}
		HashMap<SItem, Integer> baseMap = new HashMap<SItem, Integer>();
		HashMap<SItem, Double> valueMap = new HashMap<SItem, Double>();

		ArrayList<Item> sellables = GTSUtil.getAllSellableItems();

		for (Item item : sellables) {
			//TODO implement stuff
			baseMap.put(SItem.getSItem(item), GTSUtil.getBaseValue(item));
			valueMap.put(SItem.getSItem(item), GTSUtil.getValue(item));
		}
		return new ValuesBean(baseMap,valueMap);
	}

}
