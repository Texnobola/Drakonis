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
import net.mcreator.drakonis.DrakonisMod;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
public class IcyHelmetModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.parse("drakonis:icy_helmet"), "main");
    public final ModelPart helmet;

    public IcyHelmetModel(ModelPart root) {
        this.helmet = root.getChild("helmet");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // FIX: I added +9.0F to every Y coordinate in the addBox functions below.
        // Old Main Box: -17.0F -> New: -8.0F (This aligns exactly with the top of the player's head)
        
        PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create()
                // Top rim: -15 -> -6
                .texOffs(0, 0).addBox(-4.5F, -6.0F, -4.5F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                // Main Helmet Box: -17 -> -8 (Standard Head Position)
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                // Side Detail: -12 -> -3
                .texOffs(0, 0).addBox(-5.0F, -3.0F, -5.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                // Side Detail: -12 -> -3
                .texOffs(0, 0).addBox(4.0F, -3.0F, -5.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                // Top Spike Base: -21 -> -12
                .texOffs(0, 0).addBox(-2.0F, -12.0F, -4.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                // Top Spike Tip: -24 -> -15
                .texOffs(0, 0).addBox(-1.0F, -15.0F, -3.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                // Small Detail: -12 -> -3
                .texOffs(0, 0).addBox(-3.0F, -3.0F, -4.2F, 2.0F, 1.0F, 0.3F, new CubeDeformation(0.0F))
                // Small Detail: -12 -> -3
                .texOffs(0, 0).addBox(1.0F, -3.0F, -4.2F, 2.0F, 1.0F, 0.3F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // FIX: I also added +9.0F to the Y offset of every child part below.
        
        PartDefinition Back_Plate_Upper_r1 = helmet.addOrReplaceChild("Back_Plate_Upper_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -3.0F, -0.5F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -6.0F, 4.0F, 0.3927F, 0.0F, 0.0F)); // Old: -15

        PartDefinition Back_Plate_Lower_r1 = helmet.addOrReplaceChild("Back_Plate_Lower_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.3927F, 0.0F, 0.0F)); // Old: -9

        PartDefinition Horn_Tip_Right_r1 = helmet.addOrReplaceChild("Horn_Tip_Right_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.5F, -4.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(7.0F, -9.0F, -0.5F, 0.0F, 0.0F, -0.7854F)); // Old: -18

        PartDefinition Horn_Base_Right_r1 = helmet.addOrReplaceChild("Horn_Base_Right_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0F, -4.0F, -1.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, -5.0F, -1.0F, 0.0F, 0.0F, -0.3927F)); // Old: -14

        PartDefinition Horn_Tip_Left_r1 = helmet.addOrReplaceChild("Horn_Tip_Left_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -4.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.0F, -9.0F, -0.5F, 0.0F, 0.0F, 0.7854F)); // Old: -18

        PartDefinition Horn_Base_Left_r1 = helmet.addOrReplaceChild("Horn_Base_Left_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -4.0F, -1.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, -5.0F, -1.0F, 0.0F, 0.0F, 0.3927F)); // Old: -14

        PartDefinition Chin_Spike_r1 = helmet.addOrReplaceChild("Chin_Spike_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.3927F, 0.0F, 0.0F)); // Old: -9

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}