package com.alexpansion.gts.value.wrappers;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.Config;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ValueWrapperFluid extends ValueWrapper {

    private static Map<Fluid,ValueWrapperFluid> fluidMapServer = new HashMap<Fluid,ValueWrapperFluid>();
    private static Map<Fluid,ValueWrapperFluid> fluidMapRemote = new HashMap<Fluid,ValueWrapperFluid>();

    private Fluid fluid;

    public static ValueWrapperFluid get(Fluid fluidIn,boolean isRemote){
        Map<Fluid,ValueWrapperFluid> fluidMap = isRemote ? fluidMapRemote : fluidMapServer;
        if(fluidMap.containsKey(fluidIn)){
            return fluidMap.get(fluidIn);
        }else{
            return new ValueWrapperFluid(fluidIn,isRemote);
        }
    }

    private ValueWrapperFluid(Fluid fluidIn, boolean isRemote){
        super("Fluid",fluidIn.getRegistryName().toString(),isRemote);
        Map<Fluid,ValueWrapperFluid> fluidMap = isRemote ? fluidMapRemote : fluidMapServer;
        this.fluid = fluidIn;
        fluidMap.put(fluidIn, this);
    }

    //gets or creates the wrapper for this fluid
    public static ValueWrapperFluid get(String name,boolean isRemote){
        IForgeRegistry<Fluid> fluidReg = ForgeRegistries.FLUIDS;
        Fluid fluid = fluidReg.getValue(new ResourceLocation(name));
        return get(fluid,isRemote);
    }

    public Fluid getFluid(){
        return fluid;
    }

    @Override
    public String toString(){
        return super.toString() + "Fluid;"+ fluid.getRegistryName().toString() ;
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
        // int valueSold = valueSoldMap.get(fluid);
        // if (baseValueMap.get(fluid) == null) {
        // 	toRemove = fluid;
        // 	return;
        // }
        // double baseValue = baseValueMap.get(fluid);
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
        // LogHelper.info(fluid.getUnlocalizedName() + " is worth " +
        // newValue);
        this.value = newValue;
        return newValue;
    }

    @Override
    public String getType() {
        return "Fluid";
    }

    @Override
    public String getLabel() {
        return fluid.getRegistryName().toString();
    }
}