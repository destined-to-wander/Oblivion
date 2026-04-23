package destinedtowander.oblivion.client.event;

import destinedtowander.oblivion.common.index.OblivionItems;
import destinedtowander.oblivion.mixin.client.InGameHudAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import static destinedtowander.oblivion.common.compat.EnchancementCompat.getBrimstoneLevel;

public class AstralBrimstoneRenderEvent implements HudRenderCallback {
    public static int forcedHeight = -1;
    public static int health = -1;

    public AstralBrimstoneRenderEvent() {
    }

    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (FabricLoader.getInstance().isModLoaded("enchancement") && minecraft.player.getActiveItem().isOf(OblivionItems.STARDUST_CONFLAGRATION) && getBrimstoneLevel(minecraft.player.getActiveItem()) > 0) {
            int scaledWidth = minecraft.getWindow().getScaledWidth();
            int scaledHeight = minecraft.getWindow().getScaledHeight();
            forcedHeight = scaledHeight / 2 + 6;
            MinecraftClient client = ((InGameHudAccessor)minecraft.inGameHud).getClient();

            client.getProfiler().push("chargeLevel");
            String string = health / 60 + (health % 60 < 10 ? ":0" : ":") + health % 60;
            int Xcoord = (scaledWidth - minecraft.inGameHud.getTextRenderer().getWidth(string)) / 2;
            drawContext.drawText(minecraft.inGameHud.getTextRenderer(), string, Xcoord + 1, forcedHeight, 0xFFFFFF, false);
            drawContext.drawText(minecraft.inGameHud.getTextRenderer(), string, Xcoord - 1, forcedHeight, 0xFFFFFF, false);
            drawContext.drawText(minecraft.inGameHud.getTextRenderer(), string, Xcoord, forcedHeight + 1, 0xFFFFFF, false);
            drawContext.drawText(minecraft.inGameHud.getTextRenderer(), string, Xcoord, forcedHeight - 1, 0xFFFFFF, false);
            drawContext.drawText(minecraft.inGameHud.getTextRenderer(), string, Xcoord, forcedHeight, 0x48157E, false);
            client.getProfiler().pop();

            forcedHeight = -1;
        } else {
            health = -1;
        }
    }
}
