package destinedtowander.oblivion;

import destinedtowander.oblivion.common.compat.EnchancementCompat;
import destinedtowander.oblivion.common.index.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oblivion implements ModInitializer {
	public static final String MOD_ID = "oblivion";
	public static final Logger LOGGER = LoggerFactory.getLogger("Oblivion");

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("enchancement")) {
			EnchancementCompat.init();
		}
		OblivionEffects.register();
		OblivionEnchantments.register();
		OblivionBlocks.register();
		OblivionItems.register();
		OblivionEntities.register();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}