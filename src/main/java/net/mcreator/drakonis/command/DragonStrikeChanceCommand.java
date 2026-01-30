package net.mcreator.drakonis.command;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;

import net.mcreator.drakonis.network.DrakonisModVariables;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@EventBusSubscriber
public class DragonStrikeChanceCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dragonstrike")
            .requires(s -> s.hasPermission(2))
            .then(Commands.literal("chance")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(DragonStrikeChanceCommand::setChance)))));
    }

    private static int setChance(CommandContext<net.minecraft.commands.CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        double chance = DoubleArgumentType.getDouble(ctx, "value");
        
        var data = target.getData(DrakonisModVariables.PLAYER_VARIABLES);
        data.dragonStrikeProcChance = chance;
        data.syncPlayerVariables(target);
        
        ctx.getSource().sendSuccess(() -> Component.literal("§6Dragon Strike proc chance set to " + (chance * 100) + "% for " + target.getName().getString()), true);
        target.sendSystemMessage(Component.literal("§6Your Dragon Strike proc chance is now " + (chance * 100) + "%"));
        
        return 1;
    }
}
