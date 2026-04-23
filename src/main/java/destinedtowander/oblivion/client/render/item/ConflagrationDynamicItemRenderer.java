package destinedtowander.oblivion.client.render.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static destinedtowander.oblivion.Oblivion.id;

public class ConflagrationDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final List<ModelIdentifier> MODELS_TO_REGISTER = new ArrayList<>();

    public static final Pair<ModelIdentifier, ModelIdentifier> DEFAULT_MODEL_IDENTIFIER = registerVariantModelPair("stardust");

    private static @NotNull Pair<ModelIdentifier, ModelIdentifier> registerVariantModelPair(String name) {
        String s = name + (name.isEmpty() ? "" : "_") + "conflagration";

        ModelIdentifier inventoryModelIdentifier = new ModelIdentifier(id(s + "_inventory"), "inventory");
        ModelIdentifier inHandModelIdentifier = new ModelIdentifier(id(s + "_in_hand"), "inventory");

        MODELS_TO_REGISTER.add(inventoryModelIdentifier);
        MODELS_TO_REGISTER.add(inHandModelIdentifier);

        return new Pair<>(inventoryModelIdentifier, inHandModelIdentifier);
    }

    private static @NotNull Pair<ModelIdentifier, ModelIdentifier> getModelIdentifierModelIdentifierPair(ItemStack stack) {
        return DEFAULT_MODEL_IDENTIFIER;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean inHand = mode.isFirstPerson() || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformationMode.HEAD || mode == ModelTransformationMode.FIXED;
        boolean inInventory = mode == ModelTransformationMode.GUI;

        matrices.push();
        matrices.translate(.5, .5, .5);

        Pair<ModelIdentifier, ModelIdentifier> modelIdentifierPair = getModelIdentifierModelIdentifierPair(stack);

        BakedModel baseModel = MinecraftClient.getInstance()
            .getBakedModelManager()
            .getModel(!inHand ? modelIdentifierPair.getLeft() : modelIdentifierPair.getRight());
        BakedModel model = baseModel.getOverrides().apply(
            baseModel, stack,
            MinecraftClient.getInstance().world,
            MinecraftClient.getInstance().player,
            0
        );

        if (inInventory) DiffuseLighting.disableGuiDepthLighting();

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);

        if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) immediate.draw();
        if (inInventory) DiffuseLighting.enableGuiDepthLighting();

        matrices.pop();
    }
}
