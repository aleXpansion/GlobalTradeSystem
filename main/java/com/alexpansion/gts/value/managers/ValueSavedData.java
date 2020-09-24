package com.alexpansion.gts.value.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.wrappers.ValueWrapper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class ValueSavedData extends WorldSavedData implements Supplier<ValueSavedData>{

	private static final String DATA_NAME = GTS.MOD_ID + "ValueData";
	
	private ValuesBean bean;
	private int total = 0;
	private static boolean valuesLoaded = false;

	public ValueSavedData(String name) {
		super(name);
		BaseValueManager.initItemValues();
		Map<String,Map<String,ValueWrapper>> wrapperMap = new HashMap<String,Map<String,ValueWrapper>>();
		wrapperMap.put("Item", BaseValueManager.wrapperMap);
		bean = new ValuesBean(wrapperMap);
		markDirty();
	}

	public ValueSavedData() {
		this(DATA_NAME);
	}

	@Override
	public void read(CompoundNBT nbt) {
		total = nbt.getInt("total");
		String wrappersString = nbt.getString("wrappers");
		bean = new ValuesBean(wrappersString,false);
		valuesLoaded = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putString("wrappers", bean.toString());
		nbt.putInt("total", total);
		return nbt;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int newTotal) {
		total = newTotal;
		markDirty();
	}

	public boolean areValuesLoaded() {
		return valuesLoaded;
	}

	public ValuesBean getBean(){
		return bean;
	}

	public void saveBean(ValuesBean beanIn){
		bean = beanIn;
		markDirty();
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
