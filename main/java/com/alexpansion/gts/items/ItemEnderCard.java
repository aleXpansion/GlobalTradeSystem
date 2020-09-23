package com.alexpansion.gts.items;

import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerServer;
import com.alexpansion.gts.value.ValueWrapperChannel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemEnderCard extends ItemBase implements IValueContainer {

	private static String ID_KEY = "id";

	public ItemEnderCard() {
		super(new Item.Properties().maxStackSize(1).maxDamage(1000));
	}

	public ItemStack setChannel(ItemStack stack,String id){
		CompoundNBT tag = stack.getTag();
		tag.putString(ID_KEY, id);
		return stack;
	}

	public ItemStack setChannel(ItemStack stack,PlayerEntity player){
		CompoundNBT tag = stack.getTag();
		tag.putString(ID_KEY, player.getUniqueID().toString());
		tag.putString("Username", player.getName().getString());
		return stack;
	}

	private ValueWrapperChannel getChannel(ItemStack stack,World world){
		ValueManager vm = ValueManager.getVM(world);
		String id = stack.getTag().getString(ID_KEY);
		if(id.equals("")){
			return null;
		}
		ValueWrapperChannel channel = (ValueWrapperChannel)vm.getWrapper("Channel", id);
		if(channel == null && !world.isRemote){
			channel = ValueWrapperChannel.get(id, false);
			((ValueManagerServer)vm).addWrapper(channel, id, "Channel");
		}
		return channel;
	}

	public String getId(ItemStack stack){
		return stack.getTag().getString(ID_KEY);
	}

	@Override
	public ItemStack setValue(ItemStack stack, int value, World world) {
		ValueWrapperChannel channel = getChannel(stack,world);
		if(channel != null){
			channel.setValue(value);
			updateDisplay(stack,world);
		}
		return stack;
	}
	
	public int getValue(ItemStack stack,World world){
		ValueWrapperChannel channel = getChannel(stack, world);
		if(channel == null)return 0;
		return (int)channel.getValue();		
	}
	
	private void updateDisplay(ItemStack stack,World world){
		stack.setDamage(stack.getMaxDamage()-((int) ((getValue(stack,world)/(double)getLimit(stack, world)) * stack.getMaxDamage())));
	}

	@Override
	public int getLimit(ItemStack stack,World world) {
		return getChannel(stack, world).getLimit();
	}

	@Override
	public int getSpace(ItemStack stack, World world) {
		ValueWrapperChannel channel = getChannel(stack,world);
		if(channel == null)return 0;
		return getLimit(stack, world) - getValue(stack,world);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		setChannel(playerIn.getHeldItemMainhand(), playerIn);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
}
