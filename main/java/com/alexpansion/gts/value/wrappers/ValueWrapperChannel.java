package com.alexpansion.gts.value.wrappers;

import java.util.HashMap;
import java.util.Map;

public class ValueWrapperChannel extends ValueWrapper{

    private String id;
    private static Map<String,ValueWrapperChannel> serverMap = new HashMap<String,ValueWrapperChannel>();
    private static Map<String,ValueWrapperChannel> clientMap = new HashMap<String,ValueWrapperChannel>();


    private ValueWrapperChannel(String id){
        this.id = id;
    }

    public static ValueWrapperChannel get(String id, boolean isRemote){
        Map<String,ValueWrapperChannel> map = isRemote ? clientMap : serverMap;
        if(map.containsKey(id)){
            return map.get(id);
        }else{
            ValueWrapperChannel instance = new ValueWrapperChannel(id);
            map.put(id,instance);
            instance.setLimit(1000000);
            return instance;
        }
    }

    public String toString(){
        return super.toString() +"Channel;"+ id;
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
        return id;
    }

    public int getLimit(){
        if(soldValue == 0){
            soldValue = 1000000000;
        }
        return (int)soldValue;
    }

    public void setLimit(int limit){
        soldValue = limit;
    }
}
