package net.mcreator.drakonis.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class IcyBootsModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.parse("drakonis:icy_boots"), "main");
    
    public final ModelPart left_boot;
    public final ModelPart right_boot;

    public IcyBootsModel(ModelPart root) {
        this.left_boot = root.getChild("left_boot");
        this.right_boot = root.getChild("right_boot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- LEFT BOOT (Heavy Monarch Design) ---
        PartDefinition left_boot = partdefinition.addOrReplaceChild("left_boot", CubeListBuilder.create()
            // Main Foot Base (Heavy Sole)
            .texOffs(0, 172).addBox(-2.5F, 10.0F, -2.5F, 5.0F, 2.5F, 5.0F, new CubeDeformation(0.1F))
            // Ankle Guard (Thick)
            .texOffs(0, 148).addBox(-2.1F, 8.0F, -2.1F, 4.2F, 2.0F, 4.2F, new CubeDeformation(0.1F)), 
            PartPose.offset(1.9F, 12.0F, 0.0F));

        // -- The "Monarch" Shin Plate (Front) --
        // A large protective plate rising up the leg
        left_boot.addOrReplaceChild("Left_Shin_Plate", CubeListBuilder.create()
            .texOffs(40, 160).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)),
            PartPose.offsetAndRotation(0.0F, 9.0F, -2.5F, -0.1F, 0.0F, 0.0F)); // Angled slightly back

        // -- Ice Spur (Heel) --
        // A sharp crystal jutting out the back
        left_boot.addOrReplaceChild("Left_Heel_Spur", CubeListBuilder.create()
            .texOffs(56, 168).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, 0.3F, 0.0F, 0.0F)); // Angled Up

        // -- "Valkyrie" Wing (Side Crystal) --
        // A shard on the outer ankle for style
        left_boot.addOrReplaceChild("Left_Ankle_Wing", CubeListBuilder.create()
            .texOffs(48, 160).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, 9.0F, 0.0F, 0.0F, 0.0F, -0.2F)); // Flared out


        // --- RIGHT BOOT (Mirrored) ---
        PartDefinition right_boot = partdefinition.addOrReplaceChild("right_boot", CubeListBuilder.create()
            // Main Foot Base
            .texOffs(20, 172).addBox(-2.5F, 10.0F, -2.5F, 5.0F, 2.5F, 5.0F, new CubeDeformation(0.1F))
            // Ankle Guard
            .texOffs(20, 148).addBox(-2.1F, 8.0F, -2.1F, 4.2F, 2.0F, 4.2F, new CubeDeformation(0.1F)), 
            PartPose.offset(-1.9F, 12.0F, 0.0F));

        // -- Shin Plate --
        right_boot.addOrReplaceChild("Right_Shin_Plate", CubeListBuilder.create()
            .texOffs(40, 160).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)),
            PartPose.offsetAndRotation(0.0F, 9.0F, -2.5F, -0.1F, 0.0F, 0.0F));

        // -- Ice Spur --
        right_boot.addOrReplaceChild("Right_Heel_Spur", CubeListBuilder.create()
            .texOffs(56, 168).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, 0.3F, 0.0F, 0.0F));

        // -- Ankle Wing --
        right_boot.addOrReplaceChild("Right_Ankle_Wing", CubeListBuilder.create()
            .texOffs(48, 160).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 9.0F, 0.0F, 0.0F, 0.0F, 0.2F)); // Flared out

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Standard armor animation
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        left_boot.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        right_boot.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}