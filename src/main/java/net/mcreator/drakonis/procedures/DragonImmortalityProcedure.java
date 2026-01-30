package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

import net.mcreator.drakonis.network.DrakonisModVariables;

@EventBusSubscriber
public class DragonImmortalityProcedure {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        
        Level level = player.level();
        long worldTime = level.getGameTime();
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        // Check immortality
        if (worldTime < data.dragonImmortalityUntil) {
            event.setCanceled(true);
            
            // Optional: scorch attacker
            if (event.getSource().getEntity() != null) {
                event.getSource().getEntity().setRemainingFireTicks(20);
            }
        }
    }
}
