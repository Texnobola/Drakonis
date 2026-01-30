package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;

public class DragonConcentrationTickProcedure {
    public static void execute(Player player) {
        if (player == null) return;
        Level level = player.level();
        if (level.isClientSide) return;

        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        // Only log when holding concentration
        if (data.isHoldingConcentration) {
            DrakonisMod.LOGGER.info("[DRAGON CONC] Tick execute - holding=true, animationPlayed=" + data.concentrationAnimationPlayed);
        }
        
        boolean hasFireStone = false;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                hasFireStone = true;
                break;
            }
        }
        
        if (!hasFireStone) {
            var curiosInv = net.mcreator.drakonis.DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
            if (curiosInv != null) {
                for (int i = 0; i < curiosInv.getSlots(); i++) {
                    if (curiosInv.getStackInSlot(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                        hasFireStone = true;
                        break;
                    }
                }
            }
        }
        
        long worldTime = level.getGameTime();
        
        // Check if player is currently holding the concentration button
        if (!data.isHoldingConcentration) {
            return;
        }
        
        // Calculate how long we've been holding
        long holdTime = worldTime - data.dragonConcentrationStartTime;
        
        // Animation already sent from toggle message handler - don't send again in tick!
        // Just show the activation message once
        if (holdTime == 1 && !data.concentrationAnimationPlayed) {
            data.concentrationAnimationPlayed = true;
            data.syncPlayerVariables(player);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lDRAGON CONCENTRATION §eACTIVATING..."));
        }
        
        // Check if animation is complete (activate ability after animation ends)
        if (holdTime >= 200) {
            // Ability activates here after animation completes
            data.isHoldingConcentration = false;
            data.dragonConcentrationActivated = true;
            data.syncPlayerVariables(player);
            
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lDRAGON CONCENTRATION §aACTIVATED"));
            return;
        }
        
        // Particle effects
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            for (int i = 0; i < 3; i++) {
                double angle = (worldTime * 0.05 + i * Math.PI * 0.667) % (Math.PI * 2);
                double dist = 1.5;
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    pos.x + Math.cos(angle) * dist, pos.y + 1.5, pos.z + Math.sin(angle) * dist,
                    1, 0.1, 0.1, 0.1, 0.02);
            }
        }
    }
}
