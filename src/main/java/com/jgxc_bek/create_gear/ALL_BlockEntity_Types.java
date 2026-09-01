package com.jgxc_bek.create_gear;

import com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.Simple_K_BlockEntity;
import com.jgxc_bek.create_gear.content.kinetics.base.Helical_BlockEntity_Visual;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ALL_BlockEntity_Types {
    public static final CreateRegistrate REGISTRATE=CreateGear.get_Gear_Registrate();

    public static final BlockEntityEntry<Simple_K_BlockEntity> ENCASED_HELICAL_GEAR = REGISTRATE
            .blockEntity("encased_helical_gear",Simple_K_BlockEntity::new)
            .visual(() -> Helical_BlockEntity_Visual.createOf(),false)
            .validBlocks(ALL_Blocks.Helical_Gear)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();
    public static void register(){

    }


}
