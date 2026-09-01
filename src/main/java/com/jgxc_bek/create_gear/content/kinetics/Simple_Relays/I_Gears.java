package com.jgxc_bek.create_gear.content.kinetics.Simple_Relays;

import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface I_Gears extends ICogWheel {
    default boolean is_Helical_Gear(){
        return false;
    }
    static boolean is_Helical_Gear(Block block) {
        return block instanceof I_Gears && ((I_Gears) block).is_Helical_Gear();
    }
    static boolean is_Helical_Gear(BlockState state) {
        return is_Helical_Gear(state.getBlock());
    }
}
