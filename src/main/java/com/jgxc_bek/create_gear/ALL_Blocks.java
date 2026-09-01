package com.jgxc_bek.create_gear;

import com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.Helical_Gear_Block;
import com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.Helical_Gear_Block_Item;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class ALL_Blocks {
    private static final CreateRegistrate REGISTRATE = CreateGear.get_Gear_Registrate();

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }


    /*
    .onRegister(block -> {
            // block参数就是已经注册完成的方块实例，不要写AllBlocks.BEVEL_GEAR.get()
            KineticBlockBehaviour.registerStressValue(block, 4.0, 0.0);
        })
        // 第二个onRegister：动力学自定义烘焙模型
        .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
        .simpleItem()
        .register();
     */
    public static final BlockEntry<Helical_Gear_Block> Helical_Gear =
            REGISTRATE.block("helical_gear", Helical_Gear_Block::get_new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.sound(SoundType.WOOD)
                    .mapColor(MapColor.DIRT))
            //.transform(CStress.setNoImpact())
                    .onRegister(block -> {
                        BlockStressValues.IMPACTS.register(block, ()->0.0D);
                        BlockStressValues.CAPACITIES.register(block, ()->0.0D);
                    })
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(Helical_Gear_Block_Item::new)
            .build()
            .register();
//    public static final DeferredRegister.Blocks BLOCKS =
//        DeferredRegister.createBlocks(CreateGear.MODID);
//
//    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block){
//        ALL_Items.ITEMS.register(name,()-> new BlockItem(block.get(),new Item.Properties()));
//    }
//    private static <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block){
//        DeferredBlock<T> blocks=BLOCKS.register(name,block);
//        registerBlockItems(name,blocks);
//        return blocks;
//    }
//    public static void register(IEventBus eventBus){
//        BLOCKS.register(eventBus);
//    }
//    public static final BlockEntry<Helical_Gear_Block> Helical_Gear =
//            registerBlocks("helical_gear", ()->new BlockEntry<Helical_Gear_Block>()
//



    public static void register(){

    }
}
