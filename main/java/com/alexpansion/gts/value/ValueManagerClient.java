package com.alexpansion.gts.value;

import java.util.Calendar;
import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.network.ValuesRequestPacket;

import net.minecraft.world.World;

public class ValueManagerClient extends ValueManager {
	private ValuesBean bean;

	private Calendar lastUpdate = Calendar.getInstance();

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

}
