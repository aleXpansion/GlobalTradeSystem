package com.alexpansion.gts.utility;

import java.util.Calendar;
import com.alexpansion.gts.GlobalTradeSystem;
import com.alexpansion.gts.network.ValuesRequestPacket;

import net.minecraft.world.World;

public class ValueManagerClient extends ValueManager {

	private Calendar lastUpdate = Calendar.getInstance();

	public ValueManagerClient(World inWorld) {
		super(inWorld);
	}

	public ValuesBean getBean() {
		long timeSinceUpdate;
		if (lastUpdate != null) {
			timeSinceUpdate = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
		}else{
			timeSinceUpdate = Long.MAX_VALUE;
		}
		// TODO add config for refresh time
		if (timeSinceUpdate > 1000) {
			//LogHelper.info("Requesting ValuesBean");
			GlobalTradeSystem.network.sendToServer(new ValuesRequestPacket());
			lastUpdate = Calendar.getInstance();
		}
		return bean;

	}

}
