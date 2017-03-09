package com.alexpansion.gts.utility;

import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class ValueManager {

	protected ValuesBean bean;
	protected World world;
	private static ValueManager clientInstance;
	private static ValueManager serverInstance;

	public ValueManager(World inWorld) {
		world = inWorld;
		bean = getBean();
	}

	public static ValueManager getManager(World inWorld) {

		if (inWorld.isRemote) {
			if (clientInstance != null && clientInstance.world != null && clientInstance.world.equals(inWorld)) {
				return clientInstance;
			}else{
				clientInstance = new ValueManagerClient(inWorld);
				return clientInstance;
			}
		} else {
			if (serverInstance != null && serverInstance.world != null && serverInstance.world.equals(inWorld)) {
				return serverInstance;
			}
			else{
				serverInstance = new ValueManagerServer(inWorld);
				return serverInstance;
			}
		}

	}

	public abstract ValuesBean getBean();

	public void setBean(ValuesBean inBean) {
		bean = inBean;
	}

	public Double getValue(SItem target) {
		if (!canISell(target)) {
			return (double) 0;
		} else if (!canIBuy(target)) {
			return (double) getBaseValue(target);
		} else {
			return getBean().getValueMap().get(target);
		}
	}

	public int getBaseValue(SItem target) {
		return getBean().getBaseMap().get(target);
	}

	public boolean canISell(SItem item) {
		if (getBean() != null) {
			return getBean().getBaseMap().containsKey(item);
		} else {
			return false;
		}
	}

	public boolean canIBuy(SItem item) {
		if (bean != null) {
			return getBean().getValueMap().containsKey(item);
		} else {
			return false;
		}
	}

	// TODO remove these and properly migrate to SItem
	public double getValue(Item target) {
		return getValue(SItem.getSItem(target));
	}

	public int getBaseValue(Item target) {
		return getBaseValue(SItem.getSItem(target));
	}

	public boolean canISell(Item item) {
		return canISell(SItem.getSItem(item));
	}

	public boolean canIBuy(Item item) {
		return canIBuy(SItem.getSItem(item));
	}

}
