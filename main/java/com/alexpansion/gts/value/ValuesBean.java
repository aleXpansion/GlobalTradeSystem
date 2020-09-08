package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValuesBean {

	public Map<String,List<ValueWrapper>> wrappersMap;

	public ValuesBean(){
		wrappersMap = new HashMap<String,List<ValueWrapper>>();
	}

	public ValuesBean(Map<String,List<ValueWrapper>> wrappersMap) {
		this.wrappersMap = wrappersMap;
	}

	public ValuesBean(String inString) {
		wrappersMap = new HashMap<String,List<ValueWrapper>>();
		String[] splitString = inString.split("#");
		for(String setString : splitString){
			String[] setStringSplit = setString.split(":");
			String key = setStringSplit[0];
			String[] wrappingStrings = setString.substring(setString.indexOf(":")+1).split("@");
			List<ValueWrapper> newList = new ArrayList<ValueWrapper>();
			for(String wrapperString : wrappingStrings){
				newList.add(ValueWrapper.create(wrapperString));
			}
			wrappersMap.put(key, newList);
		}	
	}

	public List<ValueWrapper> getWrappers(String key){
		return wrappersMap.get(key);
	}

	public String toString() {
		String out = "";
		for(String key : wrappersMap.keySet()){
			out += key + ":";
			for(ValueWrapper wrapper : wrappersMap.get(key)){
				out += wrapper.toString() + "@";
			}
			//take off the last character to remove the trailing comma
			out = out.substring(0,out.length()-1);
			out += "#";
		}
		
		return out.substring(0,out.length()-1);
	}


}
