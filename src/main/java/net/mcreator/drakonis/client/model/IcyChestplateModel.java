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

public class IcyChestplateModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.parse("drakonis:icy_chestplate"), "main");
    public final ModelPart bone;

    public IcyChestplateModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // FIX 1: Reset the Main Bone Pivot to 0,0,0. We will fix the boxes instead.
        // This ensures the renderer cannot "reset" our fix.
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
            // Body Main Box: Old (-12, -12, 4) -> New (-4, 0, -2) [Shift: X+8, Y+12, Z-6]
            .texOffs(64, 80).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            // Detail 1: Old (-11, -3, 3) -> New (-3, 9, -3)
            .texOffs(64, 124).addBox(-3.0F, 9.0F, -3.0F, 6.0F, 2.0F, 1.5F, new CubeDeformation(0.0F))
            // Detail 2: Old (-11.5, -6, 2.8) -> New (-3.5, 6, -3.2)
            .texOffs(64, 140).addBox(-3.5F, 6.0F, -3.2F, 7.0F, 2.5F, 1.7F, new CubeDeformation(0.0F))
            // Back Detail 1: Old (-12, -13.5, 7) -> New (-4, -1.5, 1)
            .texOffs(144, 72).addBox(-4.0F, -1.5F, 1.0F, 8.0F, 2.5F, 2.0F, new CubeDeformation(0.0F))
            // Back Detail 2: Old (-10, -11, 8.5) -> New (-2, 1, 2.5)
            .texOffs(144, 76).addBox(-2.0F, 1.0F, 2.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            // Back Detail 3: Old (-9.5, -8, 8.5) -> New (-1.5, 4, 2.5)
            .texOffs(144, 76).addBox(-1.5F, 4.0F, 2.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            // Back Detail 4: Old (-9, -5, 8.5) -> New (-1, 7, 2.5)
            .texOffs(144, 76).addBox(-1.0F, 7.0F, 2.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), 
            PartPose.offset(0.0F, 0.0F, 0.0F));

        // FIX 2: Fixed Child Part Offsets.
        // We calculate the new offsets by taking the old ones and applying (X+8, Y+37, Z-6).
        // This snaps the floating parts down to the shoulders (Y=0) and centers them.

        // Right Pauldron Spike 2: Old (0, -37, 6) -> New (8, 0, 0)
        PartDefinition Pauldron_R_Spike_2_r1 = bone.addOrReplaceChild("Pauldron_R_Spike_2_r1", 
            CubeListBuilder.create().texOffs(92, 168).addBox(0.0F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        // Right Pauldron Spike 1: Old (-2, -38, 6) -> New (6, -1, 0)
        PartDefinition Pauldron_R_Spike_1_r1 = bone.addOrReplaceChild("Pauldron_R_Spike_1_r1", 
            CubeListBuilder.create().texOffs(76, 176).addBox(0.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(6.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        // Right Pauldron Base: Old (-4, -35, 6) -> New (4, 2, 0)
        PartDefinition Pauldron_R_Base_r1 = bone.addOrReplaceChild("Pauldron_R_Base_r1", 
            CubeListBuilder.create().texOffs(52, 184).addBox(0.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        // Left Pauldron Spike 2: Old (-16, -37, 6) -> New (-8, 0, 0)
        PartDefinition Pauldron_L_Spike_2_r1 = bone.addOrReplaceChild("Pauldron_L_Spike_2_r1", 
            CubeListBuilder.create().texOffs(40, 168).addBox(-3.0F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        // Left Pauldron Spike 1: Old (-14, -38, 6) -> New (-6, -1, 0)
        PartDefinition Pauldron_L_Spike_1_r1 = bone.addOrReplaceChild("Pauldron_L_Spike_1_r1", 
            CubeListBuilder.create().texOffs(24, 176).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(-6.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        // Left Pauldron Base: Old (-12, -35, 6) -> New (-4, 2, 0)
        PartDefinition Pauldron_L_Base_r1 = bone.addOrReplaceChild("Pauldron_L_Base_r1", 
            CubeListBuilder.create().texOffs(0, 184).addBox(-6.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        // Crystal Core: Old (-8, -33, 3) -> New (0, 4, -3)
        PartDefinition Crystal_Core_r1 = bone.addOrReplaceChild("Crystal_Core_r1", 
            CubeListBuilder.create().texOffs(128, 120).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(0.0F, 4.0F, -3.0F, 0.0F, 0.0F, 0.7854F));

        // Pec Plate Right: Old (-3.5, -31.5, 3.5) -> New (4.5, 5.5, -2.5)
        PartDefinition Pec_Plate_Right_r1 = bone.addOrReplaceChild("Pec_Plate_Right_r1", 
            CubeListBuilder.create().texOffs(108, 128).addBox(-4.0F, -4.5F, -1.0F, 4.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(4.5F, 5.5F, -2.5F, 0.0F, 0.0F, 0.3927F));

        // Pec Plate Left: Old (-12.5, -31.5, 3.5) -> New (-4.5, 5.5, -2.5)
        PartDefinition Pec_Plate_Left_r1 = bone.addOrReplaceChild("Pec_Plate_Left_r1", 
            CubeListBuilder.create().texOffs(92, 128).addBox(0.0F, -4.5F, -1.0F, 4.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offsetAndRotation(-4.5F, 5.5F, -2.5F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}