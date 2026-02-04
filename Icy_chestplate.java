// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class icy_chestplate_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "icy_chestplate_converted"), "main");
	private final ModelPart bone;

	public icy_chestplate_Converted(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(64, 80).addBox(-12.0F, -24.0F, 4.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(64, 124).addBox(-11.0F, -15.0F, 3.0F, 6.0F, 2.0F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(64, 140).addBox(-11.5F, -18.0F, 2.8F, 7.0F, 2.5F, 1.7F, new CubeDeformation(0.0F))
		.texOffs(144, 72).addBox(-12.0F, -25.5F, 7.0F, 8.0F, 2.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(144, 76).addBox(-10.0F, -23.0F, 8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(144, 76).addBox(-9.5F, -20.0F, 8.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(144, 76).addBox(-9.0F, -17.0F, 8.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		PartDefinition Pauldron_R_Spike_2_r1 = bone.addOrReplaceChild("Pauldron_R_Spike_2_r1", CubeListBuilder.create().texOffs(92, 168).addBox(0.0F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -24.0F, 6.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition Pauldron_R_Spike_1_r1 = bone.addOrReplaceChild("Pauldron_R_Spike_1_r1", CubeListBuilder.create().texOffs(76, 176).addBox(0.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -25.0F, 6.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition Pauldron_R_Base_r1 = bone.addOrReplaceChild("Pauldron_R_Base_r1", CubeListBuilder.create().texOffs(52, 184).addBox(0.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -22.0F, 6.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition Pauldron_L_Spike_2_r1 = bone.addOrReplaceChild("Pauldron_L_Spike_2_r1", CubeListBuilder.create().texOffs(40, 168).addBox(-3.0F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.0F, -24.0F, 6.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition Pauldron_L_Spike_1_r1 = bone.addOrReplaceChild("Pauldron_L_Spike_1_r1", CubeListBuilder.create().texOffs(24, 176).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -25.0F, 6.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition Pauldron_L_Base_r1 = bone.addOrReplaceChild("Pauldron_L_Base_r1", CubeListBuilder.create().texOffs(0, 184).addBox(-6.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -22.0F, 6.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition Crystal_Core_r1 = bone.addOrReplaceChild("Crystal_Core_r1", CubeListBuilder.create().texOffs(128, 120).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -20.0F, 3.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition Pec_Plate_Right_r1 = bone.addOrReplaceChild("Pec_Plate_Right_r1", CubeListBuilder.create().texOffs(108, 128).addBox(-4.0F, -4.5F, -1.0F, 4.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -18.5F, 3.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition Pec_Plate_Left_r1 = bone.addOrReplaceChild("Pec_Plate_Left_r1", CubeListBuilder.create().texOffs(92, 128).addBox(0.0F, -4.5F, -1.0F, 4.0F, 4.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.5F, -18.5F, 3.5F, 0.0F, 0.0F, -0.3927F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}