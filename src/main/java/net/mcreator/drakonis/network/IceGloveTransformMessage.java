package net.mcreator.drakonis.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record IceGloveTransformMessage(boolean activate) implements CustomPacketPayload {
	public static final Type<IceGloveTransformMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "ice_glove_transform"));
	public static final StreamCodec<FriendlyByteBuf, IceGloveTransformMessage> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, IceGloveTransformMessage message) -> {
		buffer.writeBoolean(message.activate);
	}, (FriendlyByteBuf buffer) -> new IceGloveTransformMessage(buffer.readBoolean()));

	@Override
	public Type<IceGloveTransformMessage> type() {
		return TYPE;
	}

	public static void handleData(final IceGloveTransformMessage message, final IPayloadContext context) {
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

		Level level = player.level();
		
		// Check if player has Muztoshi in curios
		boolean hasMuztoshi = DrakonisMod.CuriosApiHelper.getCuriosInventory(player) != null &&
			hasItemInCurios(player, DrakonisModItems.MUZTOSHI.get().getDefaultInstance());

		if (!hasMuztoshi)
			return;

		if (activate) {
			// Store current items
			ItemStack mainHand = player.getMainHandItem().copy();
			ItemStack offHand = player.getOffhandItem().copy();
			
			// Check if already transformed
			if (mainHand.is(DrakonisModItems.ICY_GLOVE_RIGHT.get()) && offHand.is(DrakonisModItems.ICY_GLOVE_LEFT.get()))
				return;

			// Store items in player data if not empty
			if (!mainHand.isEmpty()) {
				player.getPersistentData().put("StoredMainHand", mainHand.save(player.registryAccess()));
			}
			if (!offHand.isEmpty()) {
				player.getPersistentData().put("StoredOffHand", offHand.save(player.registryAccess()));
			}

			// Play transformation animation FIRST
			net.mcreator.drakonis.procedures.AnimationHelper.playAnimation(player, "drakonis:animation.drakonis.street_fighter", true, false);

			// Delay glove equipping until animation completes (4 seconds)
			net.mcreator.drakonis.DrakonisMod.queueServerWork(80, () -> {
				player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(DrakonisModItems.ICY_GLOVE_RIGHT.get()));
				player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, new ItemStack(DrakonisModItems.ICY_GLOVE_LEFT.get()));
				player.displayClientMessage(Component.literal("§b✦ Ice Gloves Activated!"), true);
			});
		} else {
			// Deactivate - restore items
			if (player.getPersistentData().contains("StoredMainHand")) {
				ItemStack mainHand = ItemStack.parse(player.registryAccess(), player.getPersistentData().getCompound("StoredMainHand")).orElse(ItemStack.EMPTY);
				ItemStack offHand = ItemStack.parse(player.registryAccess(), player.getPersistentData().getCompound("StoredOffHand")).orElse(ItemStack.EMPTY);
				
				player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, mainHand);
				player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, offHand);
				
				player.getPersistentData().remove("StoredMainHand");
				player.getPersistentData().remove("StoredOffHand");
			} else {
				player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, ItemStack.EMPTY);
			}
			
			player.displayClientMessage(Component.literal("§b✦ Ice Gloves Deactivated!"), true);
		}
	}

	private static boolean hasItemInCurios(Player player, ItemStack item) {
		var curiosInventory = DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
		if (curiosInventory == null)
			return false;
		
		for (int i = 0; i < curiosInventory.getSlots(); i++) {
			ItemStack stack = curiosInventory.getStackInSlot(i);
			if (ItemStack.isSameItem(stack, item))
				return true;
		}
		return false;
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		DrakonisMod.addNetworkMessage(TYPE, STREAM_CODEC, IceGloveTransformMessage::handleData);
	}
}