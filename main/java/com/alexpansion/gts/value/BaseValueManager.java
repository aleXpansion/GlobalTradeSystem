package com.alexpansion.gts.value;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class BaseValueManager {

	static HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();

	private static IForgeRegistry<Item> itemReg = ForgeRegistries.ITEMS;

	public static void addSellableItem(Item item, int value) {
		if (item != null) {
			baseValueMap.put(item, value);
		} else {
			GTS.LOGGER.error("Null item in BaseValueManager.addSellableItem!");
		}
	}

	public static void addSellableItem(String name, int value) {
		ResourceLocation rl = new ResourceLocation(name);
		Item item = itemReg.getValue(rl);
		addSellableItem(item, value);
	}

	public static String[] getDefaultValues() {
		initItemValues();
		String[] output = new String[baseValueMap.size()];

		int i = 0;
		for (Map.Entry<Item, Integer> pair : baseValueMap.entrySet()) {
			if (pair.getKey() != null) {
				output[i] = pair.getKey().toString() + "," + pair.getValue();
				i++;
			} else {
				// output[i] = "null";
			}
		}

		return output;
	}

	public static void addTagValue(Tag<Item> tag, int value) {
		Collection<Item> items = tag.getAllElements();
		for (Item item : items) {
			addSellableItem(item, value);
		}
	}

	public static void addTagValue(String tagString, int value) {
		TagCollection<Item> tags = ItemTags.getCollection();
		ResourceLocation rl = new ResourceLocation(tagString);
		Tag<Item> tag = tags.get(rl);
		addTagValue(tag, value);
	}
	
	public static void initItemValues() {
		addTagValue(ItemTags.LOGS, 32);
		addTagValue(ItemTags.LEAVES, 1);
		addTagValue(ItemTags.SAND, 1);
		addTagValue(ItemTags.SAPLINGS, 32);
		addTagValue(ItemTags.SMALL_FLOWERS, 16);
		addTagValue(ItemTags.WOOL, 48);
		addTagValue("forge:cobblestone", 1);
		addSellableItem("minecraft:dirt", 1);
		addSellableItem("minecraft:cobblestone", 1);
		addSellableItem("minecraft:gunpowder", 192);
		addSellableItem("minecraft:feather", 48);
		addSellableItem("minecraft:string", 12);
		addSellableItem("minecraft:apple", 128);
		addSellableItem("minecraft:iron_ore", 256);
		addSellableItem("minecraft:rotten_flesh", 24);
		addSellableItem("minecraft:reeds", 32);
		addSellableItem("minecraft:clay_ball", 64);
		addSellableItem("minecraft:flint", 4);
		addSellableItem("minecraft:redstone_ore", 64);
		addSellableItem("minecraft:coal_ore", 128);
		addSellableItem("minecraft:wheat", 24);
		addSellableItem("minecraft:wheat_seeds", 16);
		addSellableItem("minecraft:bone", 96);
		addSellableItem("minecraft:gold_ore", 2048);
		addSellableItem("minecraft:leather", 64);
		addSellableItem("minecraft:beef", 64);
		addSellableItem("minecraft:pumpkin", 144);
		addSellableItem("minecraft:sweet_berries", 16);
		addSellableItem("minecraft:lapis_ore", 864);
		addSellableItem("minecraft:diamond_ore", 8192);
		addSellableItem("minecraft:andesite", 1);
		addSellableItem("minecraft:diorite", 1);
		addSellableItem("minecraft:granite", 1);
		addSellableItem("minecraft:gravel", 1);
	}
	
	/*

	public static void updateBaseValues(String[] input) {
		HashMap<SItem, Integer> newMap = new HashMap<SItem, Integer>();

		for (String line : input) {
			if ("null".equalsIgnoreCase(line)) {
				continue;
			}
			String[] values = line.split(",");
			SItem key = SItem.getSItem(values[0]);
			if (key != null) {
				newMap.put(key, Integer.parseInt(values[1]));
			}
		}

		baseValueMap = newMap;

	}
*/
}
