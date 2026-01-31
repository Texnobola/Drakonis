package net.mcreator.drakonis.init;

import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.CuriosCapability;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.drakonis.DrakonisMod;

@EventBusSubscriber(modid = DrakonisMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DrakonisModCuriosCompat {
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(CuriosCapability.ITEM, (stack, context) -> new ICurio() {
			@Override
			public ItemStack getStack() {
				return stack;
			}

			@Override
			public boolean makesPiglinsNeutral(SlotContext slotContext) {
				return true;
			}
		}, DrakonisModItems.OLOVTOSHI.get(), DrakonisModItems.ICY_GLOVES.get());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onLoadComplete(FMLLoadCompleteEvent event) {
		try {
			Class<?> curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
			java.lang.reflect.Method registerSlotMethod = curiosApi.getDeclaredMethod("registerSlot", String.class);
			registerSlotMethod.invoke(null, "hands");
		} catch (Exception e) {
			DrakonisMod.LOGGER.warn("Failed to register hands slot via reflection", e);
		}
	}
}