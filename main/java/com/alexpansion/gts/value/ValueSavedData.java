package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValueSavedData extends WorldSavedData implements Supplier<ValueSavedData>{

	private static final String DATA_NAME = GTS.MOD_ID + "ValueData";

	private HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	private int total = 0;
	private static boolean valuesLoaded = false;
	private IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;

	public ValueSavedData(String name) {
		super(name);
	}

	public ValueSavedData() {
		super(DATA_NAME);
	}

	@Override
	public void read(CompoundNBT nbt) {
		HashMap<Item, Integer> newMap = new HashMap<Item, Integer>();
		Set<String> keys = nbt.keySet();
		for (String key : keys) {
			if ("total".equals(key)) {
				total = nbt.getInt(key);
			} else {
				Item itemKey = itemReg.getValue(new ResourceLocation(key));
				Integer value = nbt.getInt(key);
				newMap.put(itemKey, value);
			}
		}
		valueSoldMap = newMap;
		GTS.LOGGER.info("readFromNBT was just called!");
		valuesLoaded = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		for (Map.Entry<Item, Integer> pair : valueSoldMap.entrySet()) {
			if (pair.getKey() != null) {
				nbt.putInt(pair.getKey().toString(), pair.getValue());
			}
		}
		nbt.putInt("total", total);
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

	public static ValueSavedData get(ServerWorld world) {

		DimensionSavedDataManager storage = world.getSavedData();
		ValueSavedData instance = storage.get( new ValueSavedData(), DATA_NAME);
		if (instance == null) {
			instance = new ValueSavedData();
			storage.set(instance);
			valuesLoaded = true;
		}
		return instance;
	}

	@Override
	public ValueSavedData get() {
		return this;
	}

}
