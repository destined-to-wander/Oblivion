package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.block.AccumulateBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionBlocks {
    public static final List<Item> BLOCKITEMS = new ArrayList<>();

    public static final Block CELESTIAL_ACCUMULATE = registerBlock("celestial_accumulate",
        new AccumulateBlock(
            FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.CYAN).instrument(Instrument.CHIME)
        )
    );

    public static Block registerBlock(String name, Block block){
        Item blockItem = new BlockItem(block , new Item.Settings());
        BLOCKITEMS.add(blockItem);
        Registry.register(Registries.ITEM, id(name), blockItem);
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    public static void register(){}
}
