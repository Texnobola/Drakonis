package net.mcreator.drakonis.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.drakonis.network.DrakonisModVariables;

public class MuztoshiPassiveTickProcedure {
    private static final int SUMMON_DURATION = 80;
    
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof Player player)) return;
        
        Level level = player.level();
        long worldTime = level.getGameTime();
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        if (!data.isSnowGolemSummoning) return;
        
        long elapsed = worldTime - data.snowGolemSummonStartTime;
        
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            double radius = 2.0;
            for (int i = 0; i < 3; i++) {
                double angle = (elapsed * 0.2 + i * 120) * Math.PI / 180.0;
                double x = pos.x + Math.cos(angle) * radius;
                double z = pos.z + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, pos.y + 0.5, z, 2, 0.2, 0.2, 0.2, 0.02);
            }
            
            if (elapsed >= 40 && elapsed <= 60) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 1, pos.z, 10, 1.5, 1.5, 1.5, 0.1);
            }
        }
        
        if (elapsed >= SUMMON_DURATION) {
            data.isSnowGolemSummoning = false;
            data.markSyncDirty();
            
            if (level instanceof ServerLevel serverLevel) {
                Vec3 playerPos = player.position();
                
                for (int i = 0; i < 3; i++) {
                    double angle = (i * 120) * Math.PI / 180.0;
                    double distance = 2.5;
                    double x = playerPos.x + Math.cos(angle) * distance;
                    double z = playerPos.z + Math.sin(angle) * distance;
                    
                    SnowGolem golem = new SnowGolem(net.minecraft.world.entity.EntityType.SNOW_GOLEM, level);
                    golem.moveTo(x, playerPos.y, z, player.getYRot(), 0);
                    golem.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(BlockPos.containing(x, playerPos.y, z)), 
                        MobSpawnType.MOB_SUMMONED, null);
                    golem.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
                    serverLevel.addFreshEntity(golem);
                    
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, playerPos.y + 1, z, 1, 0, 0, 0, 0);
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, playerPos.y + 1, z, 30, 0.5, 1, 0.5, 0.1);
                }
            }
        }
    }
}
