package destinedtowander.oblivion.client.render.entities;

import destinedtowander.oblivion.client.OblivionClient;
import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;

import java.awt.*;
import java.util.List;

import static destinedtowander.oblivion.Oblivion.id;

@Environment(EnvType.CLIENT)
public class StarShardEntityRenderer extends EntityRenderer<StarShardProjectileEntity> {

    private static final RenderLayer TRAIL_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createCachedToken(id("textures/trail/light_trail.png")));

    public static final Identifier TEXTURE = id("textures/entity/projectiles/star_shard.png");
    private static final RenderLayer STAR_SHARD = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private static final float SINE_45_DEGREES = (float)Math.sin(Math.PI / 4);
    private final StarShardEntityModel model;

    public RenderLayer getTrailRenderType() {
        return TRAIL_TYPE;
    }

    public StarShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context); // , new StarShardEntityModel(context.getPart(OblivionClient.STAR_SHARD_LAYER)), 0.5f
        this.model = new StarShardEntityModel(context.getPart(OblivionClient.STAR_SHARD_LAYER));
    }

    @Override
    public Identifier getTexture(StarShardProjectileEntity entity) {
        return TEXTURE;
    }
    
    public void render(StarShardProjectileEntity starShardEntity, float entityYaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        float age = (starShardEntity.age + tickDelta) * 3.0F;
        int k = OverlayTexture.DEFAULT_UV;
        Color variantColor = Color.decode(starShardEntity.getVariant().color);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(STAR_SHARD);
        matrixStack.push();
        matrixStack.scale(0.5F,0.5F,0.5F);
        Quaternionf q = new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES);
        Quaternionf q2 = new Quaternionf().setAngleAxis((float) (2 * Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES);
        Quaternionf ageRot = RotationAxis.POSITIVE_Y.rotationDegrees(age);

        matrixStack.multiply(ageRot);
        matrixStack.multiply(q);
        matrixStack.multiply(q);
        matrixStack.multiply(ageRot);
        matrixStack.multiply(q);
        matrixStack.multiply(ageRot);

//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age));
//        matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age));
//        matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age));

        this.model.render(matrixStack, vertexConsumer, light, k, variantColor);
        matrixStack.pop();
        matrixStack.pop();

        super.render(starShardEntity, entityYaw, tickDelta, matrixStack, vertexConsumerProvider, light);
        
        if (!starShardEntity.isInvisible()) {
            matrixStack.push();
            List<TrailPoint> positions = starShardEntity.getPastPositions();
            VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(getTrailRenderType());

            float size = 0.2f;
            float alpha = 0.7f;

            float x = (float) MathHelper.lerp(tickDelta, starShardEntity.prevX, starShardEntity.getX());
            float y = (float) MathHelper.lerp(tickDelta, starShardEntity.prevY, starShardEntity.getY());
            float z = (float) MathHelper.lerp(tickDelta, starShardEntity.prevZ, starShardEntity.getZ());

            matrixStack.translate(-x, -y, -z);
            builder.setColor(variantColor)
                .setAlpha(alpha)
                .renderTrail(matrixStack,
                    positions,
                    f -> MathHelper.sqrt(f) * size,
                    f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (alpha * f) - 0.1f)))
                )
                .renderTrail(matrixStack,
                    positions,
                    f -> (MathHelper.sqrt(f) * size) / 1.5f,
                    f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (((alpha * f) / 1.5f) - 0.1f))))
                );

            matrixStack.pop();
        }
    }
}
