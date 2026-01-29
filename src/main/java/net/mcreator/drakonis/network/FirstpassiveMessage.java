package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.mcreator.drakonis.procedures.ToggleTorchModeProcedure;
import net.mcreator.drakonis.DrakonisMod;

public record FirstpassiveMessage(int eventType, int pressedms) implements CustomPacketPayload {
    public static final Type<FirstpassiveMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "key_firstpassive"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FirstpassiveMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, FirstpassiveMessage message) -> {
        buffer.writeInt(message.eventType);
        buffer.writeInt(message.pressedms);
    }, (RegistryFriendlyByteBuf buffer) -> new FirstpassiveMessage(buffer.readInt(), buffer.readInt()));

    @Override
    public Type<FirstpassiveMessage> type() {
        return TYPE;
    }

    public static void handleData(final FirstpassiveMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                pressAction(context.player(), message.eventType, message.pressedms);
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }

    public static void pressAction(Player entity, int type, int pressedms) {
        Level world = entity.level();
        
        // Security Check
        if (!world.hasChunkAt(entity.blockPosition())) return;

        // Key Press Logic
        if (type == 0) {
            // >>> THIS WAS THE ERROR. NOW IT IS FIXED <<<
            ToggleTorchModeProcedure.execute(entity); 
        }
    }
}