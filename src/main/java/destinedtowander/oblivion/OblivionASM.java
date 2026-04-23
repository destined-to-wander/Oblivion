package destinedtowander.oblivion;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class OblivionASM implements Runnable {
    public static final String CONFLAGRATION_TARGET = "STARDUST_CONFLAGRATION";

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String recipeBookTypeTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(recipeBookTypeTarget).addEnumSubclass(
            CONFLAGRATION_TARGET,
            "destinedtowander.oblivion.common.mm.ConflagrationTarget"
        ).build();
    }
}