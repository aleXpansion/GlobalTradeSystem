package com.alexpansion.gts.value;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValueWrapperItem extends ValueWrapper {

    private Item item;

    public ValueWrapperItem(Item itemIn){
        this.item = itemIn;
    }

    //creates a new wrapper for this item
    public static ValueWrapperItem create(String name){
        IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;
        Item item = itemReg.getValue(new ResourceLocation(name));
        return new ValueWrapperItem(item);
    }

    public Item getItem(){
        return item;
    }

    @Override
    public String toString(){
        return super.toString() + "Item;"+ item.getRegistryName().toString() ;
    }

    @Override
    public float calculateValue(int totalValueSold) {
        if (totalValueSold == 0) {
			totalValueSold = 1;
		}
        float newValue;
        if(soldAmt < 1){
            newValue = baseValue;
        }else{
            float multiplier = 1 - (float)(soldAmt -1)/640;
            newValue = baseValue * multiplier;
        }
        //int rampUp = ConfigurationHandler.rampUpCredits;
        //double multiplier = ConfigurationHandler.depreciationMultiplier;
        // multiplier = (totalValueSold / 15000) + 1;
        // double multiplier = 1;
        // int rampUp = 1200;
        // int valueSold = valueSoldMap.get(item);
        // if (baseValueMap.get(item) == null) {
        // 	toRemove = item;
        // 	return;
        // }
        // double baseValue = baseValueMap.get(item);
        // double newValue = baseValue;
        // double loss = 0;
        // if(valueSold >= totalValueSold){
        // 	valueSold = (totalValueSold /2)+1;
        // }
        // if (totalValueSold < rampUp) {
        // 	newValue = ((rampUp - totalValueSold) / (double) rampUp) * baseValue
        // 			+ (totalValueSold / (double) rampUp) * ((totalValueSold - valueSold) / (double)(totalValueSold));
        // } else {
        // 	loss = newValue * ((valueSold) / ((double) totalValueSold)) * multiplier;
        // 	newValue -= loss;
        // }
        // LogHelper.info(item.getUnlocalizedName() + " is worth " +
        // newValue);
        this.value = newValue;
        return newValue;
    }
}