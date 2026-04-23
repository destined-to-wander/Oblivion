package destinedtowander.oblivion.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static destinedtowander.oblivion.common.compat.EnchancementCompat.isBrimstoneAcceptable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void brimstoneAcceptsConflagration(ItemStack stack, CallbackInfoReturnable<Boolean> CBInfo) {
        if (!FabricLoader.getInstance().isModLoaded("enchancement")) return;
        if (isBrimstoneAcceptable(((Enchantment)(Object)this), stack)) CBInfo.setReturnValue(true);
    }
}
