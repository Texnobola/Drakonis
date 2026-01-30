package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;

public record DragonConcentrationToggleMessage(boolean isPressed) implements CustomPacketPayload {
    public static final Type<DragonConcentrationToggleMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "dragon_concentration"));
    public static final StreamCodec<FriendlyByteBuf, DragonConcentrationToggleMessage> STREAM_CODEC = StreamCodec.of(
        (FriendlyByteBuf buffer, DragonConcentrationToggleMessage message) -> buffer.writeBoolean(message.isPressed),
        (FriendlyByteBuf buffer) -> new DragonConcentrationToggleMessage(buffer.readBoolean())
    );

    private static final int CONCENTRATION_DURATION = 20 * 20;
    private static final int TOGGLE_COOLDOWN = 30 * 20;
    
    @Override
    public Type<DragonConcentrationToggleMessage> type() {
        return TYPE;
    }

    public static void handleData(final DragonConcentrationToggleMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                try {
                    Player player = context.player();
                    Level level = player.level();
                    long worldTime = level.getGameTime();
                    var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
                    
                    DrakonisMod.LOGGER.info("[DRAGON CONC] Button pressed=" + message.isPressed + " at worldTime=" + worldTime);
                    
                    if (message.isPressed) {
                        // Button pressed - start charging animation
                        boolean hasFireStone = false;
                        for (int i = 0; i < 9; i++) {
                            if (player.getInventory().getItem(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                                hasFireStone = true;
                                break;
                            }
                        }
                        if (!hasFireStone && !player.getOffhandItem().is(DrakonisModItems.OLOVTOSHI.get())) {
                            var curiosInv = DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
                            if (curiosInv != null) {
                                for (int i = 0; i < curiosInv.getSlots(); i++) {
                                    if (curiosInv.getStackInSlot(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                                        hasFireStone = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!hasFireStone) return;
                        
                        if (worldTime < data.dragonConcentrationToggleCooldown) {
                            long remaining = (data.dragonConcentrationToggleCooldown - worldTime) / 20;
                            player.sendSystemMessage(Component.literal("§cConcentration cooldown: " + remaining + "s"));
                            return;
                        }
                        if (worldTime < data.dragonConcentrationOnProcCooldown) {
                            long remaining = (data.dragonConcentrationOnProcCooldown - worldTime) / 20;
                            player.sendSystemMessage(Component.literal("§cConcentration locked after ultimate: " + remaining + "s"));
                            return;
                        }
                        
                        // Mark as holding - set activation time after animation completes (10 seconds)
                        data.isHoldingConcentration = true;
                        data.concentrationAnimationPlayed = false;
                        data.dragonConcentrationActivationTime = worldTime + 200; // 10 seconds * 20 ticks
                        data.dragonConcentrationUntil = worldTime + 200;
                        data.dragonConcentrationStartTime = worldTime; // Store when holding started
                        data.syncPlayerVariables(player);
                        
                        // Send animation packet immediately
                        DrakonisMod.LOGGER.info("[DRAGON CONC] Sending animation packet immediately");
                        net.mcreator.drakonis.procedures.AnimationHelper.playAnimation(player, "drakonis:dragon_concentration", true, false);
                        
                        if (level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 50; i++) {
                                double angle = Math.random() * Math.PI * 2;
                                double dist = 2.0;
                                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                                    player.getX() + Math.cos(angle) * dist, player.getY() + 1, player.getZ() + Math.sin(angle) * dist,
                                    1, 0, 0.5, 0, 0.05);
                            }
                        }
                    } else {
                        // Button released - deactivate
                        data.isHoldingConcentration = false;
                        data.dragonConcentrationUntil = 0;
                        data.dragonConcentrationToggleCooldown = worldTime + TOGGLE_COOLDOWN;
                        data.syncPlayerVariables(player);
                        
                        player.sendSystemMessage(Component.literal("§cConcentration DEACTIVATED"));
                    }
                } catch (Exception e) {
                    DrakonisMod.LOGGER.error("Error in concentration toggle: ", e);
                }
            });
        }
    }
}
