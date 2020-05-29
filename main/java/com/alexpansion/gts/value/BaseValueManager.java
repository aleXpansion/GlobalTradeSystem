package com.alexpansion.gts.value;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.GTS;

import net.minecraft.item.Item;
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

	public static void addSellableItem(String name,int value){
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
	
	public static void initItemValues() {
		GTS.LOGGER.info("init values");
		addSellableItem("minecraft:dirt", 1);
		addSellableItem("minecraft:cobblestone", 1);
	}
		/*
		addVarietiesById(1, 0, 6, 1);
		addVarietiesById(351, 5, 14, 8);
		addSellableItem(SItem.getSItem("minecraft:dye/15"), 48);
		addSellableItem(SItem.getSItem("minecraft:dye/4"), 864);
		addSellableItemById(2, 1);
		addVarietiesById(3, 0, 2, 1);
		addSellableItemById(4, 1);
		addVarietiesById(5, 0, 5, 8);
		addVarietiesById(6, 0, 5, 32);
		addVarietiesById(12, 0, 1, 1);
		addSellableItemById(13, 4);
		addSellableItemById(14, 4096);
		addSellableItemById(15, 512);
		addSellableItemById(16, 64);
		addVarietiesById(17, 0, 3, 32);
		addVarietiesById(18, 0, 3, 1);
		addSellableItemById(20, 1);
		addSellableItemById(21, 108);
		addSellableItemById(22, 7776);
		addSellableItemById(23, 119);
		addVarietiesById(24, 0, 2, 4);
		addSellableItemById(25, 128);
		addSellableItemById(26, 144);
		addSellableItemById(27, 2048);
		addSellableItemById(28, 256);
		addSellableItemById(29, 372);
		addSellableItemById(30, 12);
		addSellableItemById(31, 1);
		addSellableItemById(32, 1);
		addSellableItemById(33, 348);
		addVarietiesById(35, 0, 15, 48);
		addSellableItemById(37, 16);
		addVarietiesById(38, 0, 8, 16);
		addSellableItemById(39, 32);
		addSellableItemById(40, 32);
		addSellableItemById(41, 18432);
		addSellableItemById(42, 2304);
		addSellableItemById(43, 1);
		addSellableItemById(44, 1);
		addSellableItem(SItem.getSItem("minecraft:stone_slab/1"), 2);
		addSellableItem(SItem.getSItem("minecraft:stone_slab/3"), 1);
		addSellableItem(SItem.getSItem("minecraft:stone_slab/4"), 128);
		addSellableItem(SItem.getSItem("minecraft:stone_slab/5"), 2);
		addSellableItem(SItem.getSItem("minecraft:stone_slab/6"), 2);
		addSellableItemById(45, 256);
		addSellableItemById(46, 964);
		addSellableItemById(47, 336);
		addSellableItemById(48, 1);
		addSellableItemById(49, 64);
		addSellableItemById(50, 9);
		addSellableItemById(53, 12);
		addSellableItemById(54, 64);
		addSellableItemById(56, 4096);
		addSellableItemById(57, 73728);
		addSellableItemById(58, 32);
		addSellableItemById(61, 8);
		addSellableItemById(64, 48);
		addSellableItemById(65, 14);
		addSellableItemById(66, 96);
		addSellableItemById(67, 1);
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
		addSellableItemById(91, 153);
		addSellableItemById(92, 384);
		addVarietiesById(95, 15, 1);
		addSellableItemById(96, 24);
		addVarietiesById(98, 3, 4);
		addSellableItemById(101, 96);
		addSellableItemById(102, 1);
		addSellableItemById(103, 144);
		addSellableItemById(106, 8);
		addSellableItemById(107, 32);
		addSellableItemById(108, 384);
		addSellableItemById(109, 6);
		addSellableItemById(110, 2);
		addSellableItemById(111, 16);
		addSellableItemById(112, 4);
		addSellableItemById(113, 4);
		addSellableItemById(114, 6);
		addSellableItemById(115, 24);
		addSellableItemById(116, 16736);
		addSellableItemById(117, 1539);
		addSellableItemById(118, 1792);
		addSellableItemById(121, 2);
		addSellableItemById(122, 73728);
		addSellableItemById(123, 1792);
		addVarietiesById(126, 5, 4);
		addSellableItemById(128, 6);
		addSellableItemById(129, 4096);
		addSellableItemById(130, 2304);
		addSellableItemById(131, 134);
		addSellableItemById(133, 73728);
		addSellableItemById(134, 12);
		addSellableItemById(135, 12);
		addSellableItemById(136, 12);
		addVarietiesById(139, 1, 1);
		addSellableItemById(143, 8);
		addSellableItem(SItem.getSItem("minecraft:anvil",0), 7936);
		addSellableItem(SItem.getSItem("minecraft:anvil",1), 5291);
		addSellableItem(SItem.getSItem("minecraft:anvil",2), 2645);
		addSellableItem(SItem.getSItem("minecraft:trapped_chest"), 198);
		addSellableItem(SItem.getSItem("minecraft:light_weighted_pressure_plate",0), 4096);
		addSellableItem(SItem.getSItem("minecraft:heavy_weighted_pressure_plate",0), 512);
		addSellableItem(SItem.getSItem("minecraft:activator_rail"), 269);
		addSellableItem(SItem.getSItem("minecraft:dropper"), 71);
		addVarietiesById(159, 15, 256);
		addVarietiesById(160, 15, 1);
		addVarietiesById(161, 1, 1);
		addVarietiesById(162, 1, 32);
		addSellableItemById(152, 576);
		addSellableItem(SItem.getSItem("minecraft:acacia_stairs"), 12);
		addSellableItem(SItem.getSItem("minecraft:dark_oak_stairs"), 12);
		addSellableItemById(165, 216);
		addSellableItem(SItem.getSItem("minecraft:iron_trapdoor"), 1024);
		addSellableItem(SItem.getSItem("minecraft:hay_block"), 216);
		addVarietiesById(171, 15, 32);
		addSellableItem(SItem.getSItem("minecraft:hardened_clay"), 256);
		addVarietiesById(175, 5, 32);
		addVarietiesById(179, 2, 4);
		addSellableItem(SItem.getSItem("minecraft:red_sandstone_stairs"), 6);
		addSellableItem(SItem.getSItem("minecraft:stone_slab2"), 2);
		addSellableItem(SItem.getSItem("minecraft:spruce_fence_gate"), 32);
		addSellableItem(SItem.getSItem("minecraft:birch_fence_gate"), 32);
		addSellableItem(SItem.getSItem("minecraft:jungle_fence_gate"), 32);
		addSellableItem(SItem.getSItem("minecraft:dark_oak_fence_gate"), 32);
		addSellableItem(SItem.getSItem("minecraft:acacia_fence_gate"), 32);
		addSellableItem(SItem.getSItem("minecraft:spruce_fence"), 12);
		addSellableItem(SItem.getSItem("minecraft:birch_fence"), 12);
		addSellableItem(SItem.getSItem("minecraft:jungle_fence"), 12);
		addSellableItem(SItem.getSItem("minecraft:dark_oak_fence"), 12);
		addSellableItem(SItem.getSItem("minecraft:acacia_fence"), 12);
		addSellableItem(SItem.getSItem("minecraft:end_bricks"), 2);
		addSellableItem(SItem.getSItem("minecraft:magma"), 3168);
		addSellableItem(SItem.getSItem("minecraft:nether_wart_block"), 216);
		addSellableItem(SItem.getSItem("minecraft:red_nether_brick"), 50);
		addSellableItem(SItem.getSItem("minecraft:bone_block"), 432);
		
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
	*/
	/*
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

	public static void addVarietiesById(int id, int max, int value) {
		addVarietiesById(id, 0, max, value);
	}
*/
}
