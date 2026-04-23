package destinedtowander.oblivion.common.datagen;

import destinedtowander.oblivion.common.index.OblivionDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class OblivionDamageSourceGen extends FabricTagProvider<DamageType> {
    public OblivionDamageSourceGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
            .addOptional(OblivionDamageTypes.VITRIFICATION);
        this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN)
            .addOptional(OblivionDamageTypes.DISSONANT);
    }
}
