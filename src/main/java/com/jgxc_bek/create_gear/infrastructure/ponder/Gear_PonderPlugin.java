package com.jgxc_bek.create_gear.infrastructure.ponder;

import com.jgxc_bek.create_gear.CreateGear;
import com.jgxc_bek.create_gear.infrastructure.ponder.scenes.Gear_PonderScene;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class Gear_PonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreateGear.MODID;
    }
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper){
        Gear_PonderScene.register(helper);
    }
}
