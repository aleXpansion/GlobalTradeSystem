package com.alexpansion.gts.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.item.IValueContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GTSUtil {

	public static HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	public static HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	public static HashMap<Item, Double> changeMap = new HashMap<Item, Double>();
	public static HashMap<Item, Double> valueMap = new HashMap<Item, Double>();
	public static int totalValueSold = 0;
	private static boolean valuesLoaded = false;
	private static int calcCount = 0;
	private static Item toRemove = null;

	@Deprecated
	public static boolean canISell(Item item) {
		return baseValueMap.containsKey(item);
	}

	
	@Deprecated
	public static double getValue(Item item) {
		if (!canISell(item)) {
			return 0;
		}
		if (!valueMap.containsKey(item)) {
			calculateValue(item);
		}
		return valueMap.get(item);
	}

	public static void calculateValues() {
		LogHelper.info("recalculating all values");
		for (Item key : valueSoldMap.keySet()) {
			calculateValue(key);
		}
		if(toRemove != null){
			valueSoldMap.remove(toRemove);
			toRemove = null;
		}
		calcCount = 0;
	}

	public static void calculateValue(Item item) {
		if (totalValueSold == 0) {
			totalValueSold = 1;
		}
		if (valueSoldMap.containsKey(item)) {
			int rampUp = ConfigurationHandler.rampUpCredits;
			double multiplier = ConfigurationHandler.depreciationMultiplier;
			// multiplier = (totalValueSold / 15000) + 1;
			multiplier = 1;
			rampUp = 10000;
			int valueSold = valueSoldMap.get(item);
			if(baseValueMap.get(item)==null){
				toRemove = item;
				return;
			}
			double newValue = baseValueMap.get(item);
			double loss = 0;
			if (totalValueSold < rampUp) {
				newValue = ((rampUp - totalValueSold) / (double) rampUp) * newValue
						+ (totalValueSold / (double) rampUp) * ((totalValueSold - valueSold) / (totalValueSold));
			} else {
				loss = newValue * ((valueSold) / ((double) totalValueSold)) * multiplier;
				newValue -= loss;
			}
			// LogHelper.info(item.getUnlocalizedName() + " is worth " +
			// newValue);
			valueMap.put(item, newValue);
		} else if (baseValueMap.containsKey(item)) {
			int base = baseValueMap.get(item);
			valueMap.put(item, (double) base);
		}
	}

	public static void addSellableItem(Item item, int value) {
		baseValueMap.put(item, value);
	}

	public static String[] getDefaultValues() {
		initItemValues();
		String[] output = new String[baseValueMap.size()];

		int i = 0;
		for (Map.Entry<Item, Integer> pair : baseValueMap.entrySet()) {
			if (pair.getKey() != null) {
				output[i] = pair.getKey().getRegistryName() + "," + pair.getValue();
			} else {
				output[i] = "null";
			}
			i++;
		}

		return output;
	}
	
	@Deprecated
	public static int getBaseValue(Item item){
		if(baseValueMap.containsKey(item)){
			return baseValueMap.get(item);
		}else{
			return 0;
		}
	}
	/**
	 * Gets the value in credits of a given stack. This only works for items
	 * that store credits, such as credits and credit cards. If the item is not
	 * recognized, returns 0.
	 * 
	 * @param stack
	 *            The stack to be examined
	 * @return int The value of the examined stack, in credits
	 */
	@Deprecated
	public static int getValue(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof IValueContainer) {
			return ((IValueContainer) stack.getItem()).getValue(stack);
		} else {
			return 0;
		}
	}

	public static ArrayList<Item> getAllSellableItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item key : baseValueMap.keySet()) {
			items.add(key);
		}
		return items;
	}
	
	public static ArrayList<Item> getAllBuyableItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item key : valueSoldMap.keySet()) {
			items.add(key);
		}
		return items;
	}

	public static ArrayList<Item> getAllBuyableItems(int limit) {
		ArrayList<Item> items = getAllBuyableItems();
		ArrayList<Item> newItems = new ArrayList<Item>();
		for (Item item : items) {
			if (getValue(item) <= limit) {
				newItems.add(item);
			}
		}
		return newItems;
	}

	public static ArrayList<Item> getAllBuyableItemsSorted(int limit){
		ArrayList<Item> oldList = getAllBuyableItems(limit);
		ArrayList<Item> newList = new ArrayList<Item>();
		while(oldList.size()>0){
			Double top = (double) 0;
			Item topItem = null;
			for(Item item:oldList){
				if(getValue(item)>top){
					top = getValue(item);
					topItem = item;
				}
			}
			if(topItem == null){
				LogHelper.error("topItem was null in GTSUtil.getAllSellableItems");
				return newList;
			}
			newList.add(topItem);
			oldList.remove(topItem);
		}
		return newList;
	}
	
	public static void initItemValues() {
		addSellableItemById(1, 1);
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
		addSellableItemByRegistryName("minecraft:gunpowder",192);
		addSellableItemByRegistryName("minecraft:feather",48);
		addSellableItemByRegistryName("minecraft:string",12);
		addSellableItemByRegistryName("minecraft:apple",128);
		addSellableItemByRegistryName("minecraft:hopper",1344);
		addSellableItemByRegistryName("minecraft:coal_block",1152);
		addSellableItemByRegistryName("minecraft:clay_ball",64);
		addSellableItemByRegistryName("minecraft:iron_ingot",256);
		addSellableItemByRegistryName("minecraft:rotten_flesh",24);
		addSellableItemByRegistryName("minecraft:reeds",32);
		addSellableItemByRegistryName("minecraft:flint",4);
		addSellableItemByRegistryName("minecraft:redstone",64);
		addSellableItemByRegistryName("minecraft:stick",4);
		addSellableItemByRegistryName("minecraft:coal",128);
		addSellableItemByRegistryName("minecraft:wheat",24);
		addSellableItemByRegistryName("minecraft:wheat_seeds",16);
		addSellableItemByRegistryName("minecraft:arrow",14);
		addSellableItemByRegistryName("minecraft:bone",96);
		addSellableItemByRegistryName("minecraft:gold_ingot",2048);
		addSellableItemByRegistryName("minecraft:paper",32);
		addSellableItemByRegistryName("minecraft:sugar",32);
		addSellableItemByRegistryName("minecraft:book",96);
		addSellableItemByRegistryName("minecraft:leather",64);
		addSellableItemByRegistryName("minecraft:beef",64);
		addSellableItemByRegistryName("minecraft:cooked_beef",64);
		addSellableItemByRegistryName("minecraft:bread",72);
	}

	public static void addSellableItemById(int id, int value) {
		addSellableItem(Item.getItemById(id), value);
	}

	public static void addValueSold(Item item, int value, World world) {
		ValueSavedData data = ValueSavedData.get(world);
		if (!valueSoldMap.containsKey(item)) {
			valueSoldMap.put(item, value);
		} else {
			valueSoldMap.put(item, valueSoldMap.get(item) + value);
		}
		totalValueSold += value;
		LogHelper.info("Total value is now at " + totalValueSold);
		LogHelper.info("Added " + value + " value to " + item.getUnlocalizedName() + " for a total of "
				+ valueSoldMap.get(item) + ". "
				+ (int) Math.floor((double) valueSoldMap.get(item) / totalValueSold * 100)
				+ " percent of total sales.");

		calculateValue(item);
		data.saveValues(valueSoldMap);
		data.setTotal(totalValueSold);

	}

	public static double getValuePercentage(Item item) {
		if (!canIBuy(item)) {
			return 0;
		} else {
			return Math.floor(((double) valueSoldMap.get(item) / totalValueSold * 100) * 100) / 100;
		}
	}

	public static void addValueSold(Item item, double value, World world) {
		if (changeMap.containsKey(item)) {
			value += changeMap.get(item);
		}
		if (value > 1 || value < -1) {
			addValueSold(item, (int) value, world);
		}
		changeMap.put(item, value % 1);
		if (calcCount++ > 5) {
			calculateValues();
		}
	}

	@Deprecated
	public static boolean canIBuy(Item item) {
		return valueSoldMap.containsKey(item);
	}

	public static void updateBaseValues(String[] input) {
		HashMap<Item, Integer> newMap = new HashMap<Item, Integer>();

		for (String line : input) {
			if ("null".equalsIgnoreCase(line)) {
				continue;
			}
			String[] values = line.split(",");
			Item key = getItemFromRegistryName(values[0]);
			if (key != null) {
				newMap.put(key, Integer.parseInt(values[1]));
			}
		}

		baseValueMap = newMap;

	}

	@SuppressWarnings("deprecation")
	public static Item getItemFromRegistryName(String name) {
		try {
			String[] splitName = name.split(":");
			return GameRegistry.findItem(splitName[0], splitName[1]);
		} catch (NullPointerException e) {
			LogHelper.error("NPE in getItemFromUnlocalizedName with value: " + name);
			return null;
		} catch (IndexOutOfBoundsException e){
			LogHelper.error("IOOB Exception in getItemFromRegistryName. Input:"+name);
			return null;
		}
	}
	
	public static void addSellableItemByRegistryName(String name,int value){
		addSellableItem(getItemFromRegistryName(name),value);
	}

	public static void loadValues(World world) {
		valuesLoaded = true;
		ValueSavedData data = ValueSavedData.get(world);
		if (!data.areValuesLoaded()) {
			valuesLoaded = false;
			data.markDirty();
		} else {
			valueSoldMap = data.getValues();
			totalValueSold = data.getTotal();
		}
	}

	public static boolean areValuesLoaded() {
		return valuesLoaded;
	}

}
