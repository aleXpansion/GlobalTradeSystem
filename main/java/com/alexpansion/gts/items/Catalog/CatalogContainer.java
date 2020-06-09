package com.alexpansion.gts.items.Catalog;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.blocks.ContainerGTS;
import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.util.RegistryHandler;
import com.alexpansion.gts.value.ValueManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CatalogContainer extends ContainerGTS {

    public final NonNullList<ItemStack> itemList = NonNullList.create();
    
    static final Inventory TMP_INVENTORY = new Inventory(38);
    public int value;
    public double change;
    private ValueManager vm;
    private ItemStack stack;
    private World world;

    
    public CatalogContainer(int windowId, PlayerEntity clientPlayer, PacketBuffer data) {
        this(windowId,clientPlayer,clientPlayer.getHeldItemMainhand());
	}

    public CatalogContainer(int windowId,PlayerEntity player, ItemStack stack) {
        super(RegistryHandler.CATALOG_CONTAINER.get(), windowId, player.world, null, player.inventory);
        world = player.world;
        player.openContainer = this;
        this.stack = stack;
        playerInventory = new InvWrapper(player.inventory);
        vm = ValueManager.getVM(world);
        if(stack.getItem() instanceof ItemCatalog){
            ItemCatalog item = (ItemCatalog) stack.getItem();
            value = item.getValue(stack);
        }else{
            GTS.LOGGER.error("Catalog container found "+stack.getDisplayName()+" instead of Catalog.");
            value = 0;
        }
        change = 0;

        this.addSlot(new Slot(TMP_INVENTORY, 0, 8, 18));
        this.addSlot(new Slot(TMP_INVENTORY, 1, 152, 18));

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new CatalogScreen.LockedSlot(TMP_INVENTORY, i * 9 + j + 2, 8 + j * 18,
                        54 + i * 18));
            }
        }
        layoutPlayerInventorySlots(8, 140);

        this.scrollTo(0.0F);
    }


	public boolean buyItem(ItemStack buyStack){
        ItemStack bought = buyItem(buyStack, 1);
        return !bought.isEmpty();
    }

    public ItemStack buyItem(ItemStack stack,int amt){
        double itemValue = vm.getValue(stack);
        int toBuy = Math.min(amt, (int)(value/itemValue));
        if(toBuy <= 0){
            return ItemStack.EMPTY;
        }
        change -= itemValue * toBuy;
        while(change < 0){
            change++;
            value--;
        }
        stack.setCount(toBuy);
        vm.addValueSold(stack.getItem(), 0-(itemValue*toBuy), world);
        return stack;
    }

    public ItemStack sellItem(ItemStack sellStack){
        return sellItem(sellStack,sellStack.getCount());
    }

    //attempts to sell the items in the given stack. Returns the remaining items, or an empty stack if all were sold.
    public ItemStack sellItem(ItemStack sellStack, int max){
        Item item = sellStack.getItem();
        double itemValue = vm.getValue(sellStack);
        if(!vm.canISell(item)){
            return sellStack;
        }else{
            int space = ((IValueContainer)stack.getItem()).getLimit() - value;
            if(itemValue <= space){
                int toSell = Math.min(max, (int)(space/itemValue));
                change += itemValue * toSell;
                vm.addValueSold(item, itemValue * toSell, world);
                if(change > 1 ){
                    value += (int)change;
                    change = change %1;
                }
                if(toSell < sellStack.getCount()){
                    sellStack.setCount(sellStack.getCount() - toSell);
                    return sellStack;
                }else{
                    return ItemStack.EMPTY;
                }

            }else{
                return sellStack;
            }
        }
    }

    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public void scrollTo(float pos) {
        ValueManager vm = ValueManager.getVM(world);
        itemList.clear();
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack filterStack = getSlot(1).getStack();
        ArrayList<Item> buyable = vm.getBuyableItemsTargeted(filterStack.getItem(),36);
        for(Item i : buyable){
            stacks.add(new ItemStack(i));
        }
        itemList.addAll(stacks);

        int i = (this.itemList.size() + 9 - 1) / 9 - 4;
        int j = (int) ((double) (pos * (float) i) + 0.5D);
        if (j < 0) {
            j = 0;
        }

        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;
                if (i1 >= 0 && i1 < this.itemList.size()) {
                    TMP_INVENTORY.setInventorySlotContents(l + k * 9+2, this.itemList.get(i1));
                } else {
                    TMP_INVENTORY.setInventorySlotContents(l + k * 9+2, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity player){
        if(stack.getItem() instanceof ItemCatalog){
            ItemCatalog item = (ItemCatalog) stack.getItem();
            int itemValue = item.getValue(stack);
            int dif = value - itemValue;
            try{
                if(value <= 0){
                    item.removeValue(stack, item.getValue(stack));
                }else if (value <= item.getLimit()){
                    item.addValue(stack, dif);
                }else{
                    item.addValue(stack, item.getLimit()-itemValue);
                }
            }catch(ValueOverflowException e){
                GTS.LOGGER.error("CatalogContainer attempted to put "+value + " in a Catalog with max "+item.getLimit());
            }
        }else{
            GTS.LOGGER.error("Catalog container found "+stack.getDisplayName()+" instead of Catalog.");
            value = 0;
        }

    }

    
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        GTS.LOGGER.info("dragType: "+dragType+" clicktype: "+ clickTypeIn.toString());
        if(slotId == 1){
            Slot slot = getSlot(slotId);
            ItemStack mouseStack = player.inventory.getItemStack();
            if(mouseStack.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }else if(vm.canIBuy(mouseStack.getItem())){
                slot.putStack(new ItemStack(mouseStack.getItem(),1));
                sellItem(mouseStack);
                player.inventory.setItemStack(ItemStack.EMPTY);
            }
            scrollTo(0.0f);
            return ItemStack.EMPTY;
        }else if(slotId >=0 && slotId < 38){
            boolean shift = clickTypeIn == ClickType.QUICK_MOVE;
            Slot slot = getSlot(slotId);
            ItemStack stack = slot.getStack();
            ItemStack mouseStack = player.inventory.getItemStack();
            if(mouseStack.getItem() == ItemStack.EMPTY.getItem()){
                if(clickTypeIn.equals(ClickType.CLONE) && !stack.isEmpty()){
                    getSlot(1).putStack(new ItemStack(stack.getItem(),1));
                    return mouseStack;
                }
                int amt = shift ? stack.getMaxStackSize():1;
                stack = buyItem(stack, amt);
                player.inventory.setItemStack(stack);
                scrollTo(0.0f);
                return stack;
            }else if(mouseStack.getItem() == stack.getItem()){
                if(mouseStack.getCount() < mouseStack.getMaxStackSize()){
                    int oldCount = mouseStack.getCount();
                    int goal = shift ? mouseStack.getMaxStackSize():oldCount+1;
                    ItemStack newStack = buyItem(stack, goal-oldCount);
                    mouseStack.setCount(newStack.getCount() + oldCount);
                    player.inventory.setItemStack(mouseStack);
                }
                return mouseStack;
            }else{
                ItemStack newStack;
                //dragType 1 means the right mouse button was used.
                if(dragType == 1){
                    newStack = sellItem(mouseStack,1);
                }else{
                    newStack = sellItem(mouseStack);
                }
                player.inventory.setItemStack(newStack);
                return newStack;
            }
        }else {
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
    }

    public boolean canScroll() {
        return this.itemList.size() > 36;
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack stack = getSlot(index).getStack();
        if(index >= 38 && !stack.isEmpty() && vm.canISell(stack.getItem())){
            putStackInSlot(index, sellItem(stack));
        }else if(index > 1 && index < 38){
            ItemStack outStack = buyItem(stack, 64);
            this.mergeItemStack(outStack, 38, 74,false);
        }
        return ItemStack.EMPTY;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != TMP_INVENTORY;
    }

    public boolean canDragIntoSlot(Slot slotIn) {
        return slotIn.inventory != TMP_INVENTORY;
    }



    public static class ContainerProvider implements INamedContainerProvider {

        ItemStack stack;
    
        public ContainerProvider(ItemStack stack){
            this.stack = stack;
        }
    
        
        @Override
        public CatalogContainer createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
            return new CatalogContainer(i,player,stack);
        }
    
        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent(RegistryHandler.CATALOG_CONTAINER.get().getRegistryName().getPath());
        }
        
    }

   
}