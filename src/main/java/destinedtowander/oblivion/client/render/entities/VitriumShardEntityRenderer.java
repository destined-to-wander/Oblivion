package destinedtowander.oblivion.client.render.entities;

import destinedtowander.oblivion.common.entities.VitriumShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

import static destinedtowander.oblivion.Oblivion.id;

public class VitriumShardEntityRenderer extends ProjectileEntityRenderer<VitriumShardEntity> {
    public static final Identifier TEXTURE = id("textures/entity/projectiles/vitrium_shard.png");

    public VitriumShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(VitriumShardEntity vitriumShardEntity) {
        return TEXTURE;
    }
}
