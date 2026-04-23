//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package destinedtowander.oblivion.common.compat;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static destinedtowander.oblivion.Oblivion.id;

public class AstralBrimstoneEntityRenderer extends ProjectileEntityRenderer<EnchancementCompat.AstralBrimstoneEntity> {
    private static final Identifier TEXTURE = id("textures/entity/brimstone.png");

    public AstralBrimstoneEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(EnchancementCompat.AstralBrimstoneEntity entity) {
        return TEXTURE;
    }

    public void render(EnchancementCompat.AstralBrimstoneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float scale = MathHelper.lerp((float)entity.ticksExisted / 10.0F, 1.0F, 0.0625F) * MathHelper.lerp((float)entity.getDamage() / 12.0F, 0.1F, 1.0F);
        float v = ((float)Math.floorMod(entity.getWorld().getTime(), 40) + tickDelta) / 4.0F;
        float u = v + -2.0F / scale;
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityAlpha(TEXTURE));
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) + 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)((entity.getWorld().getTime() + (long)entity.age) * 12L)));
        matrices.scale(scale, 1.0F, scale);
        MatrixStack.Entry entry = matrices.peek();

        for(int j = 0; (float)j < entity.maxY; ++j) {
            for(int i = 0; i < 360; i += 15) {
                drawPlane(entry.getPositionMatrix(), entry.getNormalMatrix(), vertices, u, v);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i));
            }

            matrices.translate(0.0F, 1.0F, 0.0F);
        }

        matrices.pop();
    }

    private static void drawPlane(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float u, float v) {
        drawVertex(positionMatrix, normalMatrix, vertices, 1, 0.0F, 1.0F, u);
        drawVertex(positionMatrix, normalMatrix, vertices, 0, 0.0F, 1.0F, v);
        drawVertex(positionMatrix, normalMatrix, vertices, 0, 0.25F, 0.0F, v);
        drawVertex(positionMatrix, normalMatrix, vertices, 1, 0.25F, 0.0F, u);
        drawVertex(positionMatrix, normalMatrix, vertices, 0, 0.25F, 0.0F, v);
        drawVertex(positionMatrix, normalMatrix, vertices, 0, 0.0F, 1.0F, v);
        drawVertex(positionMatrix, normalMatrix, vertices, 1, 0.0F, 1.0F, u);
        drawVertex(positionMatrix, normalMatrix, vertices, 1, 0.25F, 0.0F, u);
    }

    private static void drawVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, int y, float z, float u, float v) {
        vertices.vertex(positionMatrix, 0.0F, (float)y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }
}
