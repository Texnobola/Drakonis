/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.drakonis.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import net.mcreator.drakonis.item.*;
import net.mcreator.drakonis.DrakonisMod;

public class DrakonisModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(DrakonisMod.MODID);
	public static final DeferredItem<Item> OLOVTOSHI;
	public static final DeferredItem<Item> MUZTOSHI;
	public static final DeferredItem<Item> SUVTOSHI;
	public static final DeferredItem<Item> YERTOSHI;
	public static final DeferredItem<Item> VAQTTOSHI;
	public static final DeferredItem<Item> ENERGIYATOSHI;
	public static final DeferredItem<Item> DARKTOSHI;
	public static final DeferredItem<Item> LIGHTTOSHI;
	public static final DeferredItem<Item> ICY_GLOVE_RIGHT;
	public static final DeferredItem<Item> ICY_GLOVE_LEFT;
	public static final DeferredItem<Item> ICY_HELMET_HELMET;
	public static final DeferredItem<Item> ICY_CHESTPLATE_CHESTPLATE;
	public static final DeferredItem<Item> ICY_LEGGINGS_LEGGINGS;
	public static final DeferredItem<Item> ICY_BOOTS_BOOTS;
	static {
		OLOVTOSHI = REGISTRY.register("olovtoshi", OlovtoshiItem::new);
		MUZTOSHI = REGISTRY.register("muztoshi", MuztoshiItem::new);
		SUVTOSHI = REGISTRY.register("suvtoshi", SuvtoshiItem::new);
		YERTOSHI = REGISTRY.register("yertoshi", YertoshiItem::new);
		VAQTTOSHI = REGISTRY.register("vaqttoshi", VaqttoshiItem::new);
		ENERGIYATOSHI = REGISTRY.register("energiyatoshi", EnergiyatoshiItem::new);
		DARKTOSHI = REGISTRY.register("darktoshi", DarktoshiItem::new);
		LIGHTTOSHI = REGISTRY.register("lighttoshi", LighttoshiItem::new);
		ICY_GLOVE_RIGHT = REGISTRY.register("icy_glove_right", IcyGloveRightItem::new);
		ICY_GLOVE_LEFT = REGISTRY.register("icy_glove_left", IcyGloveLeftItem::new);
		ICY_HELMET_HELMET = REGISTRY.register("icy_helmet_helmet", IcyHelmetItem.Helmet::new);
		ICY_CHESTPLATE_CHESTPLATE = REGISTRY.register("icy_chestplate_chestplate", IcyChestplateItem.Chestplate::new);
		ICY_LEGGINGS_LEGGINGS = REGISTRY.register("icy_leggings_leggings", IcyLeggingsItem.Leggings::new);
		ICY_BOOTS_BOOTS = REGISTRY.register("icy_boots_boots", IcyBootsItem.Boots::new);
	}
	// Start of user code block custom items
	// End of user code block custom items
}