package com.jgxc_bek.create_gear;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ALL_Items {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateGear.MODID);
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
