package destinedtowander.oblivion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import destinedtowander.oblivion.common.index.OblivionItems;
import moriyashiine.enchancement.client.event.BrimstoneRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = BrimstoneRenderEvent.class, remap = false)
public class BrimstoneRenderEventMixin {
    @ModifyExpressionValue(
        method = "onHudRender",
        at = @At(
            value = "INVOKE",
            target = "Lmoriyashiine/enchancement/common/util/EnchancementUtil;hasEnchantment(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)Z"
        )
    )
    private boolean disableHeartsWithConflagration(boolean value) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return value;
        return value && !client.player.getActiveItem().isOf(OblivionItems.STARDUST_CONFLAGRATION);
    }

}
