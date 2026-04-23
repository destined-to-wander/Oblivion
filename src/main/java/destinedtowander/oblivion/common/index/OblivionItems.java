package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.items.ConflagrationItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static destinedtowander.oblivion.Oblivion.id;
import static destinedtowander.oblivion.common.index.OblivionBlocks.BLOCKITEMS;

public class OblivionItems {
    public static final List<Item> ITEMS = new ArrayList<>();
    public static Item STARDUST;
    public static ConflagrationItem STARDUST_CONFLAGRATION;

    public static final ItemGroup OBLIVION;

    static {
        OBLIVION = Registry.register(Registries.ITEM_GROUP,
            id("oblivion"),
            FabricItemGroup.builder()
                .displayName(Text.translatable("itemgroup.oblivion"))
                .icon(() -> new ItemStack(STARDUST))
                .entries((displayContext, entries) -> {
                    entries.addAll(ITEMS.stream().map(Item::getDefaultStack).toList());
                    entries.addAll(BLOCKITEMS.stream().map(Item::getDefaultStack).toList());
                }).build());

        STARDUST = registerItem("stardust",new Item(new Item.Settings().fireproof()));
        STARDUST_CONFLAGRATION = registerItem("stardust_conflagration",new ConflagrationItem(
            new Item.Settings().fireproof().maxCount(1)
        ));
    }

    public static <T extends Item> T registerItem(String name, T item) {
        Registry.register(Registries.ITEM, id(name), item);
        ITEMS.add(item);
        return item;
    }

    public static void register(){}
}

