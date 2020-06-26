package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import com.alexpansion.gts.Config;
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
	static ArrayList<String> itemDefaults = new ArrayList<String>();
	static ArrayList<String> tagDefaults = new ArrayList<String>();

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

	public static ArrayList<String> getTagDefaults(){
		return tagDefaults;
	}

	public static ArrayList<String> getItemDefaults(){
		return itemDefaults;
	}

	public static void loadTagDefaults(List<String>inTags){
		for(String line : inTags){
			String[] splitLine = line.split(",");
			addTagValue(splitLine[0], Integer.parseInt(splitLine[1]));
		}
	}

	public static void loadItemDefaults(List<String>inTags){
		for(String line : inTags){
			String[] splitLine = line.split(",");
			if(splitLine.length < 2){
				GTS.LOGGER.error("Improperly formatted line in GTS value config:"+line);
			}else{
				addSellableItem(splitLine[0], Integer.parseInt(splitLine[1].trim()));
			}
		}
	}

	//reloads all base values from config
	public static void initItemValues(){
		baseValueMap.clear(); 
		loadTagDefaults(Config.DEFAULT_TAG_VALUES.get());
		loadItemDefaults(Config.DEFAULT_ITEM_VALUES.get());
	}

	public static void initDefaultValues() {
		tagDefaults.add("minecraft:logs,32");
		tagDefaults.add("minecraft:leaves,1");
		tagDefaults.add("minecraft:sand,1");
		tagDefaults.add("minecraft:saplings,32");
		tagDefaults.add("minecraft:small_flowers,16");
		tagDefaults.add("minecraft:wool,48");
		tagDefaults.add("forge:cobblestone,1");
		tagDefaults.add("forge:stone,1");
		itemDefaults.add("minecraft:dirt, 1");
		itemDefaults.add("minecraft:gunpowder, 192");
		itemDefaults.add("minecraft:feather, 48");
		itemDefaults.add("minecraft:string, 12");
		itemDefaults.add("minecraft:apple, 128");
		itemDefaults.add("minecraft:iron_ore, 256");
		itemDefaults.add("minecraft:rotten_flesh, 24");
		itemDefaults.add("minecraft:sugar_cane, 32");
		itemDefaults.add("minecraft:clay_ball, 64");
		itemDefaults.add("minecraft:flint, 4");
		itemDefaults.add("minecraft:redstone_ore, 64");
		itemDefaults.add("minecraft:coal_ore, 128");
		itemDefaults.add("minecraft:wheat, 24");
		itemDefaults.add("minecraft:wheat_seeds, 16");
		itemDefaults.add("minecraft:bone, 96");
		itemDefaults.add("minecraft:gold_ore, 2048");
		itemDefaults.add("minecraft:leather, 64");
		itemDefaults.add("minecraft:beef, 64");
		itemDefaults.add("minecraft:pumpkin, 144");
		itemDefaults.add("minecraft:sweet_berries, 16");
		itemDefaults.add("minecraft:lapis_ore, 864");
		itemDefaults.add("minecraft:diamond_ore, 8192");
		itemDefaults.add("minecraft:gravel, 1");
		itemDefaults.add("minecraft:emerald_ore,8192");
		itemDefaults.add("minecraft:potato:24");
		itemDefaults.add("minecraft:carrot:24");
		itemDefaults.add("minecraft:egg:32");
	}
}
