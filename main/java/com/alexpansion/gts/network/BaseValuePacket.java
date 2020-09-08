package com.alexpansion.gts.network;

import java.util.function.Supplier;

import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerServer;
import com.alexpansion.gts.value.ValueWrapperItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class BaseValuePacket {
    
    private int value;
    private Item key;

    public BaseValuePacket(Item key,int value){
        this.key = key;
        this.value = value;
    }

    public BaseValuePacket(PacketBuffer buf){
        key = buf.readItemStack().getItem();
        value = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        ItemStack stack = new ItemStack(key);
        buf.writeItemStack(stack);
        buf.writeInt(value);
    }

    public void handle(Supplier<Context> ctx){
        Context get = ctx.get();
        get.enqueueWork(() ->{
            ServerWorld world = get.getSender().getServerWorld();
            ValueManagerServer vm = ValueManager.getVM(world);
            ValueWrapperItem wrapper= vm.getWrapper(key);
            if(wrapper == null){
                wrapper = new ValueWrapperItem(key);
                vm.addWrapper(wrapper, "item");
            }
            wrapper.setBaseValue(value);
        });
    }
}