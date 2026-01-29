package net.mcreator.drakonis.mixin;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;

import org.objectweb.asm.Opcodes;

import org.checkerframework.checker.units.qual.g;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.mcreator.drakonis.DrakonisModPlayerAnimationAPI;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	private String master = null;
	private Minecraft mc = Minecraft.getInstance();
	@Shadow
	@Final
	protected List<Object> layers;

	@Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;", opcode = Opcodes.GETFIELD))
	private List<Object> filterLayers(LivingEntityRenderer instance, LivingEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
		if (master == null) {
			if (!DrakonisModPlayerAnimationAPI.animations.isEmpty())
				master = "drakonis";
			else
				return layers;
		}
		if (!master.equals("drakonis")) {
			return layers;
		}
		if (entity instanceof Player player && mc.options.getCameraType().isFirstPerson()) {
			if (mc.player == player && mc.screen == null) {
				CompoundTag playerData = player.getPersistentData();
				if (playerData.getBoolean("FirstPersonAnimation")) {
					playerData.putInt("setNullRender", 4);
					return layers.stream().filter(layer -> layer instanceof PlayerItemInHandLayer).toList();
				} else if (playerData.contains("setNullRender")) {
					if (playerData.getInt("setNullRender") <= 0)
						playerData.remove("setNullRender");
					else {
						playerData.putInt("setNullRender", playerData.getInt("setNullRender") - 1);
						return List.of();
					}
				}
			}
		}
		return layers;
	}
}