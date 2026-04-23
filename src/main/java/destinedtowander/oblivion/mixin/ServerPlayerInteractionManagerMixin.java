package destinedtowander.oblivion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import destinedtowander.oblivion.common.index.OblivionItems;
import destinedtowander.oblivion.common.items.ConflagrationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    public ServerPlayerInteractionManagerMixin() {
    }

    @ModifyExpressionValue(
        method = "interactItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"
        )
    )
    private boolean allowLoadingThroughCooldown(boolean value, ServerPlayerEntity player, World world, ItemStack stack) {
        return value && (!stack.isOf(OblivionItems.STARDUST_CONFLAGRATION) || ConflagrationItem.isCharged(stack));
    }
}
