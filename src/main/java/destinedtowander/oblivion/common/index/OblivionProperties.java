package destinedtowander.oblivion.common.index;

import destinedtowander.oblivion.common.block.AccumulateStage;
import net.minecraft.state.property.EnumProperty;

public class OblivionProperties {
    /**
     * A property that specifies the stage celestial accumulate is in
     */
    public static final EnumProperty<AccumulateStage> ACCUMULATION_LEVEL = EnumProperty.of("accumulation_level", AccumulateStage.class);
}
