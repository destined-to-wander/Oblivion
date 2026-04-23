package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.effect.DissonantEffect;
import destinedtowander.oblivion.common.effect.ResonantEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static destinedtowander.oblivion.Oblivion.id;

public class OblivionEffects {
    public static final StatusEffect RESONANT = registerEffect("resonant", new ResonantEffect());
    public static final StatusEffect DISSONANT = registerEffect("dissonant", new DissonantEffect());

    public static void register(){}

    public static StatusEffect registerEffect(String id, StatusEffect entry){
        return Registry.register(Registries.STATUS_EFFECT, id(id), entry);
    }
}
