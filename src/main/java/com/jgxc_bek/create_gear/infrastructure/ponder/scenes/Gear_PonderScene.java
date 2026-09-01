package com.jgxc_bek.create_gear.infrastructure.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class Gear_PonderScene {
    public static void Helical_gear_AsRelay(SceneBuilder builder, SceneBuildingUtil util) {
        //注释是ai写的，有删改，代码也有删改，仅供参考
        // 把 Ponder 传进来的底层 builder 包装成 Create 增强版
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        // 设置场景 id 和标题后备文本。
        // "sceneId" 决定语言键：create.ponder.sceneId.header / text_N
        scene.title("helical_gear", "Relaying rotational force using helical_gear");

        // 底板设置：前两个参数是底板在场景空间的偏移(x,z)，第三个是边长
        scene.configureBasePlate(0, 0, 7);

        // util.grid().at(x,y,z)：把"场景网格坐标"转成思考世界里的 BlockPos
        // (4,1,1) 处放的是转速表，用来给玩家直观显示当前 RPM
        BlockPos recourse = util.grid().at(1, 1, 1);
        Selection recourse_Select = util.select().position(recourse);   // 选中这一个方块

        // 让第 0 层（底板层）从下往上滑入出现
        scene.world().showSection(util.select().layer(0), Direction.UP);

        // 让转速表也从下往上出现
        scene.world().showSection(recourse_Select, Direction.UP);

        // 先把转速表所在网络的转速手动设为 0（思考世界不会真的跑动力传播，全靠手动设）
        // 这样开场时转速表显示 0 RPM
        //scene.world().setKineticSpeed(recourse_Select, 0);

        // 时间轴前进 5 tick（0.25秒）。Ponder 场景=指令队列+时间轴，idle 就是"让画面停一会儿"
        scene.idle(5);

        // 出现 (1,1,3)~(1,1,5) 一段竖直的轴列，从上向下滑入
        scene.world().showSection(util.select().fromTo(1, 1, 2, 5, 1, 2), Direction.DOWN);
        scene.idle(10);

        // 循环：沿 x=1→4 逐个出现 z=2 那一排的方块（每次间隔5tick，形成"依次摆放"的动画）

        scene.idle(5);
        // 第一段文字：显示 60 tick
        scene.overlay().showText(60)
                // 字面量只是后备显示；真正的文本走语言键 create.ponder.cogwheel.text_1
                //（按 showText 的调用顺序自动编号 text_1、text_2…）
                .text("斜齿轮轴互相垂直且齿接触时传递动力")
                // 指示线指向 (0,1,2) 方块的东面表面
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.EAST));

        scene.idle(60);   // 停留给玩家读字

        scene.world().showSection(util.select().position(5, 2, 2), Direction.DOWN);

        // 现在把转速表网络设为 64 RPM → 整个场景里的齿轮/轴开始旋转，表盘显示 64
        scene.world().setKineticSpeed(recourse_Select, 64);



//      // 在两根轴上画"旋转方向箭头"，直观展示两轴转向相反
//        scene.effects().rotationDirectionIndicator(util.grid().at(1, 1, 1));
//        scene.effects().rotationDirectionIndicator(util.grid().at(2, 1, 1));
//        scene.idle(20);

//        // 第二段文字：显示 100 tick
//        scene.overlay().showText(100)
//                .text("Neighbouring shafts connected like this will rotate in opposite directions")
//                .placeNearTarget()      // 文本框贴近指示点放置（而不是屏幕默认位置）
//                .attachKeyFrame()       // 在场景进度条上打一个"关键帧"标记
//                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.NORTH));
//        scene.idle(70);   // 收尾停留
    }
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(ResourceLocation.fromNamespaceAndPath("create_gear", "helical_gear"))
                .addStoryBoard("helical/basic", Gear_PonderScene::Helical_gear_AsRelay);
        //                       ↑ 决定加载哪个 nbt     ↑ 方法引用
    }
}
