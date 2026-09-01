package com.jgxc_bek.create_gear.mixins;
import com.jgxc_bek.create_gear.content.kinetics.Rotation_Propagator;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = RotationPropagator.class,remap = false)
public class Rotation_Propagator_Mixin {

    @Redirect(method = "handleAdded",at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;propagateNewSource(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V"),
            remap = false)
    private static void handle(KineticBlockEntity cTE){
        Rotation_Propagator.propagateNewSource(cTE);
    }
    @Redirect(method = "handleRemoved",at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;propagateMissingSource(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V"),
            remap = false)
    private static void handler(KineticBlockEntity nTE){
        Rotation_Propagator.propagateMissingSource(nTE);
    }
}
