package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.drakonis.network.PlayPlayerAnimationMessage;
import net.mcreator.drakonis.DrakonisMod;

public class AnimationHelper {
    public static void playAnimation(Player player, String animationName, boolean override, boolean firstPerson) {
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (animationName == null || animationName.isEmpty()) {
                return;
            }
            if (animationName.length() > 256) {
                return;
            }
            DrakonisMod.LOGGER.info("[ANIM] Sending animation packet: " + animationName);
            PacketDistributor.sendToPlayer(serverPlayer, 
                new PlayPlayerAnimationMessage(player.getId(), animationName, override, firstPerson));
        }
    }
    
    public static void stopAnimation(Player player) {
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, 
                new PlayPlayerAnimationMessage(player.getId(), "", false, false));
        }
    }
}
