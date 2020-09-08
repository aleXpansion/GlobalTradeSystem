package com.alexpansion.gts.value;

import java.util.Arrays;

import com.alexpansion.gts.GTS;

public abstract class ValueWrapper {

    protected int baseValue;
    protected float value;
    protected float soldValue;
    protected int soldAmt;

    public static ValueWrapper create(String inString) {
        String[] splitString = inString.split(";");
        String valueString = splitString[0];
        String[] values = valueString.split(",");
        String type;
        if(splitString.length > 1){
            type = splitString[1];
        }else{
            GTS.LOGGER.error("stuff");
            type = "";
        }
        splitString = Arrays.copyOfRange(splitString, 2, splitString.length);
        ValueWrapper newWrapper;
        if(type.equals("Item")){
            newWrapper =  ValueWrapperItem.create(String.join(";", splitString));
        }else{
            GTS.LOGGER.error("Invalid type "+type+ " in ValueWrapper#create.");
            return null;
        }
        newWrapper.soldValue = Float.parseFloat(values[1]);
        newWrapper.soldAmt = Integer.parseInt(values[2]);
        newWrapper.setBaseValue(Integer.parseInt(values[0]));
        return newWrapper;
    }

    public String toString(){
        return baseValue+","+soldValue+","+soldAmt+";";
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setBaseValue(int baseValue){
        this.baseValue = baseValue;
        calculateValue((int)soldValue);
    }

    public int getBaseValue(){
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
    }

    public abstract float calculateValue(int totalValueSold);
}