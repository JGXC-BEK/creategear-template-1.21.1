package com.jgxc_bek.create_gear.content.kinetics.Simple_Relays;

import com.jgxc_bek.create_gear.infrastructure.Config.ALL_Configs;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.createmod.catnip.config.ConfigBase.ConfigInt;
import java.util.HashMap;
import java.util.Map;

public class Gears_Utils {
    private Gears_Utils(){}
    //RPM调用
    private static final Map<Class<? extends KineticBlock>, ConfigInt> Gears_RPM_Map=new HashMap<>();
    public static void register(){
        Gears_RPM_Map.put(Helical_Gear_Block.class,ALL_Configs.server().kinetics.maxRPM_Helical_Wood);
    }
    public static ConfigInt get_RPM(Class<?extends KineticBlock> block){
        return Gears_RPM_Map.get(block);
    }
    //Mod齿轮判断
    public static boolean is_Gears(Block block){
        return block instanceof I_Gears;
    }
    public static boolean is_Gears(BlockState blockState){
        return blockState.getBlock() instanceof I_Gears;
    }
    //RPM上限
    public static boolean tooFast(float new_neighbour_Speed,float oppositeSpeed ,Class<?extends Block> neighbour_block,Class<?extends Block> opposite_block){
        if (!KineticBlock.class.isAssignableFrom(neighbour_block)||!KineticBlock.class.isAssignableFrom(opposite_block)){
            return false;
        }
        ConfigInt opp= Gears_RPM_Map.get(opposite_block),
                nei=Gears_RPM_Map.get(neighbour_block);
        float oppo ,neig;
        oppo=(opp!=null) ?(float)opp.get() :AllConfigs.server().kinetics.maxRotationSpeed.get();
        neig=(nei!=null) ?(float)nei.get() :AllConfigs.server().kinetics.maxRotationSpeed.get();
        float opposite_max = oppo;
        float neighbour_max=neig;
        return oppositeSpeed>opposite_max||new_neighbour_Speed>neighbour_max;
    }
}
