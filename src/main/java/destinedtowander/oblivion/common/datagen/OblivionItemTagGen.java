package destinedtowander.oblivion.common.datagen;

import destinedtowander.oblivion.common.index.OblivionItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class OblivionItemTagGen extends FabricTagProvider.ItemTagProvider {
    public OblivionItemTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(OblivionItemTags.CONFLAGRATION_ACCEPTED)
            .add(Items.NETHER_STAR)
            .addOptionalTag(OblivionItemTags.CONFLAGRATION_DESTRUCTIVE);
        this.getOrCreateTagBuilder(OblivionItemTags.CONFLAGRATION_DESTRUCTIVE)
            .add(Items.END_CRYSTAL);
    }
}