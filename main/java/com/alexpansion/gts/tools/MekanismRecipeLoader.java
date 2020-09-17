package com.alexpansion.gts.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.value.ValueWrapper;
import com.alexpansion.gts.value.ValueWrapperItem;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import net.minecraft.item.ItemStack;

public class MekanismRecipeLoader extends RecipeLoader {

    private String[] cats = {"Chemical Crystallizer","Chemical Dissolution Chamber","Chemical Infuser","Chemical Washer",
        "Electrolytic Separator","Metallurgic Infuser","Pressurized Reaction Chamber","Condensentrating","Decondensentrating",
        "Chemical Oxidizer","Nutritional Liquifier","Solar Neutron Activator","Isotopic Centrifuge","Combiner","Purification Chamber",
        "Osmium Compressor","Chemical Injection Chamber","Antiprotonic Nucleosynthesizer","Supercritical Phase Shifter",
        "Precision Sawmill","Enrichment Chamber","Crusher","Energized Smelter","Thermal Evaporation Controller",
        "Item to Energy","Item to Gas","Item to Infuse Type"};
    private List<String> categories = Arrays.asList(cats);

    @Override
    public HashMap<ValueWrapper, ArrayList<RecipeWrapper>> loadRecipes(String category, List<Object> recipes,
            HashMap<ValueWrapper, ArrayList<RecipeWrapper>> wrapperList) {
        for(Object recipe:recipes){
            if (recipe instanceof ItemStackToItemStackRecipe) {
                ItemStackToItemStackRecipe stackRecipe = (ItemStackToItemStackRecipe)recipe;
                List<ItemStack> input = stackRecipe.getInput().getRepresentations();
                ArrayList<ArrayList<ValueWrapper>> inputList = new ArrayList<ArrayList<ValueWrapper>>();
                ArrayList<ValueWrapper> slotList = new ArrayList<ValueWrapper>();
                for(ItemStack stack:input){
                    slotList.add(ValueWrapperItem.get(stack.getItem()));
                }
                inputList.add(slotList);
                ItemStack output = stackRecipe.getOutput(input.get(0));

                RecipeWrapper recipeWrapper = new RecipeWrapper(output.getItem(),output.getCount(), inputList);
                ValueWrapperItem  outWrapper = ValueWrapperItem.get(output.getItem());
                if(wrapperList.containsKey(outWrapper)){
                    wrapperList.get(outWrapper).add(recipeWrapper);
                }else{
                    ArrayList<RecipeWrapper> list = new ArrayList<RecipeWrapper>();
                    list.add(recipeWrapper);
                    wrapperList.put(outWrapper, list);
                }
            }
        }
        return wrapperList;
    }

    @Override
    public boolean hasCategory(String category) {
        return categories.contains(category);
    }
    
}
