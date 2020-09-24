package com.alexpansion.gts.value.wrappers;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.Config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValueWrapperItem extends ValueWrapper {

    private static Map<Item,ValueWrapperItem> itemMapServer = new HashMap<Item,ValueWrapperItem>();
    private static Map<Item,ValueWrapperItem> itemMapRemote = new HashMap<Item,ValueWrapperItem>();

    private Item item;

    public static ValueWrapperItem get(Item itemIn,boolean isRemote){
        Map<Item,ValueWrapperItem> itemMap = isRemote ? itemMapRemote : itemMapServer;
        if(itemMap.containsKey(itemIn)){
            return itemMap.get(itemIn);
        }else{
            return new ValueWrapperItem(itemIn,isRemote);
        }
    }

    private ValueWrapperItem(Item itemIn, boolean isRemote){
        Map<Item,ValueWrapperItem> itemMap = isRemote ? itemMapRemote : itemMapServer;
        this.item = itemIn;
        itemMap.put(itemIn, this);
    }

    //gets or creates the wrapper for this item
    public static ValueWrapperItem get(String name,boolean isRemote){
        IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;
        Item item = itemReg.getValue(new ResourceLocation(name));
        return get(item,isRemote);
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
        if(getSoldAmt() == 0) {
            newValue = baseValue;
        }else if(getSoldAmt() > 0){
            float multiplier = 1 - (float)(getSoldAmt() -1)/Config.SOLD_ITEMS_MAX.get();
            newValue = baseValue * multiplier;
        }else{
            float multiplier = 1 + (float)(0- getSoldAmt())/Config.BOUGHT_ITEMS_DOUBLE.get();
            newValue = baseValue * multiplier;
        }
        if(getSoldAmt() > Config.SOLD_ITEMS_MAX.get() || getSoldAmt() < (0-Config.BOUGHT_ITEMS_MAX.get())){
            available = false;
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

    @Override
    public String getType() {
        return "Item";
    }

    @Override
    public String getLabel() {
        return item.getRegistryName().toString();
    }
}