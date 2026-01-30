package net.mcreator.drakonis.client.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.mcreator.drakonis.network.DrakonisModVariables;

@EventBusSubscriber(Dist.CLIENT)
public class AbilityTimerRenderer {
	
	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) return;
		
		Player player = mc.player;
		Level level = mc.level;
		var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
		long worldTime = level.getGameTime();
		
		GuiGraphics guiGraphics = event.getGuiGraphics();
		int screenWidth = mc.getWindow().getGuiScaledWidth();
		
		// Display Dragon Concentration timer
		if (data.isHoldingConcentration) {
			long timeLeft = Math.max(0, data.dragonConcentrationActivationTime - worldTime);
			float secondsLeft = timeLeft / 20.0f;
			String text = String.format("%.1f", secondsLeft);
			
			int x = screenWidth / 2 - 10;
			int y = 10;
			guiGraphics.drawString(mc.font, "Concentration: " + text + "s", x, y, 0x00FF00);
		}
		
		// Display Ember Dominion timer
		if (data.isHoldingEmberDominion) {
			long timeLeft = Math.max(0, data.emberDominionActivationTime - worldTime);
			float secondsLeft = timeLeft / 20.0f;
			String text = String.format("%.1f", secondsLeft);
			
			int x = screenWidth / 2 - 10;
			int y = 25;
			guiGraphics.drawString(mc.font, "Ember Dominion: " + text + "s", x, y, 0xFF6600);
		}
	}
}
