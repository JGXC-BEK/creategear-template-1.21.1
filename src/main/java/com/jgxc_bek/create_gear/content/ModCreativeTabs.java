package com.jgxc_bek.create_gear.content;

import com.jgxc_bek.create_gear.CreateGear;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CreativeModTabs=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateGear.MODID);
//    public static final Supplier<CreativeModeTab> TestTab=
//            CreativeModTabs.register("TestTab",()-> CreativeModeTab.builder()
//                    .icon(()->new ItemStack(Test_Block.Test1_Block.get()))
//                    .title(Component.translatable("itemGroup.TestTab"))
//                    .displayItems((parameters,output)->{
//                        output.accept(Test_Block.Test1_Block);
//                    })
//                    .build());

    public static void register(IEventBus eventBus){
        CreativeModTabs.register(eventBus);
    }
}
