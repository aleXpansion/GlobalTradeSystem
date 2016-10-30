package com.alexpansion.gts.crafting;

import com.alexpansion.gts.init.ModBlocks;
import com.alexpansion.gts.init.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {
	
	public static void init(){
		
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TRADER), new Object[] {"*#*","#@#","*#*",'#',Items.IRON_INGOT,'@',Blocks.CHEST,'*',Items.GOLD_NUGGET});
		ItemStack newCard = new ItemStack(ModItems.CREDIT_CARD);
		newCard.setItemDamage(1000);
		GameRegistry.addShapedRecipe(newCard, new Object[] {" # ","#@#"," # ",'#',ModItems.CREDIT,'@',Items.PAPER});
		newCard = new ItemStack(ModItems.CREDIT_CARD2);
		newCard.setItemDamage(1000);
		GameRegistry.addShapedRecipe(newCard, new Object[] {" # ","#@#"," # ",'#',ModItems.CREDIT,'@',ModItems.CREDIT_CARD});
	}

}
