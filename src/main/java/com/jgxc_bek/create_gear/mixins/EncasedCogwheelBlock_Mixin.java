//package com.fengbeek.create_gear.mixins;
//
//
//import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
//import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//
//@Mixin(EncasedCogwheelBlock.class)
//public class EncasedCogwheelBlock_Mixin {
//    @Shadow
//    @Final
//    protected boolean isLarge;
//
//    @Inject(method = "getBlockEntityType",at = @At("HEAD"),cancellable = true,remap = false)
//    public BlockEntityType<? extends SimpleKineticBlockEntity> get_BE_Type(){
//        if
//    }
//}
