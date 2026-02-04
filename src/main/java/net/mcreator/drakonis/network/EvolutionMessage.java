package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.drakonis.DrakonisMod;

public record EvolutionMessage(boolean activate) implements CustomPacketPayload {
	public static final Type<EvolutionMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "evolution"));
	public static final StreamCodec<FriendlyByteBuf, EvolutionMessage> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, EvolutionMessage message) -> {
		buffer.writeBoolean(message.activate);
	}, (FriendlyByteBuf buffer) -> new EvolutionMessage(buffer.readBoolean()));

	@Override
	public Type<EvolutionMessage> type() {
		return TYPE;
	}

	public static void handleData(final EvolutionMessage message, final IPayloadContext context) {
		if (context.flow().isServerbound()) {
			context.enqueueWork(() -> {
				Player player = context.player();
				execute(player, message.activate);
			}).exceptionally(e -> {
				context.disconnect(Component.translatable("drakonis.networking.failed", e.getMessage()));
				return null;
			});
		}
	}

	public static void execute(Player player, boolean activate) {
		if (player == null)
			return;

		// Just call the evolution summon procedure which handles all the logic
		net.mcreator.drakonis.procedures.EvolutionSummonProcedure.execute(player);
	}
}
