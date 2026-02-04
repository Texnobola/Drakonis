/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.drakonis.init;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.drakonis.network.FirstpassiveMessage;
import net.mcreator.drakonis.network.EmberDominionToggleMessage;
import net.mcreator.drakonis.network.FireBlastChargeMessage;
import net.mcreator.drakonis.network.DragonConcentrationToggleMessage;
import net.mcreator.drakonis.network.IceGloveTransformMessage;
import net.mcreator.drakonis.network.EvolutionMessage;

@EventBusSubscriber(Dist.CLIENT)
public class DrakonisModKeyMappings {
	public static final KeyMapping FIRSTPASSIVE = new KeyMapping("key.drakonis.firstpassive", GLFW.GLFW_KEY_R, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketDistributor.sendToServer(new FirstpassiveMessage(0, 0));
				FirstpassiveMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	public static final KeyMapping EMBER_DOMINION = new KeyMapping("key.drakonis.ember_dominion", GLFW.GLFW_KEY_F, "key.categories.gameplay") {
		private boolean isDownOld = false;
		private boolean wasShiftHeld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			Minecraft mc = Minecraft.getInstance();
			boolean shiftHeld = mc.player != null && mc.player.isShiftKeyDown();
			
			if (isDownOld != isDown) {
				// Check if player has ice stone in curios
				boolean hasIceStone = mc.player != null && hasItemInCurios(mc.player, net.mcreator.drakonis.init.DrakonisModItems.MUZTOSHI.get().getDefaultInstance());
				
				if (hasIceStone && !shiftHeld && isDown) {
					// Ice glove transformation
					PacketDistributor.sendToServer(new IceGloveTransformMessage(true));
				} else if (shiftHeld) {
					// Fire Blast charging (hold-based)
					PacketDistributor.sendToServer(new FireBlastChargeMessage(isDown));
				} else {
					// Ember Dominion (now hold-based)
					PacketDistributor.sendToServer(new EmberDominionToggleMessage(isDown));
				}
			}
			isDownOld = isDown;
		}
		
		private boolean hasItemInCurios(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.ItemStack item) {
			var curiosInventory = net.mcreator.drakonis.DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
			if (curiosInventory == null)
				return false;
			
			for (int i = 0; i < curiosInventory.getSlots(); i++) {
				net.minecraft.world.item.ItemStack stack = curiosInventory.getStackInSlot(i);
				if (net.minecraft.world.item.ItemStack.isSameItem(stack, item))
					return true;
			}
			return false;
		}
	};

	public static final KeyMapping DRAGON_CONCENTRATION = new KeyMapping("key.drakonis.dragon_concentration", GLFW.GLFW_KEY_C, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown) {
				PacketDistributor.sendToServer(new DragonConcentrationToggleMessage(isDown));
			}
			isDownOld = isDown;
		}
	};

	public static final KeyMapping EVOLUTION = new KeyMapping("key.drakonis.evolution", GLFW.GLFW_KEY_V, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				// Evolution trigger - V key
				PacketDistributor.sendToServer(new EvolutionMessage(true));
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(FIRSTPASSIVE);
		event.register(EMBER_DOMINION);
		event.register(DRAGON_CONCENTRATION);
		event.register(EVOLUTION);
	}

	@EventBusSubscriber(Dist.CLIENT)
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				FIRSTPASSIVE.consumeClick();
				EMBER_DOMINION.consumeClick();
				DRAGON_CONCENTRATION.consumeClick();
				EVOLUTION.consumeClick();
			}
		}
	}
}