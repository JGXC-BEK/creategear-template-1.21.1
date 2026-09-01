package com.jgxc_bek.create_gear.infrastructure.Config;

import net.createmod.catnip.config.ConfigBase;

public class C_Server extends ConfigBase {
    @Override
    public String getName() {
        return "sever";
    }

    public final C_Kinetics kinetics = nested(0, C_Kinetics::new, C_Server.Comments.kinetics);
    public static class Comments{
        static String kinetics = "Parameters and abilities of Create's kinetic mechanisms";

    }
}
