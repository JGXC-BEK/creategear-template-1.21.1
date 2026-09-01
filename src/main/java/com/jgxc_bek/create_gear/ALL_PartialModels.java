package com.jgxc_bek.create_gear;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class ALL_PartialModels {
    public static final PartialModel
        HELICAL_GEAR_MODEL=GearMod_block("helical_gear"),
        HELICAL_GEAR_R_MODEL=GearMod_block("helical_gear_r")





    ;
    private static PartialModel GearMod_block(String path){
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath("create_gear","block/"+path));
    }
    public static void init() {
        // init static fields
    }
}
