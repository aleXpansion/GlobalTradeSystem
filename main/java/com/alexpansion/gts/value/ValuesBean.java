package com.alexpansion.gts.value;

import java.util.HashMap;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValuesBean {

	private HashMap<Item, Integer> baseMap;
	private HashMap<Item, Double> valueMap;

	private IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;

	public ValuesBean(){
		baseMap = new HashMap<Item, Integer>();
		valueMap = new HashMap<Item, Double>();
	}

	public ValuesBean(HashMap<Item, Integer> baseMapIn, HashMap<Item, Double> valueMapIn) {
		baseMap = baseMapIn;
		valueMap = valueMapIn;
	}

	public ValuesBean(String inString) {
		String[] maps = inString.split(";");
		for (int i=0;i<2;i++) {
			StringBuilder sb = new StringBuilder(maps[i]);
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.indexOf("}"));
			maps[i] = sb.toString();
		}
		String[] basePairs = maps[0].split(", ");
		baseMap = new HashMap<Item, Integer>();
		valueMap = new HashMap<Item, Double>();
		try{
		for (String pair : basePairs) {
			String[] kv = pair.split("=");
			if(kv.length < 2){
				baseMap = new HashMap<Item, Integer>();
				return;
			}
			ResourceLocation rl = new ResourceLocation(kv[0]);
			Item key = itemReg.getValue(rl);
			baseMap.put(key, Integer.valueOf(kv[1]));
		}
		String[] valuePairs = maps[1].split(", ");
		for (String pair : valuePairs) {
			String[] kv = pair.split("=");
			if(kv.length < 2){
				valueMap = new HashMap<Item, Double>();
				return;
			}
			ResourceLocation rl = new ResourceLocation(kv[0]);
			Item key = itemReg.getValue(rl);
			double value = Double.valueOf(kv[1]);
			valueMap.put(key, value);
		}
		}catch(NullPointerException e){
			GTS.LOGGER.error("NPE in ValuesBean.<init>");
			//e.printStackTrace();
		}catch(IndexOutOfBoundsException e){
			GTS.LOGGER.error("IOOBE in ValuesBean.<init>");
			e.printStackTrace();
		}
	}

	public HashMap<Item, Integer> getBaseMap() {
		return baseMap;
	}

	public HashMap<Item, Double> getValueMap() {
		return valueMap;
	}

	public String toString() {
		return baseMap.toString() + ";" + valueMap.toString();
	}

}
