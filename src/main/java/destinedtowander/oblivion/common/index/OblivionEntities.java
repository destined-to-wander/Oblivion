package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import destinedtowander.oblivion.common.entities.VitriumShardEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionEntities {
    public static final EntityType<StarShardProjectileEntity> STAR_SHARD;
    public static final EntityType<VitriumShardEntity> VITRIUM_SHARD;

    static{

        STAR_SHARD = Registry.register(
            Registries.ENTITY_TYPE,
            id("star_shard"),
            FabricEntityTypeBuilder.<StarShardProjectileEntity>create(SpawnGroup.MISC, StarShardProjectileEntity::new)
                .dimensions(EntityDimensions.changing(0.5F, 0.5F))
                .trackRangeBlocks(64)
                .trackedUpdateRate(1)
                .forceTrackedVelocityUpdates(true)
                .build()
        );

        VITRIUM_SHARD = Registry.register(
            Registries.ENTITY_TYPE,
            id("vitrium_shard"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, VitriumShardEntity::new)
                .dimensions(EntityDimensions.changing(0.5F, 0.5F))
                .trackRangeBlocks(64)
                .trackedUpdateRate(1)
                .forceTrackedVelocityUpdates(true)
                .build()
        );
    }

    public static void register(){}
}
