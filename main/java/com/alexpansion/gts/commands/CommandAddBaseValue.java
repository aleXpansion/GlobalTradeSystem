package com.alexpansion.gts.commands;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerServer;
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

public class CommandAddBaseValue implements Command<CommandSource> {

    private static final CommandAddBaseValue CMD = new CommandAddBaseValue();
    private ValueManagerServer vm;

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("base").requires(cs -> cs.hasPermissionLevel(2))
            .then(Commands.argument("value", IntegerArgumentType.integer())
                .then(Commands.argument("item", ItemArgument.item())
                    .executes((CommandSource)->{
                        ServerWorld world = CommandSource.getSource().getWorld();
                        return CMD.setBaseValue(world,ItemArgument.getItem(CommandSource, "item").getItem()
                            ,IntegerArgumentType.getInteger(CommandSource, "value"));
                    }))
                .executes((CommandSource)->{
                    ServerWorld world = CommandSource.getSource().getWorld();
                    try{
                        Item item = CommandSource.getSource().asPlayer().getHeldItemMainhand().getItem();
                        return CMD.setBaseValue(world,item,IntegerArgumentType.getInteger(CommandSource, "value"));
                    }catch(CommandSyntaxException e){
                        GTS.LOGGER.error("server attempted to assign value to the item in their hand.");
                        return 0;
                    }
                }));
            
    }

    private int setBaseValue(ServerWorld world,Item key, int value){
        if(vm == null){
            vm = (ValueManagerServer)ValueManager.getVM(world);
        }
        GTS.LOGGER.info("add base value "+value+" to "+key.toString());
        vm.setBaseValue(key, value);
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        GTS.LOGGER.error("CommandAddBaseValue#run ran");
        return 0;
    }
}