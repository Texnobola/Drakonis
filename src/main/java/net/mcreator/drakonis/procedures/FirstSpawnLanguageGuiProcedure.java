package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.client.gui.LanguageSelectScreen;

@EventBusSubscriber(value = Dist.CLIENT)
public class FirstSpawnLanguageGuiProcedure {
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && player.level().isClientSide) {
            var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
            if (!data.hasSelectedLanguage) {
                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().setScreen(new LanguageSelectScreen());
                });
            }
        }
    }
}
