package com.alexpansion.gts.utility;

import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.network.ValuesRequestPacket;

import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ValueManager {

	private ValuesBean bean;
	private boolean isClient;
	private static ValueManager instance;
	private static World world;
	
	public ValueManager(World inWorld) {
		isClient = inWorld.isRemote;
		world = inWorld;
		bean = getBean();
		instance = this;
	}
	
	public static ValueManager getManager(World inWorld){
		if(world != null && instance != null && world.equals(inWorld)){
			return instance;
		}else{
			return new ValueManager(inWorld);
		}
			
	}

	public ValuesBean getBean() {
		if (isClient) {
			GlobalTradeSystem.network.sendToServer(new ValuesRequestPacket());
			return null;
		} else {
			return ValueManagerServer.getBean(world);
		}
	}
	
	public void setBean(ValuesBean inBean){
		bean = inBean;
	}

	public Double getValue(SItem target) {
		if (!canISell(target)) {
			return (double) 0;
		} else if (!canIBuy(target)) {
			return (double) getBaseValue(target);
		} else {
			return bean.getValueMap().get(target);
		}
	}

	public int getBaseValue(SItem target) {
		return bean.getBaseMap().get(target);
	}
	

	public boolean canISell(SItem item) {
		return bean.getBaseMap().containsKey(item);
	}

	public boolean canIBuy(SItem item) {
		return bean.getValueMap().containsKey(item);
	}
	
	//TODO remove these and properly migrate to SItem
	public double getValue(Item target){
		return getValue(new SItem(target));
	}
	
	public int getBaseValue(Item target){
		return getBaseValue(new SItem(target));
	}
	
	public boolean canISell(Item item){
		return canISell(new SItem(item));
	}
	
	public boolean canIBuy(Item item){
		return canIBuy(new SItem(item));
	}
	

}
