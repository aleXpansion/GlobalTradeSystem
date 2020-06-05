package com.alexpansion.gts.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerServer;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipe;
import mezz.jei.plugins.vanilla.brewing.JeiBrewingRecipe;
import mezz.jei.plugins.vanilla.cooking.fuel.FuelRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIloader implements IModPlugin {

    private static IRecipeManager manager;
    private static boolean loaded = false;
    private static HashMap<Item,RecipeWrapper> itemList;

    public JEIloader() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GTS.MOD_ID, "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IJeiRuntime runtime = jeiRuntime;
        manager = runtime.getRecipeManager();
        loadRecipes();
        loaded = true;
    }

    private static ArrayList<Item> checking = new ArrayList<Item>();

    public static int getCrafingValue(ValueManagerServer vm, Item item) {
        if(!itemList.containsKey(item)){
            return 0;
        }
        int value;
        //Checking for loops. If it's in here, that means this is a circular dependancy, just return 0 for this one.
        if(checking.contains(item)){
            return 0;
        }
        checking.add(item);

        RecipeWrapper input = itemList.get(item);
        value = input.getValue(vm);

        checking.remove(item);
        
        return value;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public void loadRecipes(){
        if(manager == null){
            return;
        }
        itemList = new HashMap<Item,RecipeWrapper>();
        List<IRecipeCategory> categories = manager.getRecipeCategories();
        for (IRecipeCategory category : categories) {
            List recipes = manager.getRecipes(category);
            GTS.LOGGER.info("got recipes");
            for (Object recipe : recipes) {
                if (recipe instanceof IRecipe) {
                    IRecipe irecipe = (IRecipe)recipe;
                    ItemStack output = irecipe.getRecipeOutput();
                    ArrayList<ArrayList<ItemStack>> input = new ArrayList<ArrayList<ItemStack>>();
                    NonNullList<Ingredient> ingredients = irecipe.getIngredients();
                    for(Ingredient i : ingredients){
                        ArrayList<ItemStack> ingredientList = new ArrayList<ItemStack>();
                        ItemStack[] stacks = i.getMatchingStacks();
                        for(ItemStack stack : stacks){
                            ingredientList.add(stack);
                        }
                        input.add(ingredientList);
                    }
                    itemList.put(output.getItem(), new RecipeWrapper(output.getItem(),output.getCount(), input));
                }else{
                    if(recipe instanceof FuelRecipe ||recipe instanceof JeiBrewingRecipe||recipe instanceof AnvilRecipe){
                        //These recipes aren't currently supported
                    }else{
                        //GTS.LOGGER.error("non-recipe where recipe expected.");
                    }
                }
            }
        }
        loaded = true;

    }

    public static boolean isLoaded(){
        return loaded;
    }

    private class RecipeWrapper{
        private int outCount;
        private ArrayList<ArrayList<ItemStack>> input;

        public RecipeWrapper(Item output,int outCount, ArrayList<ArrayList<ItemStack>> input){
            this.outCount = outCount;
            this.input = input;
        }

        public int getValue(ValueManager vm){
            int value = 0;
            for(ArrayList<ItemStack> stacks : input){
                int ingValue = 0;
                for(ItemStack stack:stacks){
                    int itemValue = vm.getBaseValue(stack.getItem()) * stack.getCount();
                    if(itemValue > ingValue){
                        ingValue = itemValue;
                    }
                }
                //if this is still zero, none of the possible inputs for this slot have a value. That means we
                //won't be able to calculate a value, so just stop here and return 0;
                if(ingValue == 0){
                    return 0;
                }else{
                    value += ingValue;
                }
            }
            return value/outCount;
        }
    }

    
}