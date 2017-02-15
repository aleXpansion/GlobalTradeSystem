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
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		if (GTSUtil.canISell(item)) {
			List<String> tooltips = event.getToolTip();
			double value = GTSUtil.getValue(stack.getItem());
			tooltips.add("Credit Value: " + (Math.floor(value*100))/100);
			tooltips.add("Base Value: " + GTSUtil.getBaseValue(item));
		}
	}

}
