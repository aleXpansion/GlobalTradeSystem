package com.alexpansion.gts.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alexpansion.gts.reference.Reference;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class ValueSavedData extends WorldSavedData {

	private static final String DATA_NAME = Reference.MOD_ID + "ValueData";

	private HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	private int total = 0;
	private static boolean valuesLoaded = false;

	public ValueSavedData(String name) {
		super(name);
	}

	public ValueSavedData() {
		super(DATA_NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		HashMap<Item, Integer> newMap = new HashMap<Item, Integer>();
		Set<String> keys = nbt.getKeySet();
		for (String key : keys) {
			if ("total".equals(key)) {
				total = nbt.getInteger(key);
			} else {
				Item itemKey = GTSUtil.getItemFromRegistryName(key);
				Integer value = nbt.getInteger(key);
				newMap.put(itemKey, value);
			}
		}
		valueSoldMap = newMap;
		LogHelper.info("readFromNBT was just called!");
		valuesLoaded = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		for (Map.Entry<Item, Integer> pair : valueSoldMap.entrySet()) {
			if (pair.getKey() != null) {
				nbt.setInteger(pair.getKey().getRegistryName().toString(), pair.getValue());
			}
		}
		nbt.setInteger("total", total);
		return nbt;
	}

	public void saveValues(HashMap<Item, Integer> map) {
		valueSoldMap = map;
		markDirty();
	}

	public void saveValue(Item key, Integer value) {
		valueSoldMap.put(key, value);
		markDirty();
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int newTotal) {
		total = newTotal;
		markDirty();
	}

	public HashMap<Item, Integer> getValues() {
		return valueSoldMap;
	}

	public boolean areValuesLoaded() {
		return valuesLoaded;
	}

	public static ValueSavedData get(World world) {

		MapStorage storage = world.getMapStorage();
		ValueSavedData instance = (ValueSavedData) storage.getOrLoadData(ValueSavedData.class, DATA_NAME);
		if (instance == null) {
			instance = new ValueSavedData();
			storage.setData(DATA_NAME, instance);
		}
		// instance.blank = true;

		if (!GTSUtil.areValuesLoaded()) {
			GTSUtil.loadValues(world);
		}
		return instance;
	}

}
