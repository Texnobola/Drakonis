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
				if (shiftHeld) {
					// Fire Blast charging (hold-based)
					PacketDistributor.sendToServer(new FireBlastChargeMessage(isDown));
				} else {
					// Ember Dominion (now hold-based)
					PacketDistributor.sendToServer(new EmberDominionToggleMessage(isDown));
				}
			}
			isDownOld = isDown;
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

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(FIRSTPASSIVE);
		event.register(EMBER_DOMINION);
		event.register(DRAGON_CONCENTRATION);
	}

	@EventBusSubscriber(Dist.CLIENT)
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				FIRSTPASSIVE.consumeClick();
				EMBER_DOMINION.consumeClick();
				DRAGON_CONCENTRATION.consumeClick();
			}
		}
	}
}