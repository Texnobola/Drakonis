package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.drakonis.network.DrakonisModVariables;

public class ToggleTorchModeProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;

        // 1. Access the Player Variables (Attachments)
        DrakonisModVariables.PlayerVariables variables = entity.getData(DrakonisModVariables.PLAYER_VARIABLES);

        // 2. Toggle the boolean
        variables.player_torch_active = !variables.player_torch_active;

        // 3. Mark as Dirty (This triggers the auto-sync)
        variables.markSyncDirty();

        // 4. Send Message
        if (entity instanceof Player _player && !_player.level().isClientSide()) {
            String status = variables.player_torch_active ? "ON" : "OFF";
            _player.displayClientMessage(Component.literal("Torch Mode: " + status), true);
        }
    }
}