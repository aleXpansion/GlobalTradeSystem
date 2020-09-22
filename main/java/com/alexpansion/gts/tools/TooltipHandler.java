package com.alexpansion.gts.tools;

import java.util.List;

import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.items.ItemEnderCard;
import com.alexpansion.gts.setup.RegistryHandler;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueWrapperChannel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TooltipHandler {

    @SubscribeEvent()
    public void onRenderTooltip(ItemTooltipEvent e) {

        ItemStack stack = e.getItemStack();
        List<ITextComponent> tooltip = e.getToolTip();

        if(e.getEntity() != null){
            if(stack.getItem() == RegistryHandler.ENDER_CATALOG.get()){
                tooltip.add(new StringTextComponent("Accesses your personal credit channel"));
                ValueManager vm = ValueManager.getVM(e.getEntity().world);
                ValueWrapperChannel channel = (ValueWrapperChannel)vm.getWrapper("Channel", e.getPlayer().getUniqueID().toString());
                if(channel != null){
                    int value = (int)channel.getValue();
                    tooltip.add(new StringTextComponent("Value Stored: "+value));
                }
            }else if(stack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) stack.getItem();
                int value = item.getValue(stack,e.getEntity().world);
                if(item == RegistryHandler.ENDER_CATALOG.get()){
                    tooltip.add(new StringTextComponent("Accesses your personal credit channel"));
                    ValueManager vm = ValueManager.getVM(e.getEntity().world);
                    ValueWrapperChannel channel = (ValueWrapperChannel)vm.getWrapper("Channel", e.getPlayer().getUniqueID().toString());
                    if(channel == null){
                        value = 0;
                    }else{
                        value = (int)channel.getValue();
                    }
                }
                tooltip.add(new StringTextComponent("Value Stored: "+value));
                if(item instanceof ItemEnderCard){
                    CompoundNBT tag = stack.getTag();
                    if(tag.contains("Username")){
                        String username = tag.getString("Username");
                        tooltip.add(new StringTextComponent(username+"'s channel"));
                    }else if(tag.contains("id")){
                        String id = tag.getString("id");
                        tooltip.add(new StringTextComponent("Id: "+id));
                    }else{
                        tooltip.add(new StringTextComponent("Unbound! Right click to bind to your channel"));
                    }
                }
            }else{
                ValueManager vm = ValueManager.getVM(e.getEntity().world);
                if(vm.canISell(stack.getItem())){
                    double value = vm.getValue(stack);
                    int baseValue = vm.getBaseValue(stack.getItem());
                    int amtSold = vm.getAmtSold(stack.getItem());
                    value = (value*100);
                    value = Math.floor(value) /100;
    
                    tooltip.add(new StringTextComponent("Value: "+ value));
                    tooltip.add(new StringTextComponent("Base Value: "+ baseValue));
                    tooltip.add(new StringTextComponent("Amt Sold: "+ amtSold));
                }
            }
        }
    }
    
}