package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Set;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValuesBean {

	private HashMap<Item, Integer> baseMap;
	private HashMap<Item, Double> valueMap;
	private HashMap<Item, Integer> amtMap;

	private IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;

	public ValuesBean(){
		baseMap = new HashMap<Item, Integer>();
		valueMap = new HashMap<Item, Double>();
		amtMap = new HashMap<Item, Integer>();
	}

	public ValuesBean(HashMap<Item, Integer> baseMapIn, HashMap<Item, Double> valueMapIn, HashMap<Item, Integer> amtMapIn) {
		baseMap = baseMapIn;
		valueMap = valueMapIn;
		amtMap = amtMapIn;
	}

	public ValuesBean(String inString) {
		String[] maps = inString.split(";");
		// for (int i=0;i<3;i++) {
		// 	StringBuilder sb = new StringBuilder(maps[i]);
		// 	sb.deleteCharAt(0);
		// 	sb.deleteCharAt(sb.indexOf("}"));
			// maps[i] = sb.toString();
		// }
		String[] basePairs = maps[0].split(", ");
		baseMap = new HashMap<Item, Integer>();
		valueMap = new HashMap<Item, Double>();
		amtMap = new HashMap<Item,Integer>();
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
			String[] amtPairs = maps[2].split(", ");
			for (String pair : amtPairs) {
				String[] kv = pair.split("=");
				if(kv.length < 2){
					amtMap = new HashMap<Item, Integer>();
					return;
				}
				ResourceLocation rl = new ResourceLocation(kv[0]);
				Item key = itemReg.getValue(rl);
				int amt = Integer.parseInt(kv[1]);
				amtMap.put(key, amt);
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

	public HashMap<Item,Integer> getAmtMap(){
		return amtMap;
	}

	public String toString() {
		String out1 = itemMapToStringInt(baseMap);
		String out2 = ";" +  itemMapToStringDouble(valueMap);
		String out3 = ';' + itemMapToStringInt(amtMap);
		return out1 + out2 + out3;
	}

	private String itemMapToStringInt(HashMap<Item,Integer> map){
		String out = "";
		Object[] keys = map.keySet().toArray();
		for(int i = 0;i<keys.length;i++){
			Item key = (Item)keys[i];
			out += key.getRegistryName().toString();
			out += "=" + map.get(key);
			if(i < keys.length -1){
				out += ", ";
			}
		}
		return out;
	}

	private String itemMapToStringDouble(HashMap<Item,Double> map){
		String out = "";
		Object[] keys = map.keySet().toArray();
		for(int i = 0;i<keys.length;i++){
			Item key = (Item)keys[i];
			out += key.getRegistryName().toString();
			out += "=" + map.get(key);
			if(i < keys.length -1){
				out += ", ";
			}
		}
		return out;
	}

}
