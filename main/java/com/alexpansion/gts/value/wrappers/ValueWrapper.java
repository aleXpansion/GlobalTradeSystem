package com.alexpansion.gts.value.wrappers;

import java.util.Arrays;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerClient;

public abstract class ValueWrapper {

    protected int baseValue;
    protected float value;
    protected float soldValue;
    private int soldAmt;
    protected boolean canBuy = false;
    protected boolean canSell = false;
    protected boolean calculated = false;
    protected boolean isRemote;
    protected String type;
    protected String label;

    public ValueWrapper(String type,String label,boolean isRemote){
        this.type = type;
        this.label = label;
        this.isRemote = isRemote;
    }

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
        }else if(type.equals("Energy")){
            newWrapper = ValueWrapperEnergy.get(String.join(";", splitString),isRemote);
        }else if(type.equals("Channel")){
            newWrapper = ValueWrapperChannel.get(String.join(";", splitString),isRemote);
        }else if(type.equals("Fluid")){
            newWrapper = ValueWrapperFluid.get(String.join(";", splitString),isRemote);
        }else {
            GTS.LOGGER.error("Invalid type "+type+ " in ValueWrapper#create.");
            return null;
        }
        newWrapper.soldValue = Float.parseFloat(values[1]);
        newWrapper.soldAmt = Integer.parseInt(values[2]);
        if(values.length > 3){    
            newWrapper.canBuy = Boolean.parseBoolean(values[3]);
        }
        newWrapper.setBaseValue(Integer.parseInt(values[0]));
        return newWrapper;
    }

    public String toString(){
        return baseValue+","+soldValue+","+soldAmt+","+canBuy+";";
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
        if(baseValue == 0 && !calculated && isRemote){
            ValueManagerClient vm = ValueManager.getClientVM();
            baseValue = vm.getCraftingValue(this);
            calculated = true;
        }
        return baseValue;
    }

    public float getValue() {
        if(!calculated){
            calculateValue(1);
            calculated = true;
        }
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
        canBuy = true;
    }

    public boolean canBuy(){
        return canBuy;
    }

    public boolean canSell(){
        return canSell;
    }

    public abstract float calculateValue(int totalValueSold);

    public abstract String getType();

    public abstract String getLabel();
}