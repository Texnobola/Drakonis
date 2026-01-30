package net.mcreator.drakonis.item;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.drakonis.network.DrakonisModVariables;

import java.util.List;

public class OlovtoshiItem extends Item implements ICurioItem {
	public OlovtoshiItem() {
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
			tooltip.add(Component.translatable("tooltip.drakonis.fire_stone.abilities").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			
			boolean emberActive = data.emberDominionActive;
			tooltip.add(Component.translatable("tooltip.drakonis.ember_dominion")
				.append(": ")
				.append(Component.translatable(emberActive ? "tooltip.drakonis.status.active" : "tooltip.drakonis.status.inactive")
					.withStyle(emberActive ? ChatFormatting.GREEN : ChatFormatting.RED)));
			
			boolean concentrationActive = worldTime < data.dragonConcentrationUntil;
			tooltip.add(Component.translatable("tooltip.drakonis.dragon_concentration")
				.append(": ")
				.append(Component.translatable(concentrationActive ? "tooltip.drakonis.status.active" : "tooltip.drakonis.status.inactive")
					.withStyle(concentrationActive ? ChatFormatting.GREEN : ChatFormatting.RED)));
			
			boolean immortal = worldTime < data.dragonImmortalityUntil;
			if (immortal) {
				tooltip.add(Component.translatable("tooltip.drakonis.immortality")
					.append(": ")
					.append(Component.translatable("tooltip.drakonis.status.active")
						.withStyle(ChatFormatting.LIGHT_PURPLE)));
			}
			
			tooltip.add(Component.literal(""));
			tooltip.add(Component.translatable("tooltip.drakonis.fire_stone.passive").withStyle(ChatFormatting.YELLOW));
		}
	}

	private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("drakonis", "olovtoshi_health");

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
		if (healthAttr != null && !healthAttr.hasModifier(HEALTH_MODIFIER_ID)) {
			healthAttr.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_ID, 20.0, AttributeModifier.Operation.ADD_VALUE));
			entity.setHealth(entity.getHealth() + 20.0F);
		}
	}

	@Override
	public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
		if (healthAttr != null && healthAttr.hasModifier(HEALTH_MODIFIER_ID)) {
			healthAttr.removeModifier(HEALTH_MODIFIER_ID);
			if (entity.getHealth() > entity.getMaxHealth()) {
				entity.setHealth(entity.getMaxHealth());
			}
		}
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));
	}
}