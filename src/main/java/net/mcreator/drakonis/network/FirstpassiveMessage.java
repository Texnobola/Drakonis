package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;

import top.theillusivec4.curios.api.CuriosApi;
import net.mcreator.drakonis.DrakonisMod;
import net.mcreator.drakonis.init.DrakonisModItems;

@EventBusSubscriber
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
                Player player = context.player();
                ItemStack targetStack = ItemStack.EMPTY;

                // 1. Check Main Hand First
                if (player.getMainHandItem().getItem() == DrakonisModItems.OLOVTOSHI.get()) {
                    targetStack = player.getMainHandItem();
                } 
                // 2. If not in hand, check Curios
                else {
                    var curiosOpt = CuriosApi.getCuriosInventory(player);
                    if (curiosOpt.isPresent()) {
                        var found = curiosOpt.get().findFirstCurio(DrakonisModItems.OLOVTOSHI.get());
                        if (found.isPresent()) {
                            targetStack = found.get().stack();
                        }
                    }
                }

                // 3. If we found the stone, Toggle the Power
                if (!targetStack.isEmpty()) {
                    CustomData data = targetStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                    CompoundTag tag = data.copyTag();
                    
                    boolean isActive = tag.getBoolean("Power1Active");
                    tag.putBoolean("Power1Active", !isActive); // Flip Switch
                    
                    targetStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    
                    player.displayClientMessage(Component.literal("Torch Mode: " + (!isActive ? "ON" : "OFF")), true);
                }
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DrakonisMod.addNetworkMessage(FirstpassiveMessage.TYPE, FirstpassiveMessage.STREAM_CODEC, FirstpassiveMessage::handleData);
    }
}