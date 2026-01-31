package net.mcreator.drakonis.item;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.drakonis.network.DrakonisModVariables;

import java.util.List;

public class MuztoshiItem extends Item implements ICurioItem {
	public MuztoshiItem() {
		super(new Item.Properties().durability(5000).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		var player = net.minecraft.client.Minecraft.getInstance().player;
		if (player != null) {
			var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
			long worldTime = player.level().getGameTime();
			
			tooltip.add(Component.literal(""));
			tooltip.add(Component.translatable("tooltip.drakonis.ice_stone.abilities").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
			
			boolean onCooldown = worldTime < data.snowGolemSummonCooldown;
			tooltip.add(Component.translatable("tooltip.drakonis.snow_golem_summon")
				.append(": ")
				.append(Component.translatable(onCooldown ? "tooltip.drakonis.status.cooldown" : "tooltip.drakonis.status.ready")
					.withStyle(onCooldown ? ChatFormatting.RED : ChatFormatting.GREEN)));
			
			tooltip.add(Component.literal(""));
			tooltip.add(Component.translatable("tooltip.drakonis.ice_stone.passive").withStyle(ChatFormatting.YELLOW));
		}
	}
}