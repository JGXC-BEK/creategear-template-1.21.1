package com.jgxc_bek.create_gear.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public class CreateGear_Registrate extends AbstractRegistrate<CreateGear_Registrate> {
    /**
     * Construct a new Registrate for the given mod ID.
     *
     * @param modid The mod ID for which objects will be registered
     */
    protected CreateGear_Registrate(String modid) {
        super(modid);
    }
    private static final Map<RegistryEntry<?, ?>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TAB_LOOKUP = Collections.synchronizedMap(new IdentityHashMap<>());

}
