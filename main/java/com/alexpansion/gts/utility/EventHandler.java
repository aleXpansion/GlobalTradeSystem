package com.alexpansion.gts.utility;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

	public static EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		ValueManager manager = ValueManager.getManager(event.getEntityPlayer().worldObj);
		ItemStack stack = event.getItemStack();
		SItem item = SItem.getSItem(stack);
		if (manager.canISell(item)) {
			List<String> tooltips = event.getToolTip();
			double value = manager.getValue(item);
			tooltips.add("Credit Value: " + (Math.floor(value*100))/100);
			tooltips.add("Base Value: " + manager.getBaseValue(item));
		}
	}

}
