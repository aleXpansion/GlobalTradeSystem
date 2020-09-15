package com.alexpansion.gts.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerClient;
import com.alexpansion.gts.value.ValueWrapper;
import com.alexpansion.gts.value.ValueWrapperItem;

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
import net.minecraftforge.fml.ModList;

@JeiPlugin
public class JEIloader implements IModPlugin {

    private static IRecipeManager manager;
    private static boolean loaded = false;
    private static HashMap<ValueWrapper,ArrayList<RecipeWrapper>> wrapperList;

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
        mek = ModList.get().isLoaded("mekanism");
        loadRecipes();
        loaded = true;
    }

    private static ArrayList<ValueWrapper> checking = new ArrayList<ValueWrapper>();

    public static int getCrafingValue(ValueManager vm, ValueWrapper wrapper) {
        if(!wrapperList.containsKey(wrapper)){
            return 0;
        }
        int value;
        //Checking for loops. If it's in here, that means this is a circular dependancy, just return 0 for this one.
        if(checking.contains(wrapper)){
            return 0;
        }
        checking.add(wrapper);


        ArrayList<RecipeWrapper> inputs = wrapperList.get(wrapper);
        value = 0;
        for(RecipeWrapper input : inputs){
            int inputValue = input.getValue(vm);
            if(inputValue != 0 && (value == 0 || inputValue < value)){
                value = inputValue;
            }
        }
        checking.remove(wrapper);
        
        return value;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public void loadRecipes(){
        if(manager == null){
            return;
        }
        ValueManagerClient vm = ValueManager.getClientVM();
        wrapperList = new HashMap<ValueWrapper,ArrayList<RecipeWrapper>>();
        List<IRecipeCategory<?>> categories = manager.getRecipeCategories();
        for (IRecipeCategory category : categories) {
            List recipes = manager.getRecipes(category);
            String out = "loading "+ category.getTitle();
            if(recipes.get(0) instanceof IRecipe){
                out += " Success!";
            }else{
                out += " fail";
                out += " " + recipes.get(0).toString();
            }
            GTS.LOGGER.info(out);
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
                    RecipeWrapper recipeWrapper = new RecipeWrapper(output.getItem(),output.getCount(), input);
                    ValueWrapperItem  outWrapper = vm.getWrapper(output.getItem());
                    if(wrapperList.containsKey(outWrapper)){
                        wrapperList.get(outWrapper).add(recipeWrapper);
                    }else{
                        ArrayList<RecipeWrapper> list = new ArrayList<RecipeWrapper>();
                        list.add(recipeWrapper);
                        wrapperList.put(outWrapper, list);
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