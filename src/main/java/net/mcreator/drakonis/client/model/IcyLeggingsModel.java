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

public class IcyLeggingsModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.parse("drakonis:icy_leggings"), "main");

    // STRUCTURE: Split for animation support
    public final ModelPart body;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public IcyLeggingsModel(ModelPart root) {
        this.body = root.getChild("body");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- 1. BODY (The Royal Belt) ---
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                // Main Belt (Thick & Heavy)
                .texOffs(0, 24).addBox(-4.5F, 10.0F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.1F))

                // Central "Ice Core" Buckle (Protrudes forward)
                .texOffs(40, 4).addBox(-1.5F, 10.0F, -3.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // -- Right Hip Guard (Tasset) --
        // Angled plate hanging off the side
        body.addOrReplaceChild("Right_Hip_Guard", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-5.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.2F)); // Flares out slightly

        // -- Left Hip Guard (Tasset) --
        body.addOrReplaceChild("Left_Hip_Guard", CubeListBuilder.create()
                .texOffs(12, 28).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(5.0F, 11.0F, 0.0F, 0.0F, 0.0F, -0.2F)); // Flares out slightly

        // --- 2. LEFT LEG (The "Cold" Spike) ---
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                // Main Thigh Plate (Slightly bulkier than skin)
                .texOffs(0, 48).addBox(-2.1F, 0.0F, -2.1F, 4.2F, 6.0F, 4.2F, new CubeDeformation(0.0F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        // -- Upper Knee Crystal --
        // A sharp shard pointing UP from the knee
        left_leg.addOrReplaceChild("Left_Upper_Shard", CubeListBuilder.create()
                .texOffs(28, 84).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 5.0F, -3.0F, 0.4F, 0.4F, 0.0F));

        // -- Lower Knee Crystal --
        // A heavier block guarding the shin
        left_leg.addOrReplaceChild("Left_Lower_Guard", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 4.5F, -2.5F, -0.1F, 0.0F, 0.0F));

        // -- Side Ridge (Sharp fin on the outside of leg) --
        left_leg.addOrReplaceChild("Left_Side_Fin", CubeListBuilder.create()
                .texOffs(0, 100).addBox(0.0F, -2.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.5F, 2.0F, 0.0F));

        // --- 3. RIGHT LEG (Mirrored Monarch) ---
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                // Main Thigh Plate
                .texOffs(16, 48).addBox(-2.1F, 0.0F, -2.1F, 4.2F, 6.0F, 4.2F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        // -- Upper Knee Crystal --
        right_leg.addOrReplaceChild("Right_Upper_Shard", CubeListBuilder.create()
                .texOffs(28, 84).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 5.0F, -3.0F, 0.4F, -0.4F, 0.0F));

        // -- Lower Knee Crystal --
        right_leg.addOrReplaceChild("Right_Lower_Guard", CubeListBuilder.create()
                .texOffs(0, 80).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 4.5F, -2.5F, -0.1F, 0.0F, 0.0F));

        // -- Side Ridge --
        right_leg.addOrReplaceChild("Right_Side_Fin", CubeListBuilder.create()
                .texOffs(16, 100).addBox(-1.0F, -2.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.5F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        // Standard armor animation handling
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}