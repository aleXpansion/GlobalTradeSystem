package com.alexpansion.gts.utility;

import java.util.HashMap;

public class ValuesBean {
	
	private HashMap<SItem,Integer> baseMap;
	private HashMap<SItem,Double> valueMap;
	
	public ValuesBean(HashMap<SItem,Integer> baseMapIn,HashMap<SItem,Double> valueMapIn){
		baseMap = baseMapIn;
		valueMap = valueMapIn;
	}
	
	public ValuesBean(String inString){
		String[] maps = inString.split(":");
		String[] basePairs = maps[0].split(", ");
		baseMap = new HashMap<SItem,Integer>();
		for(String pair : basePairs){
			String[] kv = pair.split("=");
			baseMap.put(new SItem(kv[0]), Integer.valueOf(kv[1]));
		}
		String[] valuePairs = maps[1].split(", ");
		for(String pair : valuePairs){
			String[] kv = pair.split("=");
			valueMap.put(new SItem(kv[0]), Double.valueOf(kv[1]));
		}
	}
	
	public HashMap<SItem,Integer> getBaseMap(){
		return baseMap;
	}
	
	public HashMap<SItem,Double> getValueMap(){
		return valueMap;
	}
	
	public String toString(){
		return baseMap.toString() + ":" + valueMap.toString();
	}

}
