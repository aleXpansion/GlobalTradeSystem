package com.alexpansion.gts.utility;

import java.util.HashMap;
import java.util.Map;

import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.item.ItemCreditCard;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GTSUtil {

	public static HashMap<Item, Integer> baseValueMap = new HashMap<Item, Integer>();
	public static HashMap<Item, Integer> valueSoldMap = new HashMap<Item, Integer>();
	public static HashMap<Item, Double> changeMap = new HashMap<Item, Double>();
	public static int totalValueSold = 0;

	public static boolean canISell(Item item) {
		return baseValueMap.containsKey(item);

	}

	public static double getValue(Item item) {
		if (totalValueSold == 0) {
			totalValueSold = 1;
		}
		if (valueSoldMap.containsKey(item)) {
			int rampUp = ConfigurationHandler.rampUpCredits;
			double multiplier = ConfigurationHandler.depreciationMultiplier;
			multiplier = (totalValueSold / 15000) + 1;
			rampUp = 10000;
			int valueSold = valueSoldMap.get(item);
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
			return newValue;
		} else if (baseValueMap.containsKey(item)) {
			return baseValueMap.get(item);
		} else {
			return 0;
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
				output[i] = pair.getKey().getUnlocalizedName() + "," + pair.getValue();
			} else {
				output[i] = "null";
			}
			i++;
		}

		return output;
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
	public static int getValue(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		if (stack.getItem() == ModItems.CREDIT) {
			return stack.stackSize;
		} else if (stack.getItem() == ModItems.CREDIT_CARD) {
			return stack.getMaxDamage() - stack.getItemDamage();
		} else {
			return 0;
		}
	}

	/**
	 * Removes the given value from the given stack. If the given stack is not a
	 * credit-storing item, or the stack does not have enough value, throws an
	 * InsufficientValueExeption with the stack and the remaining value to be
	 * removed. 
	 * If the stack is valid for removing value, but does have enough
	 * value to remove as much as requested, it does remove the valued before
	 * throwing the exception.
	 * 
	 * @param stack		The stack to remove from
	 * @param toRemove	The amount to remove
	 * @return			The resulting stack
	 * @throws ValueOverflowException	Thrown if an invalid stack is given
	 */
	public static ItemStack removeValue(ItemStack stack, int toRemove) throws ValueOverflowException {
		if (stack == null) {
			throw new ValueOverflowException(stack, toRemove);
		}
		if (stack.getItem() == ModItems.CREDIT) {
			if (toRemove == stack.stackSize) {
				return null;
			} else if (toRemove < stack.stackSize) {
				stack.stackSize -= toRemove;
				return stack;
			} else if (toRemove > stack.stackSize) {
				int excess = toRemove - stack.stackSize;
				stack = null;
				throw new ValueOverflowException(stack, excess);
			}
		} else if (stack.getItem() == ModItems.CREDIT_CARD) {
			int value = stack.getMaxDamage() - stack.getItemDamage();
			if (value < toRemove) {
				int excess = toRemove - value;
				stack.setItemDamage(stack.getMaxDamage());
				throw new ValueOverflowException(stack, excess);
			} else if (value >= toRemove) {
				value -= toRemove;
				stack.setItemDamage(stack.getMaxDamage() - value);
				return stack;
			}
		} else {
			throw new ValueOverflowException(stack, toRemove);
		}
		LogHelper.error("U01: Unreachable code reached: the end of removeValue in GTSUtil");
		return null;
	}

	
	public static ItemStack addValue(ItemStack stack, int toAdd) throws ValueOverflowException {
		if(stack == null){
			stack = new ItemStack(ModItems.CREDIT);
			if(toAdd>stack.getMaxStackSize()){
				stack.stackSize = stack.getMaxStackSize();
				toAdd -= stack.getMaxStackSize();
				throw new ValueOverflowException(stack,toAdd);
			}else{
				stack.stackSize = toAdd;
				return stack;
			}
		}
		if(stack.getItem() instanceof ItemCreditCard){
			if(toAdd<=stack.getItemDamage()){
				stack.setItemDamage(stack.getItemDamage()-toAdd);
				return stack;
			}else{
				toAdd -= stack.getItemDamage();
				stack.setItemDamage(stack.getItemDamage());
				throw new ValueOverflowException(stack,toAdd);
			}
		}else if(stack.getItem()==ModItems.CREDIT){
			int toLimit = stack.getMaxStackSize()-stack.stackSize;
			if(toAdd <= toLimit){
				stack.stackSize += toAdd;
				return stack;
			}else{
				toAdd -= toLimit;
				stack.stackSize = stack.getMaxStackSize();
				throw new ValueOverflowException(stack,toAdd);
			}
		}else{
			throw new ValueOverflowException(stack,toAdd);
		}
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
		addSellableItemById(14, 1024);
		addSellableItemById(15, 128);
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
		addSellableItemById(264, 8192);
	}

	public static void addSellableItemById(int id, int value) {
		addSellableItem(Item.getItemById(id), value);
	}

	public static void addValueSold(Item item, int value) {
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
	}

	public static void addValueSold(Item item, double value) {
		if (changeMap.containsKey(item)) {
			value += changeMap.get(item);
		}
		if (value > 1 || value < -1) {
			addValueSold(item, (int) value);
		}
		changeMap.put(item, value % 1);
	}

	public static boolean canIBuy(Item item) {
		return valueSoldMap.containsKey(item);
	}

}
