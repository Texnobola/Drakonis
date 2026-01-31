package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.DrakonisMod;

public class MuztoshiSummonProcedure {
    private static final long SUMMON_COOLDOWN = 200;
    
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof Player player)) return;
        
        Level level = player.level();
        if (level.isClientSide) return;
        
        long worldTime = level.getGameTime();
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        if (worldTime < data.snowGolemSummonCooldown) {
            long remaining = (data.snowGolemSummonCooldown - worldTime) / 20;
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bSnow Golem Summon cooldown: " + remaining + "s"));
            return;
        }
        
        if (!data.isSnowGolemSummoning) {
            data.isSnowGolemSummoning = true;
            data.snowGolemSummonStartTime = worldTime;
            data.snowGolemSummonCooldown = worldTime + SUMMON_COOLDOWN;
            data.markSyncDirty();
            
            DrakonisMod.LOGGER.info("[SNOW GOLEM] Sending animation packet: drakonis:snow_summon_caster");
            AnimationHelper.playAnimation(player, "drakonis:snow_summon_caster", true, false);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§b§lSNOW GOLEM SUMMON §eACTIVATING..."));
        }
    }
}
