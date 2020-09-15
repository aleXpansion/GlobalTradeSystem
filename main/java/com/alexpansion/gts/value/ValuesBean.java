package com.alexpansion.gts.value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketBuffer;

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

	public static ValuesBean create(PacketBuffer buf){
		String string = "";
		String inString = buf.readString();
		while(inString.length() == 2900){
			string += inString;
			inString = buf.readString();
		}
		string += inString;
		return new ValuesBean(string);
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
			//take off the last character to remove the trailing @
			out = out.substring(0,out.length()-1);
			out += "#";
		}
		
		return out.substring(0,out.length()-1);
	}

	public PacketBuffer toBytes(PacketBuffer buf){
		String string = toString();
		byte[] abyte = string.getBytes(StandardCharsets.UTF_8);
		while(abyte.length > 3000){
			buf.writeString(string.substring(0,2900));
			string = string.substring(2900);
			abyte = string.getBytes(StandardCharsets.UTF_8);
		}
		buf.writeString(string);
		return buf;
	}

}
