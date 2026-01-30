package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.mcreator.drakonis.DrakonisMod;

public record LanguageSelectMessage(String langCode) implements CustomPacketPayload {
    public static final Type<LanguageSelectMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "language_select"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LanguageSelectMessage> STREAM_CODEC = StreamCodec.composite(
        net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, LanguageSelectMessage::langCode,
        LanguageSelectMessage::new
    );

    @Override
    public Type<LanguageSelectMessage> type() {
        return TYPE;
    }

    public static void handleData(final LanguageSelectMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    var data = serverPlayer.getData(DrakonisModVariables.PLAYER_VARIABLES);
                    data.hasSelectedLanguage = true;
                    data.syncPlayerVariables(serverPlayer);
                }
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }
}
