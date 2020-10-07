package com.alexpansion.gts.value.wrappers;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.Config;

public class ValueWrapperChannel extends ValueWrapper{

    private static Map<String,ValueWrapperChannel> serverMap = new HashMap<String,ValueWrapperChannel>();
    private static Map<String,ValueWrapperChannel> clientMap = new HashMap<String,ValueWrapperChannel>();


    private ValueWrapperChannel(String label,boolean isRemote){
        super("Channel",label,isRemote);
    }

    public static ValueWrapperChannel get(String label, boolean isRemote){
        Map<String,ValueWrapperChannel> map = isRemote ? clientMap : serverMap;
        if(map.containsKey(label)){
            return map.get(label);
        }else{
            ValueWrapperChannel instance = new ValueWrapperChannel(label,isRemote);
            map.put(label,instance);
            instance.setLimit(1000000);
            return instance;
        }
    }

    public String toString(){
        return super.toString() +"Channel;"+ label;
    }

    @Override
    public float calculateValue(int totalValueSold) {
        value = baseValue;
        return value;
    }

    public void setValue(int value){
        this.baseValue = value;
        this.value = value;
    }

    @Override
    public String getType() {
        return "Channel";
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getLimit(){
       return Config.CHANNEL_LIMIT.get();
    }

    public void setLimit(int limit){
        soldValue = limit;
    }
}
