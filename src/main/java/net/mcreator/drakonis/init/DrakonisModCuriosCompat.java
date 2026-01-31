package net.mcreator.drakonis.init;

import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.CuriosCapability;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.world.item.ItemStack;

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
}