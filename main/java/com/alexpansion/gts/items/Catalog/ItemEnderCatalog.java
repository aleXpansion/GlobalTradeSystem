package com.alexpansion.gts.items.Catalog;

import com.alexpansion.gts.items.ItemBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemEnderCatalog extends ItemBase{

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(hand == Hand.MAIN_HAND){
            if(!world.isRemote){
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                //the funky function is player.getPosition()
                NetworkHooks.openGui(serverPlayer, new CatalogContainer.ContainerProvider(stack),player.func_233580_cy_());
            }
            return ActionResult.resultSuccess(stack);
        }else{
            return ActionResult.resultPass(stack);
        }
    }

}