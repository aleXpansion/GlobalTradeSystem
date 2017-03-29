package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.item.Item;

public class BaseValueManager {

	static HashMap<SItem, Integer> baseValueMap = new HashMap<SItem, Integer>();

	public static void addSellableItem(SItem item, int value) {
		if (item != null) {
			baseValueMap.put(item, value);
		} else {
			LogHelper.error("Null item in BaseValueManager.addSellableItem!");
		}
	}

	public static String[] getDefaultValues() {
		initItemValues();
		String[] output = new String[baseValueMap.size()];

		int i = 0;
		for (Map.Entry<SItem, Integer> pair : baseValueMap.entrySet()) {
			if (pair.getKey() != null) {
				output[i] = pair.getKey().toString() + "," + pair.getValue();
				i++;
			} else {
				// output[i] = "null";
			}
		}

		return output;
	}

	public static void initItemValues() {
		addVarietiesById(1, 0, 6, 1);
		addVarietiesById(351,5,14,8);
		addSellableItem(SItem.getSItem("minecraft:dye/15"),48);
		addSellableItem(SItem.getSItem("minecraft:dye/4"),864);
		addSellableItemById(2, 1);
		addSellableItemById(3, 1);
		addSellableItemById(4, 1);
		addSellableItemById(5, 8);
		addSellableItemById(6, 32);
		addSellableItemById(12, 1);
		addSellableItemById(13, 4);
		addSellableItemById(14, 4096);
		addSellableItemById(15, 512);
		addSellableItemById(16, 64);
		addSellableItemById(17, 32);
		addSellableItemById(18, 1);
		addSellableItemById(20, 1);
		addSellableItemById(21, 108);
		addSellableItemById(22, 7776);
		addSellableItemById(23, 119);
		addSellableItemById(24, 4);
		addSellableItemById(25, 128);
		addSellableItemById(26, 144);
		addSellableItemById(27, 2048);
		addSellableItemById(28, 256);
		addSellableItemById(29, 372);
		addSellableItemById(30, 12);
		addSellableItemById(31, 1);
		addSellableItemById(32, 1);
		addSellableItemById(33, 348);
		addSellableItemById(35, 48);
		addSellableItemById(37, 16);
		addSellableItemById(38, 16);
		addSellableItemById(39, 32);
		addSellableItemById(40, 32);
		addSellableItemById(41, 18432);
		addSellableItemById(42, 2304);
		addSellableItemById(43, 1);
		addSellableItemById(44, 1);
		addSellableItemById(45, 256);
		addSellableItemById(46, 964);
		addSellableItemById(47, 336);
		addSellableItemById(48, 1);
		addSellableItemById(49, 64);
		addSellableItemById(50, 9);
		addSellableItemById(54, 64);
		addSellableItemById(56, 4096);
		addSellableItemById(57, 73728);
		addSellableItemById(58, 32);
		addSellableItemById(61, 8);
		addSellableItemById(64, 48);
		addSellableItemById(65, 14);
		addSellableItemById(66, 96);
		addSellableItemById(69, 5);
		addSellableItemById(70, 2);
		addSellableItemById(71, 1536);
		addSellableItemById(72, 16);
		addSellableItemById(73, 16);
		addSellableItemById(76, 68);
		addSellableItemById(77, 1);
		addSellableItemById(78, 1);
		addSellableItemById(79, 1);
		addSellableItemById(80, 1);
		addSellableItemById(81, 8);
		addSellableItemById(82, 64);
		addSellableItemById(83, 32);
		addSellableItemById(84, 8256);
		addSellableItemById(85, 12);
		addSellableItemById(86, 144);
		addSellableItemById(87, 1);
		addSellableItemById(88, 49);
		addSellableItemById(89, 1536);
		addSellableItemById(92, 384);
		addSellableItemById(95, 1);
		addSellableItemById(96, 24);
		addSellableItemById(98, 4);
		addSellableItemById(101, 96);
		addSellableItemById(102, 1);
		addSellableItemById(103, 144);
		addSellableItemById(106, 8);
		addSellableItemById(107, 32);
		addSellableItemById(110, 2);
		addSellableItemById(111, 16);
		addSellableItemById(112, 4);
		addSellableItemById(115, 24);
		addSellableItemById(116, 16736);
		addSellableItemById(117, 1539);
		addSellableItemById(118, 1792);
		addSellableItemById(121, 2);
		addSellableItemById(122, 73728);
		addSellableItemById(123, 1792);
		addSellableItemById(129, 4096);
		addSellableItemById(130, 2304);
		addSellableItemById(131, 134);
		addSellableItemById(133, 73728);
		addSellableItemById(143, 8);
		addSellableItemById(152, 576);
		addSellableItemById(159, 256);
		addSellableItemById(162, 32);
		addSellableItemById(165, 216);
		addSellableItemById(264, 8192);
		addSellableItemByRegistryName("minecraft:gunpowder", 192);
		addSellableItemByRegistryName("minecraft:feather", 48);
		addSellableItemByRegistryName("minecraft:string", 12);
		addSellableItemByRegistryName("minecraft:apple", 128);
		addSellableItemByRegistryName("minecraft:hopper", 1344);
		addSellableItemByRegistryName("minecraft:coal_block", 1152);
		addSellableItemByRegistryName("minecraft:clay_ball", 64);
		addSellableItemByRegistryName("minecraft:iron_ingot", 256);
		addSellableItemByRegistryName("minecraft:rotten_flesh", 24);
		addSellableItemByRegistryName("minecraft:reeds", 32);
		addSellableItemByRegistryName("minecraft:flint", 4);
		addSellableItemByRegistryName("minecraft:redstone", 64);
		addSellableItemByRegistryName("minecraft:stick", 4);
		addSellableItemByRegistryName("minecraft:coal", 128);
		addSellableItemByRegistryName("minecraft:wheat", 24);
		addSellableItemByRegistryName("minecraft:wheat_seeds", 16);
		addSellableItemByRegistryName("minecraft:arrow", 14);
		addSellableItemByRegistryName("minecraft:bone", 96);
		addSellableItemByRegistryName("minecraft:gold_ingot", 2048);
		addSellableItemByRegistryName("minecraft:paper", 32);
		addSellableItemByRegistryName("minecraft:sugar", 32);
		addSellableItemByRegistryName("minecraft:book", 96);
		addSellableItemByRegistryName("minecraft:leather", 64);
		addSellableItemByRegistryName("minecraft:beef", 64);
		addSellableItemByRegistryName("minecraft:cooked_beef", 64);
		addSellableItemByRegistryName("minecraft:bread", 72);
	}

	public static void addSellableItemById(int id, int value) {
		addSellableItemById(id, 0, value);
	}

	public static void addSellableItemById(int id, int meta, int value) {
		Item item = Item.getItemById(id);
		if (item == null) {
			LogHelper.error("Null item returned for id: " + id);
		} else {
			addSellableItem(SItem.getSItem(item, meta), value);
		}
	}

	public static void addSellableItemByRegistryName(String name, int value) {
		addSellableItem(SItem.getSItem(name + "/0"), value);
	}

	public static void addSellableItemByRegistryName(String name, int meta, int value) {
		addSellableItem(SItem.getSItem(name + "/" + meta), value);
	}

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

	public static void addVarietiesById(int id, int min, int max, int value) {
		for (int i = min; i <= max; i++) {
			addSellableItemById(id, i, value);
		}
	}

}
