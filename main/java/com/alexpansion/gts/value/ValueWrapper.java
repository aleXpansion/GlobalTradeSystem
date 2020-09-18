package com.alexpansion.gts.value;

import java.util.Arrays;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.tools.JEIloader;

public abstract class ValueWrapper {

    protected int baseValue;
    protected float value;
    protected float soldValue;
    private int soldAmt;
    protected boolean available = false;
    protected boolean calculated = false;

    public static ValueWrapper get(String inString,boolean isRemote) {
        String[] splitString = inString.split(";");
        String valueString = splitString[0];
        String[] values = valueString.split(",");
        String type;
        if(splitString.length > 1){
            type = splitString[1];
        }else{
            GTS.LOGGER.error("attempted to create ValueWrapper with string "+inString);
            return null;
        }
        splitString = Arrays.copyOfRange(splitString, 2, splitString.length);
        ValueWrapper newWrapper;
        if(type.equals("Item")){
            newWrapper =  ValueWrapperItem.get(String.join(";", splitString),isRemote);
        }else{
            GTS.LOGGER.error("Invalid type "+type+ " in ValueWrapper#create.");
            return null;
        }
        newWrapper.soldValue = Float.parseFloat(values[1]);
        newWrapper.soldAmt = Integer.parseInt(values[2]);
        if(values.length > 3){    
            newWrapper.available = Boolean.parseBoolean(values[3]);
        }
        newWrapper.setBaseValue(Integer.parseInt(values[0]));
        return newWrapper;
    }

    public String toString(){
        return baseValue+","+soldValue+","+soldAmt+","+available+";";
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setBaseValue(int baseValue){
        this.baseValue = baseValue;
        calculated = true;
        calculateValue((int)soldValue);
    }

    public int getBaseValue(){
        if(baseValue == 0 && !calculated){
            baseValue = JEIloader.getCrafingValue(this);
            calculated = true;
        }
        return baseValue;
    }

    public float getValue() {
        return value;
    }

    public int getSoldAmt(){
        return soldAmt;
    }

    public int getAvailable(){
        return soldAmt;
    }

    public void addSold(float soldValueIn,int soldAmtIn){
        this.soldValue += soldValueIn;
        this.soldAmt += soldAmtIn;
        available = true;
    }

    public abstract float calculateValue(int totalValueSold);
}