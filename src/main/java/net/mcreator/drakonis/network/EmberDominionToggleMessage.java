package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;

public record EmberDominionToggleMessage(boolean isPressed) implements CustomPacketPayload {
    public static final Type<EmberDominionToggleMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "ember_dominion_toggle"));
    public static final StreamCodec<FriendlyByteBuf, EmberDominionToggleMessage> STREAM_CODEC = StreamCodec.of(
        (FriendlyByteBuf buffer, EmberDominionToggleMessage message) -> buffer.writeBoolean(message.isPressed),
        (FriendlyByteBuf buffer) -> new EmberDominionToggleMessage(buffer.readBoolean())
    );

    @Override
    public Type<EmberDominionToggleMessage> type() {
        return TYPE;
    }

    public static void handleData(final EmberDominionToggleMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                try {
                    Player player = context.player();
                    Level level = player.level();
                    var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
                    
                    if (message.isPressed) {
                        // Check if already active - if so, deactivate instead
                        if (data.emberDominionActive) {
                            // Only deactivate if FULLY active, not while animating
                            data.emberDominionActive = false;
                            data.emberDominionTickCounter = 0; // Reset counter to clear music effects
                            data.syncPlayerVariables(player);
                            
                            DrakonisMod.LOGGER.info("[EMBER DOM] Deactivating ember dominion");
                            net.mcreator.drakonis.procedures.AnimationHelper.stopAnimation(player);
                            
                            // Stop the music playing on deactivation
                            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                net.minecraft.network.protocol.game.ClientboundStopSoundPacket stopSoundPacket = 
                                    new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                                        net.mcreator.drakonis.init.DrakonisModSounds.HAKARI_DANCE.get().getLocation(),
                                        net.minecraft.sounds.SoundSource.PLAYERS
                                    );
                                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                    serverPlayer.connection.send(stopSoundPacket);
                                }
                            }
                            
                            player.sendSystemMessage(Component.literal("§cEmber Dominion DEACTIVATED"));
                            return;
                        }
                        
                        if (data.isHoldingEmberDominion) {
                            // Cancel activation in progress
                            DrakonisMod.LOGGER.info("[EMBER DOM] Cancelling activation");
                            data.isHoldingEmberDominion = false;
                            data.syncPlayerVariables(player);
                            net.mcreator.drakonis.procedures.AnimationHelper.stopAnimation(player);
                            return;
                        }
                        
                        // Button pressed - start animation
                        long currentTime = level.getGameTime();
                        long lastToggle = data.emberDominionLastToggle;
                        if (currentTime - lastToggle < 10) return;
                        
                        boolean hasFireStone = false;
                        for (int i = 0; i < 9; i++) {
                            ItemStack stack = player.getInventory().getItem(i);
                            if (stack.is(DrakonisModItems.OLOVTOSHI.get())) {
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
                        if (!hasFireStone) return;
                        
                        // Set holding state - animation is 10 seconds (200 ticks)
                        data.isHoldingEmberDominion = true;
                        data.emberDominionActivationTime = currentTime + 200; // 10 seconds * 20 ticks
                        data.emberDominionStartTime = currentTime; // Store when holding started
                        data.emberDominionLastToggle = currentTime;
                        data.syncPlayerVariables(player);
                        
                        // Send animation packet immediately
                        DrakonisMod.LOGGER.info("[EMBER DOM] Sending animation packet immediately");
                        net.mcreator.drakonis.procedures.AnimationHelper.playAnimation(player, "drakonis:ember_dominion", true, false);
                        
                        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            for (int i = 0; i < 100; i++) {
                                double angle = Math.random() * Math.PI * 2;
                                double dist = Math.random() * 3;
                                double height = Math.random() * 3;
                                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                                    player.getX() + Math.cos(angle) * dist, player.getY() + height, player.getZ() + Math.sin(angle) * dist,
                                    1, 0, 0, 0, 0.1);
                                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                                    player.getX() + Math.cos(angle) * dist, player.getY() + height, player.getZ() + Math.sin(angle) * dist,
                                    1, 0, 0, 0, 0);
                            }
                            level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.FIRECHARGE_USE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 0.5F);
                        }
                    } else {
                        // Button released - do nothing, animation will play until completion
                        DrakonisMod.LOGGER.info("[EMBER DOM] Button released, animation continues");
                    }
                } catch (Exception e) {
                    DrakonisMod.LOGGER.error("Error in ember dominion toggle: ", e);
                }
            });
        }
    }
}
