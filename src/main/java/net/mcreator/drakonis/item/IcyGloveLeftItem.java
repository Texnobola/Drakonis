package net.mcreator.drakonis.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.network.chat.Component;

import java.util.List;

public class IcyGloveLeftItem extends Item {
	public IcyGloveLeftItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.literal("§b§lICY GLOVE (LEFT)"));
		tooltip.add(Component.literal("§7Left hand ice magic"));
		tooltip.add(Component.literal("§9Hold in hand for ice powers"));
	}
}