package destinedtowander.oblivion.client.render.entities;

import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class StarShardEntityModel extends EntityModel<StarShardProjectileEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart main;
    private final ModelPart core;

    public StarShardEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        this.core = root.getChild("core");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartBuilder part = ModelPartBuilder.create().uv(0, 0)
            .cuboid(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F);
        ModelPartData core = modelPartData.addChild("core", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartBuilder corePart = ModelPartBuilder.create().uv(0, 18)
            .cuboid(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F);

        ModelPartData cube1 = main.addChild("cube1", part, ModelTransform.of(0,0,0, 0, 0, 1.5708F));
        ModelPartData cube2 = main.addChild("cube2", part, ModelTransform.of(0.0F, 0.0F, 0.0F,  -0.7854F, 0.7854F, 0.0F));
        ModelPartData cube3 = main.addChild("cube3", part, ModelTransform.of(0.0F, 0.0F, 0.0F,  -0.7854F, -0.7854F, 0.0F));


        ModelPartData overlay1 = core.addChild("overlay1", corePart, ModelTransform.of(0,0,0, 0, 0, 1.5708F));
        ModelPartData overlay2 = core.addChild("overlay2", corePart, ModelTransform.of(0.0F, 0.0F, 0.0F,  -0.7854F, 0.7854F, 0.0F));
        ModelPartData overlay3 = core.addChild("overlay3", corePart, ModelTransform.of(0.0F, 0.0F, 0.0F,  -0.7854F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 36, 36);
    }

    @Override
    public void setAngles(StarShardProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices,vertices,light,overlay,1.0F,1.0F,1.0F,1.0F);
        core.render(matrices,vertices,light,overlay,red,green,blue,alpha);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Color variantColor) {
        render(matrices,vertices,light,overlay,variantColor.getRed()/255.0F, variantColor.getGreen()/255.0F, variantColor.getBlue()/255.0F, 0.5F);
    }
}
