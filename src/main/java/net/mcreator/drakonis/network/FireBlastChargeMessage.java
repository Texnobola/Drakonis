package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.drakonis.DrakonisMod;

public record FireBlastChargeMessage(boolean charging) implements CustomPacketPayload {
    public static final Type<FireBlastChargeMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "fire_blast_charge"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FireBlastChargeMessage> STREAM_CODEC = StreamCodec.of(
        (RegistryFriendlyByteBuf buffer, FireBlastChargeMessage message) -> buffer.writeBoolean(message.charging),
        (RegistryFriendlyByteBuf buffer) -> new FireBlastChargeMessage(buffer.readBoolean())
    );

    @Override
    public Type<FireBlastChargeMessage> type() {
        return TYPE;
    }

    public static void handleData(final FireBlastChargeMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                Player player = context.player();
                Level level = player.level();
                var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
                
                if (message.charging) {
                    data.fireBlastCharging = true;
                    data.fireBlastChargeTime = level.getGameTime();
                } else {
                    if (data.fireBlastCharging) {
                        long chargeTime = level.getGameTime() - data.fireBlastChargeTime;
                        if (chargeTime >= 100) {
                            net.mcreator.drakonis.procedures.FireBlastProcedure.execute(level, player);
                        }
                        data.fireBlastCharging = false;
                        data.fireBlastChargeTime = 0;
                    }
                }
                data.syncPlayerVariables(player);
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }
}
