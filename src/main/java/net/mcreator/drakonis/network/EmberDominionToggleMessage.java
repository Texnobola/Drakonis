package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

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
                        if (data.isHoldingEmberDominion || data.emberDominionActive) {
                            // Deactivate
                            data.isHoldingEmberDominion = false;
                            data.emberDominionActive = false;
                            data.syncPlayerVariables(player);
                            
                            DrakonisMod.LOGGER.info("[EMBER DOM] Deactivating ember dominion");
                            net.mcreator.drakonis.procedures.AnimationHelper.stopAnimation(player);
                            
                            player.sendSystemMessage(Component.literal("§cEmber Dominion DEACTIVATED"));
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
                        
                        // Set holding state - animation is 5 seconds (100 ticks)
                        data.isHoldingEmberDominion = true;
                        data.emberDominionActivationTime = currentTime + 100; // 5 seconds * 20 ticks
                        data.emberDominionStartTime = currentTime; // Store when holding started
                        data.emberDominionLastToggle = currentTime;
                        data.syncPlayerVariables(player);
                        
                        // Send animation packet immediately
                        DrakonisMod.LOGGER.info("[EMBER DOM] Sending animation packet immediately");
                        net.mcreator.drakonis.procedures.AnimationHelper.playAnimation(player, "drakonis:ember_dominion_activate", true, false);
                        
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
