package net.mcreator.drakonis.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class FireBlastProcedure {
    public static void execute(Level level, Player player) {
        if (level.isClientSide) return;
        
        Vec3 lookVec = player.getLookAngle();
        Vec3 blastPos = player.position().add(lookVec.scale(3));
        
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 200; i++) {
                double angle = Math.random() * Math.PI * 2;
                double pitch = Math.random() * Math.PI;
                double dist = Math.random() * 5;
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    blastPos.x + Math.cos(angle) * Math.sin(pitch) * dist,
                    blastPos.y + Math.cos(pitch) * dist,
                    blastPos.z + Math.sin(angle) * Math.sin(pitch) * dist,
                    5, 0, 0, 0, 0.2);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    blastPos.x + Math.cos(angle) * Math.sin(pitch) * dist * 0.5,
                    blastPos.y + Math.cos(pitch) * dist * 0.5,
                    blastPos.z + Math.sin(angle) * Math.sin(pitch) * dist * 0.5,
                    3, 0, 0, 0, 0.1);
            }
        }
        
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.0F, 0.8F);
        
        double originalX = player.getX();
        double originalY = player.getY();
        double originalZ = player.getZ();
        
        // Apply explosion effect and damage nearby entities except the caster player
        if (level instanceof ServerLevel serverLevel) {
            // Get entities in blast radius and damage them (except the caster)
            for (Entity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, 
                new AABB(blastPos.x - 6, blastPos.y - 6, blastPos.z - 6, blastPos.x + 6, blastPos.y + 6, blastPos.z + 6))) {
                if (entity != player) {
                    entity.hurt(serverLevel.damageSources().magic(), 12.0F);
                }
            }
            // Create visual explosion effect without damaging
            serverLevel.explode(null, blastPos.x, blastPos.y, blastPos.z, 5.0F, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
        }
        
        player.setPos(originalX, originalY, originalZ);
        player.setDeltaMovement(player.getDeltaMovement().multiply(0.5, 0.5, 0.5));
    }
}
