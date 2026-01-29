/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.drakonis.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.drakonis.DrakonisMod;

public class DrakonisModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DrakonisMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DRAKONIS = REGISTRY.register("drakonis",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.drakonis.drakonis")).icon(() -> new ItemStack(Blocks.AMETHYST_CLUSTER)).displayItems((parameters, tabData) -> {
				tabData.accept(DrakonisModItems.OLOVTOSHI.get());
			}).withSearchBar().build());
}