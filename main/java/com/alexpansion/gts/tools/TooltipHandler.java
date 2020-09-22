package com.alexpansion.gts.tools;

import java.util.List;

import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.items.ItemEnderCard;
import com.alexpansion.gts.value.ValueManager;

import net.minecraft.item.ItemStack;
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
            if(stack.getItem() instanceof IValueContainer){
                IValueContainer item = (IValueContainer) stack.getItem();
                int value = item.getValue(stack,e.getEntity().world);
                tooltip.add(new StringTextComponent("Value Stored: "+value));
                if(stack.getItem() instanceof ItemEnderCard){
                    String id = ((ItemEnderCard)stack.getItem()).getId(stack);
                    tooltip.add(new StringTextComponent("Id: "+id));
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