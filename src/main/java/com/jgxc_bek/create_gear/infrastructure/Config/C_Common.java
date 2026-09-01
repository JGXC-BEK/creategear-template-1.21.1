package com.jgxc_bek.create_gear.infrastructure.Config;

import net.createmod.catnip.config.ConfigBase;

public class C_Common extends ConfigBase {
    public final C_WorldGen worldGen = nested(0, C_WorldGen::new, C_Common.Comments.worldGen);

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String worldGen = "Modify Create's impact on your terrain";
    }
}
