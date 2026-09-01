package com.jgxc_bek.create_gear.content.kinetics.base;

import com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.Simple_K_BlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.jgxc_bek.create_gear.ALL_PartialModels.*;

public class Helical_BlockEntity_Visual extends SingleAxisRotatingVisual<Simple_K_BlockEntity> {
    public Helical_BlockEntity_Visual(VisualizationContext context, Simple_K_BlockEntity blockEntity, float partialTick, Model model) {
        super(context, blockEntity, partialTick,model);
        rotatingModel.setRotationOffset(Helical_Offset(blockEntity));
        rotatingModel.setChanged();
    }
    @Override
    public void update(float pt){
        super.update(pt);
        rotatingModel.setRotationOffset(Helical_Offset(blockEntity));
        rotatingModel.setChanged();
    }
    public static float Helical_Offset(BlockEntity be){
        BlockPos pos=be.getBlockPos();
        int X =pos.getX();
        int Y =pos.getY();
        int Z =pos.getZ();
        if ((X+Y+Z)%2==0){
            return 22.5f;
        }else {
            return 0;
        }
    }
    public static SimpleBlockEntityVisualizer.Factory<Simple_K_BlockEntity> createOf() {
        return (context, blockEntity, partialTick) -> {
            boolean R= (blockEntity.getBlockPos().getX()+blockEntity.getBlockPos().getY()+blockEntity.getBlockPos().getZ())%2==0;
                return new Helical_BlockEntity_Visual(
                    context, blockEntity, partialTick,
                    Models.partial( R ?HELICAL_GEAR_R_MODEL :HELICAL_GEAR_MODEL)
                );
            };
    }
}
