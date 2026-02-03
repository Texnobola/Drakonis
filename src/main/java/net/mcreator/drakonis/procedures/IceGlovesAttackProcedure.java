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
public class IceGlovesAttackProcedure {
    // Constants
    private static final float BASE_BONUS_DAMAGE = 25.0F;
    private static final double SPLASH_RADIUS = 2.0;
    private static final float SPLASH_DAMAGE = 8.0F;
    private static final int SLOWNESS_DURATION = 5 * 20; // 5 seconds
    private static final int WEAKNESS_DURATION = 3 * 20; // 3 seconds
    private static final int SLOWNESS_LEVEL = 1;
    private static final int WEAKNESS_LEVEL = 0;

    @SubscribeEvent
    public static void onPlayerMeleeHit(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (player.level().isClientSide) return;
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) return;
        
        Level level = player.level();
        long worldTime = level.getGameTime();
        
        // Check Ice Gloves - must have both gloves
        boolean hasIceGloveLeft = player.getInventory().contains(new net.minecraft.world.item.ItemStack(DrakonisModItems.ICY_GLOVE_LEFT.get()));
        boolean hasIceGloveRight = player.getInventory().contains(new net.minecraft.world.item.ItemStack(DrakonisModItems.ICY_GLOVE_RIGHT.get()));
        
        if (!hasIceGloveLeft || !hasIceGloveRight) return;
        
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        // Play animation
        AnimationHelper.playAnimation(player, "drakonis:glacier_shatter_complete_physics", false, false);
        
        DamageSource coldDamage = new DamageSource(
            level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.MAGIC));
        
        // Primary damage with effects
        target.hurt(coldDamage, BASE_BONUS_DAMAGE);
        
        // Apply slowness and weakness
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, SLOWNESS_LEVEL, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, WEAKNESS_LEVEL, false, false));
        
        // Particle effect - icy/cold particles around target
        if (level instanceof ServerLevel serverLevel) {
            double targetX = target.getX();
            double targetY = target.getY() + target.getBbHeight() / 2;
            double targetZ = target.getZ();
            
            // Ice particles in a circle around the target
            for (int i = 0; i < 12; i++) {
                double angle = (Math.PI * 2 / 12) * i;
                double px = targetX + Math.cos(angle) * 1.2;
                double pz = targetZ + Math.sin(angle) * 1.2;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, px, targetY, pz, 3, 0.1, 0.1, 0.1, 0.05);
            }
            
            // Center particle burst
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, targetX, targetY, targetZ, 8, 0.3, 0.3, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.POOF, targetX, targetY, targetZ, 4, 0.2, 0.2, 0.2, 0.05);
        }
        
        // Splash AoE
        AABB splashBox = new AABB(target.position().subtract(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS),
            target.position().add(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS));
        List<LivingEntity> splashTargets = level.getEntitiesOfClass(LivingEntity.class, splashBox,
            e -> e != player && e != target && e instanceof Mob && e.distanceTo(target) <= SPLASH_RADIUS);
        
        for (LivingEntity splash : splashTargets) {
            splash.hurt(coldDamage, SPLASH_DAMAGE);
            splash.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION / 2, 0, false, false));
        }
        
        // Sound effect
        level.playSound(null, player.blockPosition(), SoundEvents.POWDER_SNOW_STEP, SoundSource.PLAYERS, 1.0F, 1.2F);
        
        data.markSyncDirty();
    }
}
