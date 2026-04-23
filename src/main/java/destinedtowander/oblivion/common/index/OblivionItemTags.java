package destinedtowander.oblivion.common.index;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionItemTags {
    public static final TagKey<Item> CONFLAGRATION_ACCEPTED = TagKey.of(RegistryKeys.ITEM, id("conflagration_accepted"));
    public static final TagKey<Item> CONFLAGRATION_DESTRUCTIVE = TagKey.of(RegistryKeys.ITEM, id("conflagration_destructive"));


}
