package com.alexpansion.gts.recipes.loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexpansion.gts.value.wrappers.ValueWrapper;

public abstract class RecipeLoader {
    
    public abstract HashMap<ValueWrapper,ArrayList<RecipeWrapper>> loadRecipes(String category,List<Object> recipes,HashMap<ValueWrapper,ArrayList<RecipeWrapper>> wrapperList);

    public abstract boolean hasCategory(String category);
}
