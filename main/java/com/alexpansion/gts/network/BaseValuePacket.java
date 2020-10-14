package com.alexpansion.gts.network;

import java.util.function.Supplier;

import com.alexpansion.gts.Config;
import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerServer;
import com.alexpansion.gts.value.wrappers.ValueWrapper;
import com.alexpansion.gts.value.wrappers.ValueWrapperItem;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class BaseValuePacket {
    
    private int value;
    private String key;

    public BaseValuePacket(ValueWrapper wrapper,int value){
        this.key = wrapper.toString();
        this.value = value;
    }

    public BaseValuePacket(PacketBuffer buf){
        key = buf.readString(30000);
        value = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(key);
        buf.writeInt(value);
    }

    public void handle(Supplier<Context> ctx){
        Context get = ctx.get();
        if(ctx.get().getSender().hasPermissionLevel(2) || Config.NON_OPS_CAN_SEND_VALUES.get()){
            get.enqueueWork(() ->{
                ServerWorld world = get.getSender().getServerWorld();
                ValueManagerServer vm = ValueManager.getVM(world);
                ValueWrapper wrapper= vm.getWrapper(key);
                if(wrapper == null){
                    wrapper = ValueWrapper.get(key,false);
                    vm.addWrapper(wrapper,((ValueWrapperItem)wrapper).getItem().getRegistryName().toString(), "Item");
                }
                wrapper.setBaseValue(value);
            });
        }
    }
}