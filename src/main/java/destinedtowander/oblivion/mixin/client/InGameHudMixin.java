package destinedtowander.oblivion.mixin.client;

import destinedtowander.oblivion.client.event.AstralBrimstoneRenderEvent;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({InGameHud.class})
public class InGameHudMixin {
    public InGameHudMixin() {
    }

    @ModifyArg(
        method = {"renderHealthBar"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V"
        ),
        index = 3
    )
    private int oblivion$brimstone(int value) {
        return AstralBrimstoneRenderEvent.forcedHeight >= 0 ? AstralBrimstoneRenderEvent.forcedHeight : value;
    }
}
