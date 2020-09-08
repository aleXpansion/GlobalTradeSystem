package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.alexpansion.gts.GTS;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class ValueSavedData extends WorldSavedData implements Supplier<ValueSavedData>{

	private static final String DATA_NAME = GTS.MOD_ID + "ValueData";
	
	private List<ValueWrapper> wrapperList = new ArrayList<ValueWrapper>();
	private int total = 0;
	private static boolean valuesLoaded = false;

	public ValueSavedData(String name) {
		super(name);
		BaseValueManager.initItemValues();
		wrapperList = BaseValueManager.wrapperList;
		markDirty();
	}

	public ValueSavedData() {
		this(DATA_NAME);
	}

	@Override
	public void read(CompoundNBT nbt) {
		List<ValueWrapper> newWrapperList = new ArrayList<ValueWrapper>();

		total = nbt.getInt("total");
		String wrappersString = nbt.getString("wrappers");
		String[] splitString = wrappersString.split(",");
		for(String wrapperString : splitString){
			ValueWrapper wrapper = ValueWrapper.create(wrapperString);
			newWrapperList.add(wrapper);
		}
		wrapperList = newWrapperList;
		valuesLoaded = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		String wrapperString = wrapperList.toString();
		for(ValueWrapper wrapper : wrapperList){
			wrapperString += wrapper.toString() + ",";
		}
		wrapperString = wrapperString.substring(0, wrapperString.length()-1);
		nbt.putString("wrappers", wrapperString);
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

	public List<ValueWrapper> getWrappers(){
		return wrapperList;
	}

	public void saveWrappers(List<ValueWrapper> wrapperListIn){
		wrapperList = wrapperListIn;
		markDirty();
	}

	public void saveWrapper(ValueWrapper wrapper){
		if(!wrapperList.contains(wrapper)){
			wrapperList.add(wrapper);
		}
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
