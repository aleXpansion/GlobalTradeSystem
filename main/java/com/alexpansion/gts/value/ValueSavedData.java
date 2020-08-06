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

	private HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	private HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	private HashMap<Item, Integer> amtSoldMap = new HashMap<Item, Integer>();
	private int total = 0;
	private static boolean valuesLoaded = false;
	private IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;

	public ValueSavedData(String name) {
		super(name);
		BaseValueManager.initItemValues();
		baseValueMap = BaseValueManager.baseValueMap;
		markDirty();
	}

	public ValueSavedData() {
		this(DATA_NAME);
	}

	@Override
	public void read(CompoundNBT nbt) {
		HashMap<Item, Integer> newSoldMap = new HashMap<Item, Integer>();
		HashMap<Item, Integer> newBaseMap = new HashMap<Item, Integer>();
		HashMap<Item, Integer> newAmtMap  = new HashMap<Item, Integer>();

		Set<String> keys = nbt.keySet();
		for (String key : keys) {
			if ("total".equals(key)) {
				total = nbt.getInt(key);
			} else {
				Item itemKey = itemReg.getValue(new ResourceLocation(key));
				String[] values = nbt.getString(key).split(",");
				int sold = Integer.parseInt(values[0]);
				if(sold != 0){
					newSoldMap.put(itemKey, sold);
				}
				newBaseMap.put(itemKey, Integer.parseInt(values[1]));
				int amt = Integer.parseInt(values[2]);
				if(amt != 0){
					newAmtMap.put(itemKey, amt);
				}
			}
		}
		valueSoldMap = newSoldMap;
		baseValueMap = newBaseMap;
		amtSoldMap   = newAmtMap;
		valuesLoaded = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		for (Map.Entry<Item, Integer> pair : baseValueMap.entrySet()) {
			if (pair.getKey() != null) {
				if(pair.getValue() == null){
					GTS.LOGGER.error("Null value in VSD for " +pair.getKey().toString()+" writing to NBT.");
					continue;
				}
				if(baseValueMap == null){
					GTS.LOGGER.error("Null baseValueMap in VSD.");
					continue;
				}
				Integer value = valueSoldMap.get(pair.getKey());
				value = value==null ? 0 : value;
				Integer amt = amtSoldMap.get(pair.getKey());
				amt = amt ==null ? 0 : amt;
				String values = value +","+pair.getValue().toString()+','+amt;
				nbt.putString(pair.getKey().toString(), values);
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

	public void saveAmts(HashMap<Item,Integer> map){
		amtSoldMap = map;
		markDirty();
	}

	public void saveAmt(Item key, Integer amt){
		amtSoldMap.put(key,amt);
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

	public HashMap<Item, Integer> getAmts() {
		return amtSoldMap;
	}

	public boolean areValuesLoaded() {
		return valuesLoaded;
	}

	@SuppressWarnings("unchecked")
	public HashMap<Item,Integer> getBaseValues(){
		return (HashMap<Item, Integer>) baseValueMap.clone();
	}

	//sets the base value of the given item. If value is 0 or less, removes base value for that item.
	public void setBaseValue(Item key, int value){
		if(value > 0){
			baseValueMap.put(key, value);
		}else if(baseValueMap.containsKey(key)){
			baseValueMap.remove(key);
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

	//overrides the existing base values with the supplied ones.
	public void setBaseValues(HashMap<Item, Integer> baseValueMapIn) {
		baseValueMap = baseValueMapIn;
		markDirty();
	}

}
