package com.alexpansion.gts.value;

import java.util.HashMap;

import com.alexpansion.gts.utility.LogHelper;

public class ValuesBean {

	private HashMap<SItem, Integer> baseMap;
	private HashMap<SItem, Double> valueMap;

	public ValuesBean(HashMap<SItem, Integer> baseMapIn, HashMap<SItem, Double> valueMapIn) {
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
		baseMap = new HashMap<SItem, Integer>();
		valueMap = new HashMap<SItem, Double>();
		try{
		for (String pair : basePairs) {
			String[] kv = pair.split("=");
			if(kv[0].contains(":stone/")){
				LogHelper.info("found stone");
			}
			SItem key = SItem.getSItem(kv[0]);
			baseMap.put(key, Integer.valueOf(kv[1]));
		}
		String[] valuePairs = maps[1].split(", ");
		for (String pair : valuePairs) {
			String[] kv = pair.split("=");
			SItem key = SItem.getSItem(kv[0]);
			double value = Double.valueOf(kv[1]);
			valueMap.put(key, value);
		}
		}catch(NullPointerException e){
			LogHelper.error("NPE in ValuesBean.<init>");
			//e.printStackTrace();
		}catch(IndexOutOfBoundsException e){
			LogHelper.error("IOOBE in ValuesBean.<init>");
		}
	}

	public HashMap<SItem, Integer> getBaseMap() {
		return baseMap;
	}

	public HashMap<SItem, Double> getValueMap() {
		return valueMap;
	}

	public String toString() {
		return baseMap.toString() + ";" + valueMap.toString();
	}

}
