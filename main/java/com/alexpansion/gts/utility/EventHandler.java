package com.alexpansion.gts.utility;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

	public static EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		ValueManager manager = ValueManager.getManager(event.getEntityPlayer().worldObj);
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		if (manager.canISell(item)) {
			List<String> tooltips = event.getToolTip();
			double value = manager.getValue(stack.getItem());
			tooltips.add("Credit Value: " + (Math.floor(value*100))/100);
			tooltips.add("Base Value: " + manager.getBaseValue(item));
		}
	}

}
