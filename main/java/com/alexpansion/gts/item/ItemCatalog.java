package com.alexpansion.gts.item;

import com.alexpansion.gts.network.PacketCatalogOpened;
import com.alexpansion.gts.network.PacketHandler;

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

	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (worldIn.isRemote) {
			PacketHandler.INSTANCE.sendToServer(new PacketCatalogOpened(itemStackIn));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}else{
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
		}

	}

}
