package destinedtowander.oblivion.common.effect;

import destinedtowander.oblivion.common.index.OblivionDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import static destinedtowander.oblivion.common.index.OblivionDamageTypes.damageSource;

public class DissonantEffect extends StatusEffect {
    public DissonantEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9E4DDB);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getFireTicks() > 10 + (40 * amplifier)) return;
        entity.damage(damageSource(OblivionDamageTypes.DISSONANT, entity.getWorld()), 0.4F);
        if (entity.getFireTicks() <= 1) entity.setOnFireFor((int) (3 + Math.random() * (8 + 2 * amplifier)));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
    }
}
