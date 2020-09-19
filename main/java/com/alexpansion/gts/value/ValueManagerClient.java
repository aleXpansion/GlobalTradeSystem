package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.Calendar;

import com.alexpansion.gts.network.BaseValuePacket;
import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.network.ValuesRequestPacket;
import com.alexpansion.gts.tools.JEIloader;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

public class ValueManagerClient extends ValueManager {
	private ValuesBean bean;

	private Calendar lastUpdate = Calendar.getInstance();
	private ArrayList<ValueWrapper> nonBuyable = new ArrayList<ValueWrapper>();

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
		if(getBean() == null || getBean().getWrappers("Item") == null){
			return false;
		}
		if(getBean().getWrappers("Item").containsKey(item.getRegistryName().toString())){
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

	private int getCrafingValue(Item target){
		ValueWrapperItem wrapper;
		if(getBean().getWrappers("Item").containsKey(target.getRegistryName().toString())){
			wrapper = (ValueWrapperItem)getBean().getWrappers("Item").get(target.getRegistryName().toString());
		}else{
			wrapper = ValueWrapperItem.get(target,true);
		}
		return getCrafingValue(wrapper);
	}

	private int getCrafingValue(ValueWrapper wrapper){
		if(ModList.get().isLoaded("jei") && JEIloader.isLoaded() ){
			int value = JEIloader.getCrafingValue(wrapper);
			if(value <= 0){
				nonBuyable.add(wrapper);
				return 0;
			}else{
				sendBaseValue(wrapper, value);
				return value;
			}
		}
		return 0;
	}

	private void sendBaseValue(ValueWrapper wrapper,int value){
		Networking.INSTANCE.sendToServer(new BaseValuePacket(wrapper, value));
	}

	public boolean isRemote(){
		return true;
	}

}
