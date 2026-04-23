package destinedtowander.oblivion.client.item;

import destinedtowander.oblivion.common.index.OblivionItems;
import destinedtowander.oblivion.common.items.ConflagrationItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;


public class OblivionPredicates {
    public static void register() {
        ModelPredicateProviderRegistry.register(OblivionItems.STARDUST_CONFLAGRATION, new Identifier("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) return 0.0F;
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });

        ModelPredicateProviderRegistry.register(OblivionItems.STARDUST_CONFLAGRATION, new Identifier("pulling"), (itemStack, clientWorld, livingEntity, seed) ->
            livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !ConflagrationItem.isCharged(itemStack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(OblivionItems.STARDUST_CONFLAGRATION, new Identifier("charged"), (stack, world, entity, seed) ->
            ConflagrationItem.isCharged(stack) ? 1.0F : 0.0F
        );
    }
}
