package net.mcreator.drakonis.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;

import java.util.List;

public class EmberDominionTickProcedure {
    private static final double RADIUS = 6.0;
    private static final float DAMAGE = 4.0F;
    private static final float EXECUTE_DAMAGE = 6.0F;
    private static final float EXECUTE_THRESHOLD = 0.25F;
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("drakonis", "ember_dominion_speed");

    public static void execute(Player player) {
        if (player == null) return;
        Level level = player.level();
        if (level.isClientSide) return;

        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        long worldTime = level.getGameTime();
        
        // Handle hold state - wait for animation to complete before activating
        if (data.isHoldingEmberDominion) {
            long holdTime = worldTime - data.emberDominionStartTime;
            
            if (holdTime == 1) {
                // Send animation packet only once when ability first starts
                AnimationHelper.playAnimation(player, "drakonis:ember_dominium_fixed", true, false);
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lEmber Dominion §eACTIVATING..."));
            }
            
            if (holdTime >= 200) {
                // Animation complete (10 seconds) - activate ability
                data.isHoldingEmberDominion = false;
                data.emberDominionActive = true;
                data.syncPlayerVariables(player);
                
                // Play the epic dance music!
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, player.blockPosition(), 
                        net.mcreator.drakonis.init.DrakonisModSounds.HAKARI_DANCE.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lEmber Dominion §aACTIVATED"));
                return;
            } else {
                
                if (level instanceof ServerLevel serverLevel) {
                    Vec3 pos = player.position();
                    for (int i = 0; i < 3; i++) {
                        double angle = (worldTime * 0.1 + i * Math.PI * 0.667) % (Math.PI * 2);
                        double dist = 1.5;
                        serverLevel.sendParticles(ParticleTypes.FLAME,
                            pos.x + Math.cos(angle) * dist, pos.y + 1.5, pos.z + Math.sin(angle) * dist,
                            1, 0.1, 0.1, 0.1, 0.02);
                    }
                }
                return;
            }
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
        
        if (!hasFireStone && data.emberDominionActive) {
            data.emberDominionActive = false;
            removePlayerBuffs(player);
            data.syncPlayerVariables(player);
            return;
        }
        
        if (!data.emberDominionActive) {
            removePlayerBuffs(player);
            return;
        }
        
        applyPlayerBuffs(player);
        data.emberDominionTickCounter++;
        
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            for (int i = 0; i < 5; i++) {
                double angle = (data.emberDominionTickCounter * 0.1 + i * Math.PI * 0.4) % (Math.PI * 2);
                double dist = 2.5;
                serverLevel.sendParticles(ParticleTypes.FLAME, 
                    pos.x + Math.cos(angle) * dist, pos.y + 1, pos.z + Math.sin(angle) * dist,
                    2, 0.1, 0.1, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, 
                    pos.x + Math.cos(angle + Math.PI) * dist, pos.y + 1, pos.z + Math.sin(angle + Math.PI) * dist,
                    1, 0.1, 0.1, 0.1, 0.01);
            }
            
            if (data.emberDominionTickCounter % 10 == 0) {
                for (int i = 0; i < 3; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double dist = Math.random() * RADIUS;
                    serverLevel.sendParticles(ParticleTypes.LAVA, 
                        pos.x + Math.cos(angle) * dist, pos.y + 0.1, pos.z + Math.sin(angle) * dist,
                        1, 0, 0, 0, 0);
                }
            }
        }
        
        if (data.emberDominionTickCounter % 60 == 0) {
            level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_AMBIENT, SoundSource.PLAYERS, 0.5F, 0.8F);
        }
        
        if (data.emberDominionTickCounter % 20 == 0) {
            AABB aabb = new AABB(player.blockPosition()).inflate(RADIUS);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, 
                e -> e instanceof Mob && e.distanceTo(player) <= RADIUS);
            
            for (LivingEntity entity : entities) {
                entity.hurt(new DamageSource(level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.MAGIC)), DAMAGE);
                entity.setRemainingFireTicks(120);
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, false, false));
                
                if (entity.getHealth() < entity.getMaxHealth() * EXECUTE_THRESHOLD) {
                    entity.setRemainingFireTicks(120);
                    entity.hurt(new DamageSource(level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.ON_FIRE)), EXECUTE_DAMAGE);
                    
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.FLAME, entity.getX(), entity.getY() + 1, entity.getZ(),
                            30, 0.5, 0.5, 0.5, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY() + 1, entity.getZ(),
                            15, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }
            
            BlockPos center = player.blockPosition();
            for (int x = -6; x <= 6; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -6; z <= 6; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (pos.distSqr(center) > RADIUS * RADIUS) continue;
                        
                        BlockState state = level.getBlockState(pos);
                        
                        if (state.is(Blocks.SNOW) || state.is(Blocks.ICE) || state.is(Blocks.FROSTED_ICE)) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                        
                        if (state.getBlock() instanceof CampfireBlock && !state.getValue(CampfireBlock.LIT)) {
                            level.setBlock(pos, state.setValue(CampfireBlock.LIT, true), 3);
                        }
                        
                        if (state.getBlock() instanceof CandleBlock && !state.getValue(CandleBlock.LIT)) {
                            level.setBlock(pos, state.setValue(CandleBlock.LIT, true), 3);
                        }
                        
                        if (Math.random() < 0.05 && level.getBlockState(pos).isAir() && 
                            level.getBlockState(pos.below()).isSolid()) {
                            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        
        data.syncPlayerVariables(player);
    }
    
    private static void applyPlayerBuffs(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false));
        
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && !speedAttr.hasModifier(SPEED_MODIFIER_ID)) {
            speedAttr.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
    
    private static void removePlayerBuffs(Player player) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null && speedAttr.hasModifier(SPEED_MODIFIER_ID)) {
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
        }
    }
}
