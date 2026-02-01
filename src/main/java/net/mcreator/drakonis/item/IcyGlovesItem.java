package net.mcreator.drakonis.item;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import java.util.List;

public class IcyGlovesItem extends Item implements ICurioItem {
	public IcyGlovesItem() {
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
		tooltip.add(Component.literal("§b§lICY GLOVES"));
		tooltip.add(Component.literal("§7Blockbench 3D Model"));
		tooltip.add(Component.literal("§9Equip in hands slot for ice magic"));
	}

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if (entity instanceof Player player) {
			player.displayClientMessage(Component.literal("§b✦ Icy Gloves equipped!"), true);
		}
	}

	@Override
	public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if (entity instanceof Player player) {
			player.displayClientMessage(Component.literal("§b✦ Icy Gloves removed"), true);
		}
	}
}
