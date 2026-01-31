package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.drakonis.DrakonisMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record PlayPlayerAnimationMessage(int player, String animation, boolean override, boolean firstPerson) implements CustomPacketPayload {

	public static final Type<PlayPlayerAnimationMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "play_player_animation"));
	public static final StreamCodec<FriendlyByteBuf, PlayPlayerAnimationMessage> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, PlayPlayerAnimationMessage message) -> {
		buffer.writeInt(message.player);
		buffer.writeUtf(message.animation, 256);
		buffer.writeBoolean(message.override);
		buffer.writeBoolean(message.firstPerson);
	}, (FriendlyByteBuf buffer) -> new PlayPlayerAnimationMessage(buffer.readInt(), buffer.readUtf(256), buffer.readBoolean(), buffer.readBoolean()));
	
	@Override
	public Type<PlayPlayerAnimationMessage> type() {
		return TYPE;
	}

	public static void handleData(final PlayPlayerAnimationMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				try {
					Player player = (Player) context.player().level().getEntity(message.player);
					if (player == null) {
						DrakonisMod.LOGGER.error("[ANIM] Player entity not found for ID: " + message.player);
						return;
					}
					CompoundTag data = player.getPersistentData();
					if (message.animation == null || message.animation.isEmpty()) {
						// Only reset if there's a looping animation (like Fire Blast) or no animation
						String currentAnim = data.getString("PlayerCurrentAnimation");
						
						// Always allow reset if nothing is playing
						if (currentAnim == null || currentAnim.isEmpty()) {
							data.putBoolean("ResetPlayerAnimation", true);
							data.putBoolean("FirstPersonAnimation", false);
							data.remove("PlayerCurrentAnimation");
							data.remove("PlayerAnimationProgress");

						} else {
							// Check if the current animation is one-shot (not looping)
							// These animations should NOT be reset while playing
							boolean isOneShotAnimation = currentAnim.contains("dragon_concentration") || 
														currentAnim.contains("ember_dominion") ||
														currentAnim.contains("dragon_strike") ||
														currentAnim.contains("snow_summon_caster");
							
							if (!isOneShotAnimation) {
								// This is a looping animation like fire_blast_charge - safe to reset
								data.putBoolean("ResetPlayerAnimation", true);
								data.putBoolean("FirstPersonAnimation", false);
								data.remove("PlayerCurrentAnimation");
								data.remove("PlayerAnimationProgress");
						} else {
							}
						}
					} else {
						data.putString("PlayerCurrentAnimation", message.animation);
						data.putBoolean("OverrideCurrentAnimation", message.override);
						data.putBoolean("FirstPersonAnimation", message.firstPerson);
						DrakonisMod.LOGGER.info("[ANIM] Set animation NBT: " + message.animation + " override=" + message.override);
					}
				} catch (Exception e) {
					DrakonisMod.LOGGER.error("Error handling animation packet: ", e);
					e.printStackTrace();
				}
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DrakonisMod.addNetworkMessage(PlayPlayerAnimationMessage.TYPE, PlayPlayerAnimationMessage.STREAM_CODEC, PlayPlayerAnimationMessage::handleData);
	}
}