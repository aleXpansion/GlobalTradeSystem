package com.alexpansion.gts.value;

import java.util.Calendar;
import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.network.ValuesRequestPacket;

import net.minecraft.world.World;

public class ValueManagerClient extends ValueManager {

	private Calendar lastUpdate = Calendar.getInstance();

	public ValueManagerClient(World world) {
		super(world);
	}

	public ValuesBean getBean() {
		long timeSinceUpdate;
		if (lastUpdate != null) {
			timeSinceUpdate = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
		}else{
			timeSinceUpdate = Long.MAX_VALUE;
		}
		if (timeSinceUpdate > 1000) {
			//LogHelper.info("Requesting ValuesBean");
			Networking.INSTANCE.sendToServer(new ValuesRequestPacket());
			lastUpdate = Calendar.getInstance();
		}
		if(bean == null){
			bean = new ValuesBean();
		}
		return bean;

	}

}
