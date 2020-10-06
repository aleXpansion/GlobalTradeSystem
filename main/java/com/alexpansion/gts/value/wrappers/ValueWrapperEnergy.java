package com.alexpansion.gts.value.wrappers;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerServer;

public class ValueWrapperEnergy extends ValueWrapper {

    private static Map<String,ValueWrapperEnergy> serverMap = new HashMap<String,ValueWrapperEnergy>();
    private static Map<String,ValueWrapperEnergy> clientMap = new HashMap<String,ValueWrapperEnergy>();

    public static ValueWrapperEnergy get(String type,boolean isRemote){
        Map<String,ValueWrapperEnergy> map = isRemote ? clientMap : serverMap;
        if(map.get(type) == null){
            ValueWrapperEnergy instance = new ValueWrapperEnergy(type,isRemote);
            map.put(type,instance);
            if(!isRemote){
                ValueManagerServer vm = ValueManager.getServerVM();
                if(vm != null){
                    vm.addWrapper(instance,type, "Energy");
                }
            }
            return instance;
        }else{
            return map.get(type);
        }
    }

    public ValueWrapperEnergy(String label,boolean isRemote){
        super("Energy",label,isRemote);
    }

    @Override
    public float calculateValue(int totalValueSold) {
        baseValue = 1000;
        int maxBought = 1000000;
        int sold = getSoldAmt();
        if(sold > 0){
            value = sold/baseValue +baseValue;
        }else if(sold == 0){
            value = baseValue;
        }else{
            float mult = (float)sold/maxBought;
            value = mult*baseValue + baseValue;
        }
        canBuy = sold > 0-maxBought;
        if(value < 1){
            value = 1;
        }
        return value;
    }

    @Override
    public String toString(){
        return super.toString() + "Energy;"+label;
    }

    @Override
    public String getType() {
        return "Energy";
    }

    @Override
    public String getLabel() {
        return label;
    }
    
}
