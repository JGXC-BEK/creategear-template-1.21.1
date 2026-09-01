package com.jgxc_bek.create_gear.infrastructure.Config;

import net.createmod.catnip.config.ConfigBase;

public class C_WorldGen extends ConfigBase {
    public final ConfigBool disable = b(false, "disableWorldGen", C_WorldGen.Comments.disable);

    @Override
    public String getName() {
        return "worldgen";
    }

    private static class Comments {
        static String disable = "Prevents all worldgen added by Create from taking effect";
    }
}
