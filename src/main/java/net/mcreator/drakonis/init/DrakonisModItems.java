/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.drakonis.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import net.mcreator.drakonis.item.OlovtoshiItem;
import net.mcreator.drakonis.DrakonisMod;

public class DrakonisModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(DrakonisMod.MODID);
	public static final DeferredItem<Item> OLOVTOSHI;
	static {
		OLOVTOSHI = REGISTRY.register("olovtoshi", OlovtoshiItem::new);
	}
	// Start of user code block custom items
	// End of user code block custom items
}