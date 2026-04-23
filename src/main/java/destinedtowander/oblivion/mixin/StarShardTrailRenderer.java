package destinedtowander.oblivion.mixin;

import destinedtowander.oblivion.Oblivion;
import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;

import java.awt.*;
import java.util.List;

import static destinedtowander.oblivion.Oblivion.id;

@Mixin(ProjectileEntityRenderer.class)
public abstract class StarShardTrailRenderer<T extends PersistentProjectileEntity> extends EntityRenderer<T> {
	private static final RenderLayer TRAIL_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createCachedToken(id("textures/trail/light_trail.png")));

	protected StarShardTrailRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	public RenderLayer getTrailRenderType() {
		return TRAIL_TYPE;
	}

	// spectral arrow trail
	@Inject(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
	public void render(T entity, float entityYaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
		// new render
		if (entity instanceof StarShardProjectileEntity starFragment && !starFragment.isInvisible()) {
			Oblivion.LOGGER.info("Working");
			// trail
			matrixStack.push();
			List<TrailPoint> positions = starFragment.getPastPositions();
			team.lodestar.lodestone.systems.rendering.VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(getTrailRenderType());

			float size = 0.15f;
			float alpha = 1f;

			float x = (float) MathHelper.lerp(tickDelta, starFragment.prevX, starFragment.getX());
			float y = (float) MathHelper.lerp(tickDelta, starFragment.prevY, starFragment.getY());
			float z = (float) MathHelper.lerp(tickDelta, starFragment.prevZ, starFragment.getZ());

			matrixStack.translate(-x, -y, -z);
			builder.setColor(Color.decode("0xFFFF77"))
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


			/* twinkles dk about this one
			if ((starFragment.getWorld().getRandom().nextInt(100) + 1) <= 5 && !MinecraftClient.getInstance().isPaused()) {
				float spreadDivider = 4f;
				WorldParticleBuilder.create(Effective.ALLAY_TWINKLE)
					.enableForcedSpawn()
					.setColorData(ColorParticleData.create(new Color(data.color), new Color(data.color)).build())
					.setTransparencyData(GenericParticleData.create(0.9f).build())
					.setScaleData(GenericParticleData.create(0.06f).build())
					.setLifetime(15)
					.setMotion(0, 0.05f, 0)
					.spawn(starFragment.getWorld(), starFragment.getClientCameraPosVec(MinecraftClient.getInstance().getTickDelta()).x + starFragment.getWorld().getRandom().nextGaussian() / spreadDivider, starFragment.getClientCameraPosVec(MinecraftClient.getInstance().getTickDelta()).y - 0.2f + starFragment.getWorld().getRandom().nextGaussian() / spreadDivider, starFragment.getClientCameraPosVec(MinecraftClient.getInstance().getTickDelta()).z + starFragment.getWorld().getRandom().nextGaussian() / spreadDivider);
			}

			//*/
		}
	}
}
