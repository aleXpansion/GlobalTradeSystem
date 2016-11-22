package com.alexpansion.gts.item;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.handler.ModGuiHandler;
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
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
			if (hand == EnumHand.MAIN_HAND) {
				if (!playerIn.isSneaking()) {
					playerIn.openGui(GlobalTradeSystem.instance, ModGuiHandler.ITEM_CATALOG_GUI, worldIn, 0, 0, 0);
				}
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
