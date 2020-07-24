package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.Calendar;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.network.BaseValuePacket;
import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.network.ValuesRequestPacket;
import com.alexpansion.gts.tools.JEIloader;
import com.alexpansion.gts.setup.RegistryHandler;

import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ValueManagerClient extends ValueManager {
	private ValuesBean bean;

	private Calendar lastUpdate = Calendar.getInstance();
	private ArrayList<Item> nonBuyable = new ArrayList<Item>();

	public ValueManagerClient(World world) {
		super(world);
		getBean();
	}

	public void setBean(ValuesBean inBean) {
		bean = inBean;
	}

	public ValuesBean getBean() {
		long timeSinceUpdate;
		if (lastUpdate != null) {
			timeSinceUpdate = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
		}else{
			timeSinceUpdate = Long.MAX_VALUE;
		}
		if (timeSinceUpdate > 1000) {
			Networking.INSTANCE.sendToServer(new ValuesRequestPacket());
			lastUpdate = Calendar.getInstance();
		}
		if(bean == null){
			bean = new ValuesBean();
		}
		return bean;

	}

	public boolean canISell(Item item) {
		//if we haven't loaded values yet, return false for now
		if(getBean() == null){
			return false;
		}
		if(getBean().getBaseMap().containsKey(item)){
			return true;
		}else{
			//If JEI is installed and loaded, use it to calculate the value
			return getCrafingValue(item) != 0;
		}
	}

	@Override
	public int getBaseValue(Item target) {
		int value = super.getBaseValue(target);
		if(value == 0){
			value = getCrafingValue(target);
		}
		return value;
	}

	private int getCrafingValue(Item item){
		if(item == RegistryHandler.CREDIT.get()){
			sendBaseValue(item, 1);
			return 1;
		}
		if(JEIloader.isLoaded() ){
			int value = JEIloader.getCrafingValue(this,item);
			if(value <= 0){
				nonBuyable.add(item);
				return 0;
			}else{
				sendBaseValue(item, value);
				return value;
			}
		}
		return 0;
	}

	private void sendBaseValue(Item item,int value){
		Networking.INSTANCE.sendToServer(new BaseValuePacket(item, value));
	}

}
