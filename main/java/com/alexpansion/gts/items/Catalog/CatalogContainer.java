package com.alexpansion.gts.items.Catalog;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.blocks.ContainerGTS;
import com.alexpansion.gts.items.IValueContainer;
import com.alexpansion.gts.items.ItemEnderCard;
import com.alexpansion.gts.items.ValueStack;
import com.alexpansion.gts.setup.RegistryHandler;
import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerServer;
import com.alexpansion.gts.value.wrappers.ValueWrapper;
import com.alexpansion.gts.value.wrappers.ValueWrapperChannel;
import com.alexpansion.gts.network.CatalogRefreshPacket;
import com.alexpansion.gts.network.Networking;
import com.alexpansion.gts.network.RefreshRequestPacket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CatalogContainer extends ContainerGTS {

    public final NonNullList<ItemStack> itemList = NonNullList.create();
    
    private Inventory TMP_INVENTORY = new Inventory(38);
    public ValueStack valueStack;
    public double change;
    private ValueManager vm;
    private ItemStack stack;
    private World world;
    private boolean handFull = false;
    private PlayerEntity player;
    
    private boolean ender = false;
    private ValueWrapperChannel channel;
    
    public CatalogContainer(int windowId, PlayerEntity clientPlayer, PacketBuffer data) {
        this(windowId,clientPlayer,clientPlayer.getHeldItemMainhand());
        ItemStack stack = clientPlayer.getHeldItemMainhand();
        if(stack.getItem() == RegistryHandler.ENDER_CATALOG.get()){
            ender = true;
            ValueManager vm = ValueManager.getVM(clientPlayer.world);
            channel = (ValueWrapperChannel)vm.getWrapper("Channel", clientPlayer.getUniqueID().toString());
        }

    }

    private ValueWrapperChannel getChannel(){
        if(channel == null){
            ValueManager vm = ValueManager.getVM(world);
            channel = (ValueWrapperChannel)vm.getWrapper("Channel",player.getUniqueID().toString());
            if(channel == null && !world.isRemote){
                channel = ValueWrapperChannel.get(player.getUniqueID().toString(),world.isRemote);
                ((ValueManagerServer)vm).addWrapper(channel, player.getUniqueID().toString(), "Channel");
            }
        }
        return channel;
    }
    
    private void addValue(double inValue){
        int value = getValue();
        change += inValue;
        if(change > 1){
            value += (int)change;
            change = change%1;
        }
        setValue(value);
    }

    public int getValue(){
        if(ender){
            ValueWrapperChannel channel = getChannel();
            if(channel == null){
                return 0;
            }else{
                return (int)channel.getValue();
            }
        }else{
            return valueStack.getValue();
        }
    }

    private int getLimit(){
        if(ender){
            return getChannel().getLimit();
        }else{
            return valueStack.getLimit();
        }
    }

    private void setValue(int value){
        if(ender){
            channel.setValue(value);
        }else{
            valueStack.setValue(value);
        }
    }

    private void removeValue(double inValue){
        int value = getValue();
        change -= inValue;
        if(change < 0){
            value += (int)change -1;
            change = change%1 +1;
        }
        setValue(value);
    }

    @Override
    public void detectAndSendChanges() {
        scrollTo(0.0F);
        super.detectAndSendChanges();
    }

    public CatalogContainer(int windowId,PlayerEntity player, ItemStack stack) {
        super(RegistryHandler.CATALOG_CONTAINER.get(), windowId, player.world, null, player.inventory);
        this.player = player;
        world = player.world;
        player.openContainer = this;
        this.stack = stack;
        playerInventory = new InvWrapper(player.inventory);
        vm = ValueManager.getVM(world);
        if(stack.getItem() == RegistryHandler.ENDER_CATALOG.get()){
            ender = true;
            ValueManager vm = ValueManager.getVM(player.world);
            channel = (ValueWrapperChannel)vm.getWrapper("Channel", player.getUniqueID().toString());
        }else if(stack.getItem() instanceof ItemCatalog){
            valueStack = new ValueStack(stack, world);
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
        refresh();
        //detectAndSendChanges();
    }

    @Deprecated
	public boolean buyItem(ItemStack buyStack){
        ItemStack bought = buyItem(buyStack, 1);
        return !bought.isEmpty();
    }

    public ItemStack buyItem(ItemStack stack,int amt){
        double itemValue = vm.getValue(stack);
        if(amt > 1){
            double mult = 1 + (double)amt/500;
            itemValue *= mult;
        }
        int toBuy = Math.min(amt, (int)(getValue()/itemValue));
        //toBuy = Math.min(toBuy, vm.getAmtSold(stack.getItem()));
        if(toBuy <= 0){
            return ItemStack.EMPTY;
        }
        removeValue( itemValue * toBuy);
        ItemStack outStack = new ItemStack(stack.getItem(),toBuy);
        vm.addValueSold(stack.getItem(),0-toBuy, 0-(itemValue*toBuy), world);
        return outStack;
    }

    public ItemStack sellItem(ItemStack sellStack){
        return sellItem(sellStack,sellStack.getCount());
    }

    //attempts to sell the items in the given stack. Returns the remaining items, or an empty stack if all were sold.
    public ItemStack sellItem(ItemStack sellStack, int max){
        double mult = 1;
        Item item = sellStack.getItem();
        ValueWrapper wrapper = vm.getWrapper(sellStack.getItem());
        if(wrapper == null){
            return sellStack;
        }
        if(!wrapper.canSell()) return sellStack;
        double itemValue = wrapper.getValue();
        int space = getLimit() - getValue();
        if(item instanceof IValueContainer){
            IValueContainer container = (IValueContainer)item;
            int value = container.getValue(sellStack,world);
            int toRemove = value;
            if(value > space){
                toRemove = space;
            }
            addValue(toRemove);
            return container.setValue(sellStack, value-toRemove,world);
        }
        if(!vm.canISell(item)){
            return sellStack;
        }else{
            if(itemValue <= space){
                int toSell = Math.min(max, (int)(space/itemValue));
                if(toSell < 0){
                    GTS.LOGGER.error("Selling a negative number.");
                }
                addValue(itemValue * toSell * mult);
                vm.addValueSold(item,toSell, itemValue * toSell * mult, world);
                refresh();
                if(toSell < sellStack.getCount()){
                    sellStack.setCount(sellStack.getCount() - toSell);
                    return sellStack;
                }else{
                    sellStack = ItemStack.EMPTY;
                }
                return sellStack;
            }else{
                return sellStack;
            }
        }
    }

    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public void refresh(){
        if(world.isRemote){
            Networking.INSTANCE.sendToServer(new RefreshRequestPacket());
            return;
        }
        //checking if they player is holding anything, cancel if they are. 
        //This way the items don't move around while you're trying to buy them.
        handFull = player.inventory.getItemStack().getItem() != ItemStack.EMPTY.getItem();
        if(handFull){
            return;
        }
        this.itemList.clear();
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack filterStack = getSlot(1).getStack();
        ArrayList<Item> buyable = vm.getBuyableItemsTargeted(filterStack.getItem(),36,getValue());
        for(Item i : buyable){
            stacks.add(new ItemStack(i));
        }
        this.itemList.addAll(stacks);
        scrollTo(0.0f);
        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)this.player), new CatalogRefreshPacket(stacks));
    }

    public void refresh(ArrayList<ItemStack> stacks){
        this.itemList.clear();
        this.itemList.addAll(stacks);
        scrollTo(0.0f);
    }

    public void scrollTo(float pos) {
        /*ValueManager vm = ValueManager.getVM(world);
        itemList.clear();
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack filterStack = getSlot(1).getStack();
        ArrayList<Item> buyable = vm.getBuyableItemsTargeted(filterStack.getItem(),36,getValue());
        for(Item i : buyable){
            stacks.add(new ItemStack(i));
        }
        itemList.addAll(stacks);*/

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
        //-999 means it's outside the GUI, -1 is in the GUI but not over a slot
        if(slotId == -999 || slotId == -1){
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
        //This is the credit slot, grab a credit or fill a valueContainer
        if(slotId == 0){
            int value = getValue();
            if(value <= 0) return ItemStack.EMPTY;
            if(mouseStack.isEmpty()){
                int out = shift ? 64 : 1;
                if(out > value) out = value;

                removeValue(out);
                mouseStack = new ItemStack(RegistryHandler.CREDIT.get(),out);
            }else if(mouseStack.getItem() instanceof IValueContainer){
                //if this is an ender catalog and the mouse has an ender card, check if they are on the same channel.
                //if so, do nothing.
                if(ender && mouseStack.getItem() instanceof ItemEnderCard){
                    String mouseId = ((ItemEnderCard)mouseStack.getItem()).getId(mouseStack);
                    if(mouseId.equals(channel.getLabel())){
                        return mouseStack;
                    }
                }
                ValueStack mouseValue = new ValueStack(mouseStack, world);
                int out = shift ? mouseValue.getSpace():1;
                if(out > value) out = value;
                mouseStack = mouseValue.addValue(out);
                removeValue(out);
            }
            player.inventory.setItemStack(mouseStack);
            return mouseStack;
        }
        //This is the target slot. Set the target to that item
        if(slotId == 1){
            if(mouseStack.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }else if(vm.canISell(mouseStack.getItem())){
                slot.putStack(new ItemStack(mouseStack.getItem(),1));
                sellItem(mouseStack);
                player.inventory.setItemStack(ItemStack.EMPTY);
            }
            refresh();
            return ItemStack.EMPTY;
        //The rest of the slots. Buy the selected item, or sell anything else.
        }else if(slotId >=0 && slotId < 38){
            if(mouseStack.getItem() == ItemStack.EMPTY.getItem() & stack.getItem() != ItemStack.EMPTY.getItem()){
                //This is the middle click, set the target to the selected item.
                if(clickTypeIn.equals(ClickType.CLONE) && !stack.isEmpty()){
                    getSlot(1).putStack(new ItemStack(stack.getItem(),1));
                    refresh();;
                    return mouseStack;
                }
                int amt = shift ? stack.getMaxStackSize():1;
                mouseStack = buyItem(stack, amt);
                player.inventory.setItemStack(mouseStack);
                return mouseStack;
            }else if(mouseStack.getItem() == stack.getItem() && !stack.isEmpty()){
                if(mouseStack.getCount() < mouseStack.getMaxStackSize()){
                    int oldCount = mouseStack.getCount();
                    int goal = shift ? mouseStack.getMaxStackSize():oldCount+1;
                    ItemStack newStack = buyItem(stack, goal-oldCount);
                    mouseStack.setCount(newStack.getCount() + oldCount);
                    player.inventory.setItemStack(mouseStack);
                }
                return mouseStack;
            }else if(mouseStack.getItem() instanceof IValueContainer){
                if(ender && mouseStack.getItem() instanceof ItemEnderCard){
                    String mouseId = ((ItemEnderCard)mouseStack.getItem()).getId(mouseStack);
                    if(mouseId.equals(channel.getLabel())){
                        return mouseStack;
                    }
                }
                ValueStack mouseValue = new ValueStack(mouseStack, world);
                int space = getLimit() - getValue();
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
                refresh();
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
        if(ender && stack.getItem() instanceof ItemEnderCard){
            String mouseId = ((ItemEnderCard)stack.getItem()).getId(stack);
            if(mouseId.equals(channel.getLabel())){
                return ItemStack.EMPTY;
            }
        }
        if(stack.getItem() == RegistryHandler.CATALOG.get()){
            return ItemStack.EMPTY;
        }
        if(index >= 38 && !stack.isEmpty() && vm.canISell(stack.getItem())){
            putStackInSlot(index, sellItem(stack));
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