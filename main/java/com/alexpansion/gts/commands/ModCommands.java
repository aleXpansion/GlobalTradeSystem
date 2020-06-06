package com.alexpansion.gts.commands;

import com.alexpansion.gts.GTS;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(GTS.MOD_ID)
                        .then(CommandAddBaseValue.register(dispatcher))
                        .then(CommandResetValues.register(dispatcher))
        );

    }

}