package com.alexpansion.gts.items.Catalog;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.blocks.ContainerGTS;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.items.ValueStack;
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
    public ValueStack valueStack;
    public double change;
    private ValueManager vm;
    private ItemStack stack;
    private World world;

    
    public CatalogContainer(int windowId, PlayerEntity clientPlayer, PacketBuffer data) {
        this(windowId,clientPlayer,clientPlayer.getHeldItemMainhand());
    }
    
    private void addValue(double inValue){
        int value = valueStack.getValue();
        change += inValue;
        if(change > 1){
            value += (int)change;
            change = change%1;
        }
        valueStack.setValue(value);
    }

    private void removeValue(double inValue){
        int value = valueStack.getValue();
        change -= inValue;
        if(change < 0){
            value -= (int)change -1;
            change = change%1 +1;
        }
        valueStack.setValue(value);
    }

    public CatalogContainer(int windowId,PlayerEntity player, ItemStack stack) {
        super(RegistryHandler.CATALOG_CONTAINER.get(), windowId, player.world, null, player.inventory);
        world = player.world;
        player.openContainer = this;
        this.stack = stack;
        playerInventory = new InvWrapper(player.inventory);
        vm = ValueManager.getVM(world);
        if(stack.getItem() instanceof ItemCatalog){
            valueStack = new ValueStack(stack);
        }else{
            GTS.LOGGER.error("Catalog container found "+stack.getDisplayName()+" instead of Catalog.");
            valueStack = null;
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

    @Deprecated
	public boolean buyItem(ItemStack buyStack){
        ItemStack bought = buyItem(buyStack, 1);
        return !bought.isEmpty();
    }

    public ItemStack buyItem(ItemStack stack,int amt){
        double itemValue = vm.getValue(stack);
        int toBuy = Math.min(amt, (int)(valueStack.getValue()/itemValue));
        if(toBuy <= 0){
            return ItemStack.EMPTY;
        }
        removeValue( itemValue * toBuy);
        stack.setCount(toBuy);
        vm.addValueSold(stack.getItem(), 0-(itemValue*toBuy), world);
        return stack;
    }

    public ItemStack sellItem(ItemStack sellStack){
        return sellItem(sellStack,sellStack.getCount());
    }

    //attempts to sell the items in the given stack. Returns the remaining items, or an empty stack if all were sold.
    public ItemStack sellItem(ItemStack sellStack, int max){
        double mult = 0.8;
        Item item = sellStack.getItem();
        double itemValue = vm.getValue(sellStack);
        if(!vm.canISell(item)){
            return sellStack;
        }else{
            int space = ((IValueContainer)stack.getItem()).getLimit() - valueStack.getValue();
            if(itemValue <= space){
                int toSell = Math.min(max, (int)(space/itemValue));
                addValue(itemValue * toSell * mult);
                vm.addValueSold(item, itemValue * toSell * mult, world);
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
        ArrayList<Item> buyable = vm.getBuyableItemsTargeted(filterStack.getItem(),36,valueStack.getValue());
        for(Item i : buyable){
            stacks.add(new ItemStack(i));
        }
        itemList.addAll(stacks);

        int i = (this.itemList.size() + 9 - 1) / 9 - 4;
        int j = (int) ((double) (pos * (float) i) + 0.5D);
        if (j < 0) {
            j = 0;
        }
        TMP_INVENTORY.setInventorySlotContents(0, new ItemStack(RegistryHandler.CREDIT.get()));
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
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        //If the spot clicked is not over a slot, this is what is gives as the Id. 
        if(slotId == -999){
            return super.slotClick(slotId,dragType,clickTypeIn,player);
        }
        boolean shift = clickTypeIn == ClickType.QUICK_MOVE;
        Slot slot;
        try{
            slot = getSlot(slotId);
        }catch(Exception e){
            GTS.LOGGER.error("error in catalogContainer.slotClick for slot " + slotId + "." );
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        ItemStack stack = slot.getStack();
        ItemStack mouseStack = player.inventory.getItemStack();
        //if you've clicked on the slot with this catalog in it, do nothing.
        if(this.stack == stack){
            return mouseStack;
        }
        if(slotId == 0){
            int value = valueStack.getValue();
            if(value <= 0) return ItemStack.EMPTY;
            if(mouseStack.isEmpty()){
                int out = shift ? 64 : 1;
                if(out > value) out = value;

                removeValue(out);
                mouseStack = new ItemStack(RegistryHandler.CREDIT.get(),out);
            }else if(mouseStack.getItem() instanceof IValueContainer){
                ValueStack mouseValue = new ValueStack(mouseStack);
                int out = shift ? mouseValue.getSpace():1;
                if(out > value) out = value;
                mouseStack = mouseValue.addValue(out);
                removeValue(out);
            }
            player.inventory.setItemStack(mouseStack);
            return mouseStack;
        }
        if(slotId == 1){
            if(mouseStack.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }else if(vm.canISell(mouseStack.getItem())){
                slot.putStack(new ItemStack(mouseStack.getItem(),1));
                sellItem(mouseStack);
                player.inventory.setItemStack(ItemStack.EMPTY);
            }
            scrollTo(0.0f);
            return ItemStack.EMPTY;
        }else if(slotId >=0 && slotId < 38){
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
            }else if(mouseStack.getItem() instanceof IValueContainer){
                ValueStack mouseValue = new ValueStack(mouseStack);
                int space = ((IValueContainer)this.stack.getItem()).getLimit() - valueStack.getValue();
                int in = Math.min(mouseValue.getValue(),space);
                mouseStack = mouseValue.removeValue(in);
                addValue(in);
                player.inventory.setItemStack(mouseStack);
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