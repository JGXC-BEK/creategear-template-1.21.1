//package com.fengbeek.create_gear.mixins;
//
//import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
//import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
//import com.simibubi.create.content.kinetics.RotationPropagator;
//import com.simibubi.create.content.kinetics.base.IRotate;
//import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
//import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
//import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
//import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.level.block.state.BlockState;
//import net.neoforged.bus.api.ICancellableEvent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
//
///*
//    【已废弃】
//    原因：注入处被我方后续代码覆盖了
// */
//import java.util.List;
//@Mixin(RotationPropagator.class)
//public class Large_cogwheel_mixin {
//
//    @Inject(method = "isConnected", at = @At("HEAD"), cancellable = true, remap = false)
//    private static void Large_Gear(KineticBlockEntity from, KineticBlockEntity to,CallbackInfoReturnable cir) {
//        final BlockState SF=from.getBlockState();
//        final BlockState ST=to.getBlockState();
//        if(ICogWheel.isLargeCog(SF)&&ICogWheel.isLargeCog(ST)){
//            BlockPos pos=from.getBlockPos().subtract(to.getBlockPos());
//            if(pos.distSqr(BlockPos.ZERO)==2){
//                cir.setReturnValue(false);
//            }
//        }
//        return;
//    }
//}
//
