package net.mcreator.drakonis.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;

public class EvolutionTickProcedure {
    private static final int EVOLUTION_DURATION = 300; // 15 seconds (matches evolution_aura_smooth animation)
    
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof Player player)) return;
        
        Level level = player.level();
        long worldTime = level.getGameTime();
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        if (!data.isEvolvingToArmor) return;
        
        long elapsed = worldTime - data.evolutionStartTime;
        
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            
            // Create massive spiral particle effect during evolution
            double radius = 1.5 + (elapsed * 0.02);
            
            // Multiple spirals for epic effect
            for (int spiral = 0; spiral < 3; spiral++) {
                for (int i = 0; i < 12; i++) {
                    double angle = (elapsed * 0.2 + i * 30 + spiral * 120) * Math.PI / 180.0;
                    double x = pos.x + Math.cos(angle) * radius;
                    double z = pos.z + Math.sin(angle) * radius;
                    double y = pos.y + 1 + Math.sin(elapsed * 0.05 + spiral) * 1.0;
                    
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 3, 0.2, 0.2, 0.2, 0.1);
                }
            }
            
            // Intensive bursts at key points
            if (elapsed >= 75 && elapsed <= 85) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 1, pos.z, 50, 3, 3, 3, 0.2);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 5, 2, 2, 2, 0.1);
            }
            
            // Mid-point intense effect
            if (elapsed >= 150 && elapsed <= 160) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 1, pos.z, 75, 4, 4, 4, 0.25);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.5, pos.z, 8, 3, 3, 3, 0.15);
            }
            
            // Final burst at end
            if (elapsed >= 280 && elapsed <= 300) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 1, pos.z, 100, 5, 5, 5, 0.3);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 10, 4, 4, 4, 0.2);
            }
            
            // Affect nearby players in 100 block radius
            serverLevel.getPlayers((p) -> p != player && p.distanceTo(player) <= 100.0).forEach(nearbyPlayer -> {
                Vec3 nearbyPos = nearbyPlayer.position();
                double dist = nearbyPos.distanceTo(pos);
                int particleCount = (int) (30 * (1.0 - dist / 100.0)); // More particles for closer players
                if (particleCount > 0) {
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, nearbyPos.x, nearbyPos.y + 1, nearbyPos.z, particleCount, 1, 1, 1, 0.1);
                }
            });
        }
        
        // When animation completes, equip the armor
        if (elapsed >= EVOLUTION_DURATION) {
            data.isEvolvingToArmor = false;
            data.markSyncDirty();
            
            // Keep gloves - do NOT remove them
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            
            // Set armor pieces (keep gloves in hands)
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, 
                new ItemStack(DrakonisModItems.ICY_HELMET_HELMET.get()));
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, 
                new ItemStack(DrakonisModItems.ICY_CHESTPLATE_CHESTPLATE.get()));
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, 
                new ItemStack(DrakonisModItems.ICY_LEGGINGS_LEGGINGS.get()));
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, 
                new ItemStack(DrakonisModItems.ICY_BOOTS_BOOTS.get()));
            
            // Gloves stay in hands - don't remove them
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§b✦ ULTIMATE EVOLUTION ACHIEVED ✦"), true);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§bFull Ice Form!"), true);
        }
    }
}
