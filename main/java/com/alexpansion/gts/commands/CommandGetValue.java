package com.alexpansion.gts.commands;

import com.alexpansion.gts.value.managers.ValueManager;
import com.alexpansion.gts.value.managers.ValueManagerServer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CommandGetValue implements Command<CommandSource> {

    private static final CommandResetValues CMD = new CommandResetValues();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("get_value")
                .requires(cs -> cs.hasPermissionLevel(2))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ValueManagerServer vm = (ValueManagerServer)ValueManager.getVM(world);
        ItemStack stack = context.getSource().asPlayer().getHeldItemMainhand();
        StringTextComponent message = new StringTextComponent(stack.getItem().toString()
            +" value: "+vm.getValue(stack)+", base: "+vm.getBaseValue(stack.getItem()));
        context.getSource().sendFeedback(message, true);
        return 0;
    }
    
}