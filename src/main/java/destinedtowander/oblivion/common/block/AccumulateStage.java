package destinedtowander.oblivion.common.block;

import net.minecraft.util.StringIdentifiable;

public enum AccumulateStage implements StringIdentifiable {
    STAGE_POS_3("stellar"),
    STAGE_POS_2("starcharged"),
    STAGE_POS_1("starsparked"),
    NEUTRAL("mundane");

    private final String name;

    private AccumulateStage(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}