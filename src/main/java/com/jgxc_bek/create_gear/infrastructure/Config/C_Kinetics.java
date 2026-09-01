package com.jgxc_bek.create_gear.infrastructure.Config;

import com.simibubi.create.infrastructure.config.CStress;
import net.createmod.catnip.config.ConfigBase;

public class C_Kinetics extends ConfigBase {
    @Override
    public String getName() {
        return "kinetics";
    }

    public final ConfigInt maxRPM_Helical_Wood = i(64, 16, "maxRPM", C_Kinetics.Comments.rpm_helical_wood, C_Kinetics.Comments.maxRPM_helical_wood);
    public final CStress stressValues = nested(1, CStress::new, C_Kinetics.Comments.stress);

    private static class Comments {
        static String maxRPM_helical_wood = "Maximum allowed rotation speed for any Kinetic Block.for helical_wood_gear";
        static String rpm_helical_wood = "[in Revolutions per Minute .for helical_wood_gear]";
        static String stress = "Fine tune the kinetic stats of individual components";

    }
}
