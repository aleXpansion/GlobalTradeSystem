package com.alexpansion.gts.item;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.handler.ModGuiHandler;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCatalog extends ItemCreditCard {

	public ItemCatalog() {
		super("catalog", 500000);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		return 1;
	}
	
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (worldIn.isRemote) {
			if(!playerIn.isSneaking()){
				LogHelper.info("Opening GUI!");
				playerIn.openGui(GlobalTradeSystem.instance, ModGuiHandler.ITEM_CATALOG_GUI, worldIn, 0, 0, 0);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}else{
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
		}

	}

}
