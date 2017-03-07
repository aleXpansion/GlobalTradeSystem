package com.alexpansion.gts.utility;

import java.util.Calendar;
import java.util.Date;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.network.ValuesRequestPacket;

import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ValueManager {

	private ValuesBean bean;
	private boolean isClient;
	private static ValueManager clientInstance;
	private static World clientWorld;
	private static ValueManager serverInstance;
	private static World serverWorld;
	private Calendar lastUpdate = Calendar.getInstance();

	public ValueManager(World inWorld) {
		isClient = inWorld.isRemote;
		if (isClient) {
			clientWorld = inWorld;
			clientInstance = this;
		} else {
			serverWorld = inWorld;
			serverInstance = this;
		}
		bean = getBean();
	}

	public static ValueManager getManager(World inWorld) {

		if (inWorld.isRemote) {
			if (clientWorld != null && clientInstance != null && clientWorld.equals(inWorld)) {
				return clientInstance;
			}
		} else {
			if (serverWorld != null && serverInstance != null && serverWorld.equals(inWorld)) {
				return serverInstance;
			}
		}
		return new ValueManager(inWorld);

	}

	public ValuesBean getBean() {
		if (isClient) {
			long timeSinceUpdate = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
			if (timeSinceUpdate > 1000) {
				GlobalTradeSystem.network.sendToServer(new ValuesRequestPacket());
				lastUpdate = Calendar.getInstance();
			}
			return bean;
		} else {
			return ValueManagerServer.getBean(serverWorld);
		}
	}

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
