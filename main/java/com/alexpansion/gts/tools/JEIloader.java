package com.alexpansion.gts.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
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
    private static HashMap<Item,ArrayList<RecipeWrapper>> itemList;

    public JEIloader() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GTS.MOD_ID, "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        GTS.LOGGER.info("Loading JEI runtime.");
        IJeiRuntime runtime = jeiRuntime;
        manager = runtime.getRecipeManager();
        loadRecipes();
        loaded = true;
    }

    private static ArrayList<Item> checking = new ArrayList<Item>();

    public static int getCrafingValue(ValueManager vm, Item item) {
        if(!itemList.containsKey(item)){
            return 0;
        }
        int value;
        //Checking for loops. If it's in here, that means this is a circular dependancy, just return 0 for this one.
        if(checking.contains(item)){
            return 0;
        }
        checking.add(item);


        ArrayList<RecipeWrapper> inputs = itemList.get(item);
        value = 0;
        for(RecipeWrapper input : inputs){
            int inputValue = input.getValue(vm);
            if(value == 0 || inputValue < value){
                value = inputValue;
            }
        }
        checking.remove(item);
        
        return value;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public void loadRecipes(){
        if(manager == null){
            return;
        }
        itemList = new HashMap<Item,ArrayList<RecipeWrapper>>();
        List<IRecipeCategory> categories = manager.getRecipeCategories();
        for (IRecipeCategory category : categories) {
            List recipes = manager.getRecipes(category);
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
                    RecipeWrapper wrapper = new RecipeWrapper(output.getItem(),output.getCount(), input);
                    if(itemList.containsKey(output.getItem())){
                        itemList.get(output.getItem()).add(wrapper);
                    }else{
                        ArrayList<RecipeWrapper> list = new ArrayList<RecipeWrapper>();
                        list.add(wrapper);
                        itemList.put(output.getItem(), list);
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
        //These are the ingredients. The out arraylist represents each slotk, the inner which items can go in that slot.
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
                    if(stack.isEmpty()){continue;}
                    int itemValue = vm.getBaseValue(stack.getItem()) * stack.getCount();
                    if(ingValue == 0 || (itemValue != 0 && itemValue <ingValue)){
                        ingValue = itemValue;
                    }
                }
                //if this is still zero, none of the possible inputs for this slot have a value. That means we
                //won't be able to calculate a value, so just stop here and return 0;
                if(stacks.size() > 0 && ingValue == 0){
                    return 0;
                }else{
                    value += ingValue;
                }
            }
            double rawValue = (double)value/outCount;
            //round down to a minimum of 1
            if(rawValue >0 && rawValue < 1){
                return 1;
            }else{
                return (int)rawValue;
            }
        }
    }

    
}