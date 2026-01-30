package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.drakonis.network.DrakonisModVariables;

public class FireBlastChargingTickProcedure {
    public static void execute(Player player) {
        if (player == null) return;
        Level level = player.level();
        if (level.isClientSide) return;
        
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        if (data.fireBlastCharging) {
            long chargeTime = level.getGameTime() - data.fireBlastChargeTime;
            
            if (chargeTime == 1) {
                AnimationHelper.playAnimation(player, "drakonis:fire_blast_charge", true, false);
            }
            
            if (level instanceof ServerLevel serverLevel) {
                int particleCount = (int) Math.min(chargeTime / 2, 50);
                for (int i = 0; i < particleCount; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double dist = 1.5 + Math.random() * 0.5;
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX() + Math.cos(angle) * dist,
                        player.getY() + 1 + Math.random(),
                        player.getZ() + Math.sin(angle) * dist,
                        1, 0, 0.5, 0, 0.05);
                }
                
                if (chargeTime % 20 == 0) {
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX(), player.getY() + 1, player.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1);
                }
            }
        } else {
            AnimationHelper.stopAnimation(player);
        }
    }
}
