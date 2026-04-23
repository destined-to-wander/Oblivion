package destinedtowander.oblivion.common.effect;

import destinedtowander.oblivion.common.index.OblivionDamageTypes;
import destinedtowander.oblivion.common.index.OblivionEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.explosion.Explosion;

import static destinedtowander.oblivion.common.index.OblivionDamageTypes.damageSource;

public class ResonantEffect extends StatusEffect {
    public ResonantEffect() {
        super(StatusEffectCategory.HARMFUL, 0xF5D15D);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Dissonant reaction
        StatusEffectInstance dissonantInstance = entity.getStatusEffect(OblivionEffects.DISSONANT);
        if (dissonantInstance != null){
            Explosion explosion = new Explosion(
                entity.getWorld(),
                null,
                damageSource(OblivionDamageTypes.DISCHARGE, entity.getWorld()),
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                1.0F,
                false,
                Explosion.DestructionType.KEEP);
            explosion.collectBlocksAndDamageEntities();
            explosion.affectWorld(true);

            entity.removeStatusEffect(OblivionEffects.DISSONANT);
            entity.removeStatusEffect(OblivionEffects.RESONANT);
        }

        if (entity.getFireTicks() > 10) return;
        if (entity.getFireTicks() <= 1) entity.setOnFireFor(5);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
    }


}
