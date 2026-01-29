package net.mcreator.drakonis.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

public class OlovtoshiItem extends Item {
    public OlovtoshiItem() {
        super(new Item.Properties().durability(5000).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return false; // No enchanted glint
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            // Check if Power is Active
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (data.copyTag().getBoolean("Power1Active")) {
                applyTorchEffects(player);
            }
        }
    }

    // Shared method to apply effects
    public static void applyTorchEffects(Player player) {
        // REMOVED: Night Vision
        
        // ADDED: Glowing Effect (Visual Outline)
        // Note: This does not emit block light unless you have a Dynamic Lights mod installed.
        if (player.getEffect(MobEffects.GLOWING) == null) {
             player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false, false));
        }
    }
}