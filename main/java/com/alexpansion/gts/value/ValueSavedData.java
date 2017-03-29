package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alexpansion.gts.reference.Reference;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class ValueSavedData extends WorldSavedData {

	private static final String DATA_NAME = Reference.MOD_ID + "ValueData";

	private HashMap<SItem, Integer> valueSoldMap = new HashMap<SItem, Integer>();
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
		HashMap<SItem, Integer> newMap = new HashMap<SItem, Integer>();
		Set<String> keys = nbt.getKeySet();
		for (String key : keys) {
			if ("total".equals(key)) {
				total = nbt.getInteger(key);
			} else {
				SItem itemKey = SItem.getSItem(key);
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
		for (Map.Entry<SItem, Integer> pair : valueSoldMap.entrySet()) {
			if (pair.getKey() != null) {
				nbt.setInteger(pair.getKey().toString(), pair.getValue());
			}
		}
		nbt.setInteger("total", total);
		return nbt;
	}

	public void saveValues(HashMap<SItem, Integer> map) {
		valueSoldMap = map;
		markDirty();
	}

	public void saveValue(SItem key, Integer value) {
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

	public HashMap<SItem, Integer> getValues() {
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

		return instance;
	}

}
