package net.mcreator.drakonis.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.Minecraft;
import net.minecraft.Util;

import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;
import net.mcreator.drakonis.client.model.IcyChestplateModel;

import java.util.Map;
import java.util.List;
import java.util.EnumMap;
import java.util.Collections;

@EventBusSubscriber
public abstract class IcyChestplateItem extends ArmorItem {
	public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

	@SubscribeEvent
	public static void registerArmorMaterial(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
			ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.BOOTS, 10);
			map.put(ArmorItem.Type.LEGGINGS, 10);
			map.put(ArmorItem.Type.CHESTPLATE, 10);
			map.put(ArmorItem.Type.HELMET, 10);
				map.put(ArmorItem.Type.BODY, 6);
			}), 100, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), 
			() -> Ingredient.of(new ItemStack(Blocks.BLUE_ICE)), 
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("drakonis", "icy_armors_texture"))), 
			10f, 0.5f);
			registerHelper.register(ResourceLocation.fromNamespaceAndPath("drakonis", "icy_chestplate"), armorMaterial);
			ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
		});
	}

	@SubscribeEvent
	public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private HumanoidModel armorModel = null;

			@Override
			@OnlyIn(Dist.CLIENT)
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				if (armorModel == null) {
					IcyChestplateModel chestplateModel = new IcyChestplateModel(Minecraft.getInstance().getEntityModels().bakeLayer(IcyChestplateModel.LAYER_LOCATION));
					armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"body", chestplateModel.bone, 
									"right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), 
									"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), 
									"right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), 
									"left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				}
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, DrakonisModItems.ICY_CHESTPLATE_CHESTPLATE.get());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(IcyChestplateModel.LAYER_LOCATION, IcyChestplateModel::createBodyLayer);
	}

	public IcyChestplateItem(ArmorItem.Type type, Item.Properties properties) {
		super(ARMOR_MATERIAL, type, properties);
	}

	public static class Chestplate extends IcyChestplateItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15)));
		}

		@Override
		public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
			return ResourceLocation.fromNamespaceAndPath("drakonis", "textures/models/armor/icy_armors_texture.png");
		}
	}
}
