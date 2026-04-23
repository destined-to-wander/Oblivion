package destinedtowander.oblivion.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import destinedtowander.oblivion.common.index.OblivionItems;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Unique
    private static boolean allowUsage = false;

    public ClientPlayerInteractionManagerMixin() {
    }

    @Inject(
        method = {"method_41929"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"
        )},
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void oblivion$brimstone(Hand hand, PlayerEntity player, MutableObject<?> mutableObject, int sequence, CallbackInfoReturnable<Packet<?>> cir, PlayerInteractItemC2SPacket packet, ItemStack stack) {
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem()) &&
            stack.isOf(OblivionItems.STARDUST_CONFLAGRATION) &&
            !CrossbowItem.isCharged(stack)) {
            allowUsage = true;
        }
    }

    @ModifyExpressionValue(
        method = {"method_41929"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"
        )}
    )
    private boolean oblivion$brimstone(boolean value) {
        if (allowUsage) {
            allowUsage = false;
            return false;
        } else {
            return value;
        }
    }
}
