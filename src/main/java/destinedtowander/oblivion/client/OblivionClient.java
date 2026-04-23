package destinedtowander.oblivion.client;

import destinedtowander.oblivion.client.event.AstralBrimstoneRenderEvent;
import destinedtowander.oblivion.client.item.OblivionPredicates;
import destinedtowander.oblivion.client.render.entities.StarShardEntityModel;
import destinedtowander.oblivion.client.render.entities.StarShardEntityRenderer;
import destinedtowander.oblivion.client.render.entities.VitriumShardEntityRenderer;
import destinedtowander.oblivion.client.render.item.ConflagrationDynamicItemRenderer;
import destinedtowander.oblivion.common.compat.EnchancementCompat;
import destinedtowander.oblivion.common.index.OblivionEntities;
import destinedtowander.oblivion.common.index.OblivionItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static destinedtowander.oblivion.Oblivion.id;

@Environment(EnvType.CLIENT)
public class OblivionClient implements ClientModInitializer {

    public static final EntityModelLayer STAR_SHARD_LAYER = new EntityModelLayer(id("star_shard"), "main");

    @Override
    public void onInitializeClient() {
        OblivionPredicates.register();

        // Built-in Item Renderers
        BuiltinItemRendererRegistry.INSTANCE.register(OblivionItems.STARDUST_CONFLAGRATION, new ConflagrationDynamicItemRenderer());

        // Force load the weapon models (otherwise since they're never called they wouldn't be loaded by default)
        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(ConflagrationDynamicItemRenderer.MODELS_TO_REGISTER));

        EntityRendererRegistry.register(OblivionEntities.STAR_SHARD, StarShardEntityRenderer::new);
        EntityRendererRegistry.register(OblivionEntities.VITRIUM_SHARD, VitriumShardEntityRenderer::new);
        if (FabricLoader.getInstance().isModLoaded("enchancement")) {
            EnchancementCompat.clientInit();
            HudRenderCallback.EVENT.register(new AstralBrimstoneRenderEvent());
        }

        EntityModelLayerRegistry.registerModelLayer(STAR_SHARD_LAYER, StarShardEntityModel::getTexturedModelData);

    }
}
