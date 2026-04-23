package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.enchantments.ConflagrationEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionEnchantments {
    public static final Enchantment DESOLATION = registerEnchantment("desolation",
        new ConflagrationEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND)
    );
    public static final Enchantment VITRIFICATION = registerEnchantment("vitrification",
        new ConflagrationEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND)
    );
    public static final Enchantment RICOCHET = registerEnchantment("ricochet",
        new ConflagrationEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND)
    );

    private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, id(name), enchantment);
    }

    public static void register() {}
}
