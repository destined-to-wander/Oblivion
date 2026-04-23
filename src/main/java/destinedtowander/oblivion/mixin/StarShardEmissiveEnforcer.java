package destinedtowander.oblivion.mixin;

import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class StarShardEmissiveEnforcer<T extends Entity> {
	@Inject(method = "getBlockLight", at = @At("RETURN"), cancellable = true)
	protected void getBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (entity instanceof StarShardProjectileEntity) {
			cir.setReturnValue(15);
		}
	}
}
