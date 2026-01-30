package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;

import java.util.List;

@EventBusSubscriber
public class DragonStrikeProcProcedure {
    // Constants
    private static final double BASE_PROC = 0.05;
    private static final float BASE_BONUS_DAMAGE = 24.0F;
    private static final int ICD_TICKS = 10;
    private static final double SPLASH_RADIUS = 2.5;
    private static final float SPLASH_DAMAGE = 4.0F;
    private static final int IGNITE_TICKS = 120;
    
    private static final double CONCENTRATION_PROC = 0.20;
    private static final float CONCENTRATION_DAMAGE_MULT = 1.20F;
    private static final int CONCENTRATION_ON_PROC_COOLDOWN = 120 * 20;
    
    private static final int CONSECUTIVE_REQUIRED = 4;
    private static final int CONSECUTIVE_WINDOW = 60 * 20;
    private static final int IMMORTALITY_DURATION = 5 * 60 * 20;
    
    private static final int ULTIMATE_BUFF_DURATION = 5 * 60 * 20;

    @SubscribeEvent
    public static void onPlayerMeleeHit(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (player.level().isClientSide) return;
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) return;
        
        Level level = player.level();
        long worldTime = level.getGameTime();
        
        // Check Fire Stone
        boolean hasFireStone = false;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                hasFireStone = true;
                break;
            }
        }
        if (!hasFireStone && !player.getOffhandItem().is(DrakonisModItems.OLOVTOSHI.get())) {
            // Check Curios
            var curiosInv = net.mcreator.drakonis.DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
            if (curiosInv != null) {
                for (int i = 0; i < curiosInv.getSlots(); i++) {
                    if (curiosInv.getStackInSlot(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                        hasFireStone = true;
                        break;
                    }
                }
            }
            if (!hasFireStone) return;
        }
        
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        // Check ICD
        if (worldTime < data.dragonStrikeICDUntil) return;
        
        // Compute proc chance
        boolean concentrated = worldTime < data.dragonConcentrationUntil;
        double procChance = concentrated ? CONCENTRATION_PROC : data.dragonStrikeProcChance;
        float damageMult = concentrated ? CONCENTRATION_DAMAGE_MULT : 1.0F;
        
        // Roll proc
        if (Math.random() >= procChance) {
            // Failed proc - reset consecutive if window expired
            if (worldTime >= data.dragonConsecutiveWindowUntil) {
                data.dragonConsecutiveCount = 0;
            }
            return;
        }
        
        // PROC SUCCESS
        data.dragonStrikeICDUntil = worldTime + ICD_TICKS;
        
        AnimationHelper.playAnimation(player, "drakonis:dragon_strike", true, false);
        
        float bonusDamage = BASE_BONUS_DAMAGE * damageMult;
        DamageSource magicFire = new DamageSource(
            level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.MAGIC));
        
        // Primary damage
        float appliedPrimary = bonusDamage;
        target.hurt(magicFire, bonusDamage);
        target.setRemainingFireTicks(IGNITE_TICKS);
        
        // Splash AoE
        AABB splashBox = new AABB(target.position().subtract(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS),
            target.position().add(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS));
        List<LivingEntity> splashTargets = level.getEntitiesOfClass(LivingEntity.class, splashBox,
            e -> e != player && e != target && e instanceof Mob && e.distanceTo(target) <= SPLASH_RADIUS);
        
        float totalDamage = appliedPrimary;
        for (LivingEntity splash : splashTargets) {
            splash.hurt(magicFire, SPLASH_DAMAGE);
            splash.setRemainingFireTicks(IGNITE_TICKS / 2);
            totalDamage += SPLASH_DAMAGE;
        }
        
        // Full heal on every proc
        player.setHealth(player.getMaxHealth());
        
        // Ultimate buffs during concentration
        if (concentrated) {
            data.dragonConcentrationUntil = 0;
            data.dragonConcentrationOnProcCooldown = worldTime + CONCENTRATION_ON_PROC_COOLDOWN;
            
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, ULTIMATE_BUFF_DURATION, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ULTIMATE_BUFF_DURATION, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ULTIMATE_BUFF_DURATION, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ULTIMATE_BUFF_DURATION, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ULTIMATE_BUFF_DURATION, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ULTIMATE_BUFF_DURATION, 0, false, false));
            
            player.sendSystemMessage(Component.literal("§6§l⚡ ULTIMATE DRAGON STRIKE ⚡"));
        }
        
        // Consecutive tracking
        if (worldTime >= data.dragonConsecutiveWindowUntil) {
            data.dragonConsecutiveCount = 0;
        }
        data.dragonConsecutiveCount++;
        data.dragonConsecutiveWindowUntil = worldTime + CONSECUTIVE_WINDOW;
        
        // Check for 4-in-a-row immortality
        if (data.dragonConsecutiveCount >= CONSECUTIVE_REQUIRED) {
            data.dragonConsecutiveCount = 0;
            data.dragonConsecutiveWindowUntil = 0;
            data.dragonImmortalityUntil = worldTime + IMMORTALITY_DURATION;
            
            player.sendSystemMessage(Component.literal("§c§l⚔ IMMORTALITY GRANTED ⚔"));
            
            // Massive VFX
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 200; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double pitch = Math.random() * Math.PI;
                    double dist = Math.random() * 5;
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX() + Math.cos(angle) * Math.sin(pitch) * dist,
                        player.getY() + 2 + Math.cos(pitch) * dist,
                        player.getZ() + Math.sin(angle) * Math.sin(pitch) * dist,
                        1, 0, 0, 0, 0.2);
                }
            }
            level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 3.0F, 0.5F);
        }
        
        // Enhanced punch/strike VFX
        if (level instanceof ServerLevel serverLevel) {
            // Punch trail from player to target
            for (int i = 0; i < 50; i++) {
                double t = i / 50.0;
                double x = player.getX() + (target.getX() - player.getX()) * t;
                double y = player.getY() + 1 + (target.getY() + 1 - player.getY() - 1) * t;
                double z = player.getZ() + (target.getZ() - player.getZ()) * t;
                serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 3, 0.15, 0.15, 0.15, 0.08);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.1, 0.1, 0.1, 0.05);
            }
            
            // Impact explosion at target
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + 1, target.getZ(), 3, 0.5, 0.5, 0.5, 0);
            serverLevel.sendParticles(ParticleTypes.LAVA, target.getX(), target.getY() + 1, target.getZ(), 20, 0.5, 0.5, 0.5, 0.2);
            
            // Shockwave ring
            for (int i = 0; i < 60; i++) {
                double angle = (i / 60.0) * Math.PI * 2;
                double radius = 2.0;
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    target.getX() + Math.cos(angle) * radius,
                    target.getY() + 0.1,
                    target.getZ() + Math.sin(angle) * radius,
                    1, 0, 0, 0, 0);
            }
            
            // Rising fire spiral around player
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 4;
                double height = (i / 40.0) * 3;
                double radius = 1.5 - (i / 40.0) * 0.5;
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    player.getX() + Math.cos(angle) * radius,
                    player.getY() + height,
                    player.getZ() + Math.sin(angle) * radius,
                    1, 0, 0, 0, 0.05);
            }
            
            // Concentrated mode extra effects
            if (concentrated) {
                serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH, target.getX(), target.getY() + 1, target.getZ(), 30, 1, 1, 1, 0.1);
                serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.15);
            }
        }
        
        // Enhanced sound effects
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 2.0F, 0.6F);
        level.playSound(null, target.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.8F, 0.8F);
        if (concentrated) {
            level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 2.0F, 1.5F);
        }
        
        data.syncPlayerVariables(player);
    }
}
