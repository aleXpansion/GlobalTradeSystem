package com.alexpansion.gts.value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.GTS;

import net.minecraft.network.PacketBuffer;

public class ValuesBean {

	public Map<String,Map<String,ValueWrapper>> wrapperMap;

	public ValuesBean(){
		wrapperMap = new HashMap<String,Map<String,ValueWrapper>>();
	}

	public ValuesBean(Map<String,Map<String,ValueWrapper>> wrapperMap) {
		this.wrapperMap = wrapperMap;
	}

	public ValuesBean(String inString,boolean isRemote) {
		wrapperMap = new HashMap<String,Map<String,ValueWrapper>>();
		String[] splitString = inString.split("#");
		for(String setString : splitString){
			String[] setStringSplit = setString.split(":");
			String key = setStringSplit[0];
			String[] wrappingStrings = setString.substring(setString.indexOf(":")+1).split("@");
			Map<String,ValueWrapper> newList = new HashMap<String,ValueWrapper>();
			for(String wrapperString : wrappingStrings){
				String[] wrapperSplit = wrapperString.split("%");
				if(wrapperSplit.length < 2){
					GTS.LOGGER.error("Improper length for wrapperSplit in ValuesBean.<init> for string "+wrapperString);
				}else{
					newList.put(wrapperSplit[0],ValueWrapper.get(wrapperSplit[1],isRemote));
				}
			}
			wrapperMap.put(key, newList);
		}	
	}

	public static ValuesBean create(PacketBuffer buf,boolean isRemote){
		String string = "";
		String inString = buf.readString();
		while(inString.length() == 29000){
			string += inString;
			inString = buf.readString();
		}
		string += inString;
		return new ValuesBean(string,isRemote);
	}

	public Map<String,ValueWrapper> getWrappers(String key){
		return wrapperMap.get(key);
	}

	public Map<String,Map<String,ValueWrapper>> getWrappers(){
		return wrapperMap;
	}

	public ValueWrapper getWrapper(String category,String label){
		return wrapperMap.get(category).get(label);
	}

	public String toString() {
		String out = "";
		for(String key : wrapperMap.keySet()){
			out += key + ":";
			for(String wrapperKey : wrapperMap.get(key).keySet()){
				out += wrapperKey +"%"+ wrapperMap.get(key).get(wrapperKey).toString() + "@";
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
		while(abyte.length > 30000){
			buf.writeString(string.substring(0,29000));
			string = string.substring(29000);
			abyte = string.getBytes(StandardCharsets.UTF_8);
		}
		buf.writeString(string);
		return buf;
	}

}
