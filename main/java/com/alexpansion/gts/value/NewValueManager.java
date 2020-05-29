package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Deprecated
public class NewValueManager {

    private ValuesBean bean;

    private static NewValueManager instance;

    private IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;
    private HashMap<Item,Double> values;

    public static NewValueManager getVM(){
        if(instance == null){
            instance = new NewValueManager();
        }
        return instance;
    }

    public NewValueManager(){
        ResourceLocation rl = new ResourceLocation("minecraft:cobblestone");
        values = new HashMap<Item,Double>();
        Item i = itemReg.getValue(rl);
        values.put(i, 1.0);
    }

    public Double getValue(ItemStack stack){
        if(values.containsKey(stack.getItem())){
            return values.get(stack.getItem());
        }else{
            return 1.0;
        }
    }

    public void itemSold(Item item, int amt){
        Double oldVal = values.get(item);
        if(oldVal == null){
            oldVal = 1.0;
        }
        Double change = 1- Double.valueOf(amt)/100;
        Double newVal = oldVal*change;
        values.put(item, newVal);
    }

    public void itemBought(Item item){
        itemSold(item, -1);
    }

    public void itemSold(Item item){
        itemSold(item,1);
    }

    public Collection<ItemStack> getAllBuyable(){
        Collection<ItemStack> out = new ArrayList<ItemStack>();
        for(Item i : values.keySet()){
            out.add(new ItemStack(i));
        }
        return out;
    }

    public void setBean(ValuesBean inBean) {
		bean = inBean;
    }
    
    public ValuesBean getBean(){
        return bean;
    }

}