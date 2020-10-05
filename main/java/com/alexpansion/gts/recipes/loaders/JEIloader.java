package com.alexpansion.gts.recipes.loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.wrappers.ValueWrapper;
import com.alexpansion.gts.value.wrappers.ValueWrapperFluid;
import com.alexpansion.gts.value.wrappers.ValueWrapperItem;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
        loadRecipes();
        loaded = true;
    }

    private static ArrayList<ValueWrapper> checking = new ArrayList<ValueWrapper>();

    public static int getCrafingValue(ValueWrapper wrapper) {
        if(!isLoaded()) return 0;
        if(wrapperList == null){
            GTS.LOGGER.error("huh? JEIloader");
        }
        //Checking for loops. If it's in here, that means this is a circular dependancy, just return 0 for this one.
        if(checking.contains(wrapper)){
            return 0;
        }
        checking.add(wrapper);
        int value;
        if(wrapper == null || !wrapperList.containsKey(wrapper)){
            if(wrapper instanceof ValueWrapperItem){
                ValueWrapperItem itemWrapper = (ValueWrapperItem) wrapper;
                Item item = itemWrapper.getItem();
                if(item instanceof BucketItem){
                    Fluid fluid = ((BucketItem)item).getFluid();
                    ValueWrapperFluid fluidWrapper = ValueWrapperFluid.get(fluid, true);
                    ValueWrapperItem bucketWrapper = ValueWrapperItem.get(item, true);
                    value = fluidWrapper.getBaseValue() + bucketWrapper.getBaseValue();
                }
            }
            value = 0;
        }else{
            ArrayList<RecipeWrapper> inputs = wrapperList.get(wrapper);
            value = 0;
            for(RecipeWrapper input : inputs){
                int inputValue = input.getValue();
                if(inputValue != 0 && (value == 0 || inputValue < value)){
                    value = inputValue;
                }
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
        ArrayList<RecipeLoader> loaders = new ArrayList<RecipeLoader>();
        loaders.add(new VanillaRecipeLoader());
        
        if(ModList.get().isLoaded("mekanism")){
            loaders.add(new MekanismRecipeLoader());
        }
        wrapperList = new HashMap<ValueWrapper,ArrayList<RecipeWrapper>>();
        List<IRecipeCategory<?>> categories = manager.getRecipeCategories();
        categories:
        for (IRecipeCategory category : categories) {
            String title = category.getTitle();
            List recipes = manager.getRecipes(category);

            for(RecipeLoader loader : loaders){
                if(loader.hasCategory(title)){
                    wrapperList = loader.loadRecipes(title, recipes, wrapperList);
                    continue categories;
                }
            }
            Object testRecipe = recipes.get(0);
            if(testRecipe instanceof IRecipe){
                IRecipe test = (IRecipe)testRecipe;
                if(test.getRecipeOutput() != ItemStack.EMPTY && test.getIngredients().size() > 0){
                    new VanillaRecipeLoader().loadRecipes(title, recipes, wrapperList);
                }
            }
            GTS.LOGGER.info("No loader found for "+title);
        }
        loaded = true;

    }

    public static boolean isLoaded(){
        return loaded;
    }

   
    
}

class RecipeWrapper{
    private int outCount;
    //These are the ingredients. The out arraylist represents each slotk, the inner which items can go in that slot.
    private ArrayList<ArrayList<ValueWrapper>> input;

    public RecipeWrapper(Item output,int outCount, ArrayList<ArrayList<ValueWrapper>> input){
        this.outCount = outCount;
        this.input = input;
    }

    public int getValue(){
        int value = 0;
        for(ArrayList<ValueWrapper> wrappers : input){
            int ingValue = 0;
            for(ValueWrapper wrapper:wrappers){
                if(wrapper == null){
                    continue;
                }
                int itemValue = wrapper.getBaseValue();
                if(ingValue == 0 || (itemValue != 0 && itemValue <ingValue)){
                    ingValue = itemValue;
                }
            }
            //if this is still zero, none of the possible inputs for this slot have a value. That means we
            //won't be able to calculate a value, so just stop here and return 0;
            if(wrappers.size() > 0 && ingValue == 0){
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