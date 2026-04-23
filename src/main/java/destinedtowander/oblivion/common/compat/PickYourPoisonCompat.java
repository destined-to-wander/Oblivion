package destinedtowander.oblivion.common.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.pickyourpoison.common.PickYourPoison;

public class PickYourPoisonCompat {
    public static void addVulnerabilityEffect(LivingEntity entity, @Nullable Entity source){
        addVulnerabilityEffect(entity, source, 600);
    }

    public static void addVulnerabilityEffect(LivingEntity entity, @Nullable Entity source, int duration){
        if (source != null)
            entity.addStatusEffect(new StatusEffectInstance(PickYourPoison.VULNERABILITY, duration), source);
        else
            entity.addStatusEffect(new StatusEffectInstance(PickYourPoison.VULNERABILITY, duration));
    }
}
