package destinedtowander.oblivion.common.enchantments;

import destinedtowander.oblivion.common.items.ConflagrationItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ConflagrationEnchantment extends Enchantment {
    public ConflagrationEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ConflagrationItem;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (other instanceof ConflagrationEnchantment enchantment)
            return this == enchantment;
        else
            return super.canAccept(other);
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public int getMinPower(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
