package com.alexpansion.gts.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueWrapper;
import com.alexpansion.gts.value.ValueWrapperItem;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class VanillaRecipeLoader extends RecipeLoader {

    private String[] cats = {"Crafting","Stonecutting","Smelting","Smoking","Blasting","Campfire Cooking","Fuel","Brewing","Anvil"};
    private List<String> categories = Arrays.asList(cats);

    @Override
    public HashMap<ValueWrapper,ArrayList<RecipeWrapper>> loadRecipes(String category,List<Object> recipes,HashMap<ValueWrapper,ArrayList<RecipeWrapper>> wrapperList) {
        if(category.equals("Fuel")||category.equals("Anvil")||category.equals("Brewing")){
            return wrapperList;
        }
        for (Object recipe : recipes) {
            if (recipe instanceof IRecipe<?>) {
                IRecipe<?> irecipe = (IRecipe<?>)recipe;
                ItemStack output = irecipe.getRecipeOutput();
                ArrayList<ArrayList<ValueWrapper>> input = new ArrayList<ArrayList<ValueWrapper>>();
                NonNullList<Ingredient> ingredients = irecipe.getIngredients();
                for(Ingredient i : ingredients){
                    ArrayList<ValueWrapper> ingredientList = new ArrayList<ValueWrapper>();
                    ItemStack[] stacks = i.getMatchingStacks();
                    for(ItemStack stack : stacks){
                        if(!stack.isEmpty()){
                            ValueWrapperItem inWrapper = ValueWrapperItem.get(stack.getItem(),true);
                            ingredientList.add(inWrapper);
                        }if(stack.getCount() > 1){
                            GTS.LOGGER.info("JEILoader handle this");
                        }
                    }
                    input.add(ingredientList);
                }
                RecipeWrapper recipeWrapper = new RecipeWrapper(output.getItem(),output.getCount(), input);
                ValueWrapperItem  outWrapper = ValueWrapperItem.get(output.getItem(),true);
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
