package com.alexpansion.gts.commands;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerServer;
import com.alexpansion.gts.value.wrappers.ValueWrapperItem;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.item.Item;
import net.minecraft.world.server.ServerWorld;

public class CommandSetSoldAmt implements Command<CommandSource> {

    private static final CommandSetSoldAmt CMD = new CommandSetSoldAmt();
    private ValueManagerServer vm;

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("amt").requires(cs -> cs.hasPermissionLevel(2))
            .then(Commands.argument("value", IntegerArgumentType.integer())
                .then(Commands.argument("item", ItemArgument.item())
                    .executes((CommandSource)->{
                        ServerWorld world = CommandSource.getSource().getWorld();
                        return CMD.setAmtSold(world,ItemArgument.getItem(CommandSource, "item").getItem()
                            ,IntegerArgumentType.getInteger(CommandSource, "value"));
                    }))
                .executes((CommandSource)->{
                    ServerWorld world = CommandSource.getSource().getWorld();
                    try{
                        Item item = CommandSource.getSource().asPlayer().getHeldItemMainhand().getItem();
                        return CMD.setAmtSold(world,item,IntegerArgumentType.getInteger(CommandSource, "value"));
                    }catch(CommandSyntaxException e){
                        GTS.LOGGER.error("server attempted to assign value to the item in their hand.");
                        return 0;
                    }
                }));
            
    }

    private int setAmtSold(ServerWorld world,Item key, int amt){
        if(vm == null){
            vm = (ValueManagerServer)ValueManager.getVM(world);
        }
        GTS.LOGGER.info("set sold amt to "+amt+" for "+key.toString());
        ValueWrapperItem wrapper = vm.getWrapper(key);
        if(wrapper == null){
            wrapper = ValueWrapperItem.get(key,false);
            vm.addWrapper(wrapper,key.getRegistryName().toString(), "Item");
        }else{
            wrapper.addSold(0, 0 - wrapper.getSoldAmt() + amt);
        }
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        GTS.LOGGER.error("CommandAddBaseValue#run ran");
        return 0;
    }
}