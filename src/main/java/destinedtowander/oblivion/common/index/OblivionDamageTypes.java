package destinedtowander.oblivion.common.index;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionDamageTypes {
    public static final RegistryKey<DamageType> RESONANT;
    public static final RegistryKey<DamageType> DISSONANT;
    public static final RegistryKey<DamageType> DISCHARGE;
    public static final RegistryKey<DamageType> VITRIFICATION;
    public static final RegistryKey<DamageType> SOUL_SHATTERED;


    public static DamageSource damageSource(RegistryKey<DamageType> key, World world) {
        return damageSource(key, world, null, null);
    }

    public static DamageSource damageSource(RegistryKey<DamageType> key, Entity attacker) {
        return damageSource(key, attacker.getWorld(), attacker, attacker);
    }

    public static DamageSource damageSource(RegistryKey<DamageType> key, PersistentProjectileEntity projectile, @Nullable Entity attacker) {
        return damageSource(key, projectile.getWorld(), projectile, attacker);
    }

    public static DamageSource damageSource(RegistryKey<DamageType> key, World world, @Nullable Entity source, @Nullable Entity attacker) {
        return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(key).map((type) -> new DamageSource(type, source, attacker)).orElseThrow();
    }

    static {
        RESONANT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id( "resonant"));
        DISSONANT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id( "dissonant"));
        DISCHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id( "discharge"));
        VITRIFICATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("vitrification"));
        SOUL_SHATTERED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id( "soul_shattered"));
    }
}
