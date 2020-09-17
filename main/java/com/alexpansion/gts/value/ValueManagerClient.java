package com.alexpansion.gts.value;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<Item,ValueWrapperItem> newMap = new HashMap<Item,ValueWrapperItem>();
		List<ValueWrapper> wrappers = inBean.getWrappers("item");
		for(ValueWrapper wrapper : wrappers){
			if(wrapper instanceof ValueWrapperItem){
				newMap.put(((ValueWrapperItem) wrapper).getItem(), (ValueWrapperItem)wrapper);
			}
		}
		itemMap = newMap;
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
		if(itemMap.containsKey(item)){
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
		if(itemMap.containsKey(target)){
			wrapper = itemMap.get(target);
		}else{
			wrapper = ValueWrapperItem.get(target);
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

}
