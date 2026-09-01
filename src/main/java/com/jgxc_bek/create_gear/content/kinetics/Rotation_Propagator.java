package com.jgxc_bek.create_gear.content.kinetics;

import com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.I_Gears;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

import static com.jgxc_bek.create_gear.content.kinetics.Simple_Relays.Gears_Utils.tooFast;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class Rotation_Propagator extends RotationPropagator {
    private static final int MAX_FLICKER_SCORE = 128;
//    public static void handleAdded(Level worldIn, BlockPos pos, KineticBlockEntity addedTE) {
//        if (worldIn.isClientSide)
//            return;
//        if (!worldIn.isLoaded(pos))
//            return;
//        propagateNewSource(addedTE);
//    }
    public static void propagateNewSource(KineticBlockEntity currentTE) {
        BlockPos pos = currentTE.getBlockPos();
        Level world = currentTE.getLevel();

        Block this_block=currentTE.getBlockState().getBlock();
        //循环邻居
        //改：邻居逻辑
        for (KineticBlockEntity neighbourTE : getConnectedNeighbours(currentTE)) {
            Block neighbour_block=neighbourTE.getBlockState().getBlock();
            //初速
            float speedOfCurrent = currentTE.getTheoreticalSpeed();
            float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
            //双向结果速度
            //需改：齿轮传动逻辑
            float newSpeed = getConveyedSpeed(currentTE, neighbourTE);
            float oppositeSpeed = getConveyedSpeed(neighbourTE, currentTE);

            if (newSpeed == 0 && oppositeSpeed == 0)
                continue;

            //True：邻居结果转速与初速度相反且不为0
            boolean incompatible =
                    Math.signum(newSpeed) != Math.signum(speedOfNeighbour) && (newSpeed != 0 && speedOfNeighbour != 0);
            //速度上限
            boolean tooFast=tooFast(newSpeed,oppositeSpeed,neighbour_block.getClass(),this_block.getClass());
            //原赋值
//            boolean tooFast = Math.abs(newSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get()
//                    || Math.abs(oppositeSpeed) > AllConfigs.server().kinetics.maxRotationSpeed.get();
            // Check for both the new speed and the opposite speed, just in case
            //速度变化是否过于频繁
            boolean speedChangedTooOften = currentTE.getFlickerScore() > MAX_FLICKER_SCORE;
            //超速/变化过频，销毁
            if (tooFast || speedChangedTooOften) {
                world.destroyBlock(pos, true);
                return;
            }

            // Opposite directions
            //方向相反，销毁
            if (incompatible) {
                world.destroyBlock(pos, true);
                return;

                // Same direction: overpower the slower speed
            } else {

                // Neighbour faster, overpower the incoming tree
                //旁边的块，就让他覆盖自己
                if (Math.abs(oppositeSpeed) > Math.abs(speedOfCurrent)) {
                    float prevSpeed = currentTE.getSpeed();
                    currentTE.setSource(neighbourTE.getBlockPos());
                    currentTE.setSpeed(getConveyedSpeed(neighbourTE, currentTE));
                    currentTE.onSpeedChanged(prevSpeed);
                    currentTE.sendData();

                    Rotation_Propagator.propagateNewSource(currentTE);
                    return;
                }

                // Current faster, overpower the neighbours' tree
                //自己快，就覆盖旁边的
                if (Math.abs(newSpeed) >= Math.abs(speedOfNeighbour)) {

                    // Do not overpower you own network -> cycle
                    //不覆盖同一个动力网络的方块（否则会循环
                    if (!currentTE.hasNetwork() || currentTE.network.equals(neighbourTE.network)) {
                        float epsilon = Math.abs(speedOfNeighbour) / 256f / 256f;
                        if (Math.abs(newSpeed) > Math.abs(speedOfNeighbour) + epsilon)
                            world.destroyBlock(pos, true);
                        continue;
                    }

                    //如果旁边的动力源是自己，给它动力源删了，->重加  （起来重睡！（bushi
                            //可能是因为旧的自己和现在的自己速度和方向不一样，应该是这样
                    if (currentTE.hasSource() && currentTE.source.equals(neighbourTE.getBlockPos()))
                        currentTE.removeSource();
                    //设置旁边的动力源为自己
                    float prevSpeed = neighbourTE.getSpeed();
                    neighbourTE.setSource(currentTE.getBlockPos());
                    //设置速度
                    neighbourTE.setSpeed(getConveyedSpeed(currentTE, neighbourTE));
                    //？ 如果邻居出口是可变速的，设置邻居的变速根据为入口速度？
                    //不清楚
                    neighbourTE.onSpeedChanged(prevSpeed);
                    //传入客户端
                    neighbourTE.sendData();
                    //从邻居开始传递动力
                    Rotation_Propagator.propagateNewSource(neighbourTE);
                    continue;
                }
            }

            if (Math.abs(neighbourTE.getTheoreticalSpeed() - newSpeed) <= 1e-4f)
                continue;
            //这两个是打底吗？
            float prevSpeed = neighbourTE.getSpeed();
            neighbourTE.setSpeed(newSpeed);
            neighbourTE.setSource(currentTE.getBlockPos());
            neighbourTE.onSpeedChanged(prevSpeed);
            neighbourTE.sendData();
            Rotation_Propagator.propagateNewSource(neighbourTE);

        }
    }

    //
     //证明连接的关键方法
    private static List<KineticBlockEntity> getConnectedNeighbours(KineticBlockEntity be) {
        List<KineticBlockEntity> neighbours = new LinkedList<>();
        for (BlockPos neighbourPos : getPotentialNeighbourLocations(be)) {
            final KineticBlockEntity neighbourBE = findConnectedNeighbour(be, neighbourPos);
            if (neighbourBE == null)
                continue;

            neighbours.add(neighbourBE);
        }
        return neighbours;
    }
    //返回候选坐标组
    private static List<BlockPos> getPotentialNeighbourLocations(KineticBlockEntity be) {
        List<BlockPos> neighbours = new LinkedList<>();
        BlockPos blockPos = be.getBlockPos();
        Level level = be.getLevel();

        if (!level.isLoaded(blockPos))
            return neighbours;

        //6个方向的相邻
        for (Direction facing : Iterate.directions) {
            BlockPos relative = blockPos.relative(facing);
            if (level.isLoaded(relative))
                neighbours.add(relative);
        }
        //方块实体自定义额外的候选坐标
        BlockState blockState = be.getBlockState();
        if (!(blockState.getBlock() instanceof IRotate block))
            return neighbours;
        return be.addPropagationLocations(block, blockState, neighbours);
    }
    //决定最终是否传动
    private static KineticBlockEntity findConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos) {
        BlockState neighbourState = currentTE.getLevel()
                .getBlockState(neighbourPos);
        //实现旋转接口
        if (!(neighbourState.getBlock() instanceof IRotate))
            return null;
        //拥有方块实体
        if (!neighbourState.hasBlockEntity())
            return null;
        //方块实体继承KBlock
        BlockEntity neighbourBE = currentTE.getLevel()
                .getBlockEntity(neighbourPos);
                                                        //是前者被KBE强转后的变量名
        if (!(neighbourBE instanceof KineticBlockEntity neighbourKBE))
            return null;
        //方块继承KBlock
        if (!(neighbourKBE.getBlockState()
                .getBlock() instanceof IRotate))
            return null;
        //至少单向连通
        if (!isConnected(currentTE, neighbourKBE) && !isConnected(neighbourKBE, currentTE))
            return null;
        return neighbourKBE;
    }
    public static boolean isConnected(KineticBlockEntity from, KineticBlockEntity to) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();
        //Mixin移至此处
        if (ICogWheel.isLargeCog(stateFrom)&&ICogWheel.isLargeCog(stateTo)){
            BlockPos pos=from.getBlockPos().subtract(to.getBlockPos());
            if (pos.distSqr(BlockPos.ZERO)==2){
                return false;
            }
        }
        return isLargeCogToSpeedController(stateFrom, stateTo, to.getBlockPos()
                .subtract(from.getBlockPos()))
                || getRotationSpeedModifier(from, to) != 0
                || from.isCustomConnection(to, stateFrom, stateTo);
    }
    private static float getConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();

        // Rotation Speed Controller <-> Large Gear
        if (isLargeCogToSpeedController(stateFrom, stateTo, to.getBlockPos()
                .subtract(from.getBlockPos())))
            return SpeedControllerBlockEntity.getConveyedSpeed(from, to, true);
        if (isLargeCogToSpeedController(stateTo, stateFrom, from.getBlockPos()
                .subtract(to.getBlockPos())))
            return SpeedControllerBlockEntity.getConveyedSpeed(to, from, false);

        float rotationSpeedModifier = getRotationSpeedModifier(from, to);
        //理论速度*方法得到的速度值？什么鬼
        return from.getTheoreticalSpeed() * rotationSpeedModifier;
    }
    private static boolean isLargeCogToSpeedController(BlockState from, BlockState to, BlockPos diff) {
        if (!ICogWheel.isLargeCog(from) || !AllBlocks.ROTATION_SPEED_CONTROLLER.has(to))
            return false;
        if (!diff.equals(BlockPos.ZERO.below()))
            return false;
        Direction.Axis axis = from.getValue(CogWheelBlock.AXIS);
        if (axis.isVertical())
            return false;
        if (to.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) == axis)
            return false;
        return true;
    }
    private static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        final BlockState stateFrom = from.getBlockState();
        final BlockState stateTo = to.getBlockState();

        Block fromBlock = stateFrom.getBlock();
        Block toBlock = stateTo.getBlock();
        if (!(fromBlock instanceof IRotate definitionFrom && toBlock instanceof IRotate definitionTo))
            return 0;
        //从from指向to的，坐标
        final BlockPos diff = to.getBlockPos()
                .subtract(from.getBlockPos());
        //拿到偏移量的xyz
        final Direction direction = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());

        final Level world = from.getLevel();

        boolean alignedAxes = true;
        for (Direction.Axis axis : Direction.Axis.values())
            //如果单独的xyz之一与总向量不同
            if (axis != direction.getAxis())
                //且该单独的，不为0
                if (axis.choose(diff.getX(), diff.getY(), diff.getZ()) != 0)
                    //并非正向（指斜向
                    alignedAxes = false;

        boolean connectedByAxis =
                //正向    from可向方向接轴  to可向方向接轴    hasShaftTowards:return 偏移量轴方向==方块状态轴方向
                alignedAxes
                        && definitionFrom.hasShaftTowards(world, from.getBlockPos(), stateFrom, direction)
                        && definitionTo.hasShaftTowards(world, to.getBlockPos(), stateTo, direction.getOpposite());
        //small_gear_2
        boolean connectedByGears = ICogWheel.isSmallCog(stateFrom)
                && ICogWheel.isSmallCog(stateTo);
        //对外接口自定义变速
        float custom = from.propagateRotationTo(to, stateFrom, stateTo, diff, connectedByAxis, connectedByGears);
        if (custom != 0)
            return custom;

        // Axis <-> Axis
        //不是为啥，速度=from出口倍率/to入口倍率，诶等一下，
        if (connectedByAxis) {
            float axisModifier = getAxisModifier(to, direction.getOpposite());
            if (axisModifier != 0)
                axisModifier = 1 / axisModifier;
            return getAxisModifier(from, direction) * axisModifier;
        }

        // Attached Encased Belts
        //？链条？
        if (fromBlock instanceof ChainDriveBlock && toBlock instanceof ChainDriveBlock) {
            boolean connected = ChainDriveBlock.areBlocksConnected(stateFrom, stateTo, direction);
            return connected ? ChainDriveBlock.getRotationSpeedModifier(from, to) : 0;
        }

        // Large Gear <-> Large Gear
        if (isLargeToLargeGear(stateFrom, stateTo, diff)) {
            Direction.Axis sourceAxis = stateFrom.getValue(AXIS);
            Direction.Axis targetAxis = stateTo.getValue(AXIS);
                                //偏移量在两个轴上的投影
            int sourceAxisDiff = sourceAxis.choose(diff.getX(), diff.getY(), diff.getZ());
            int targetAxisDiff = targetAxis.choose(diff.getX(), diff.getY(), diff.getZ());

            return sourceAxisDiff > 0 ^ targetAxisDiff > 0 ? -1 : 1;
        }

        // Gear <-> Large Gear
        if (ICogWheel.isLargeCog(stateFrom) && ICogWheel.isSmallCog(stateTo))
            if (isLargeToSmallCog(stateFrom, stateTo, definitionTo, diff))
                return -2f;
        if (ICogWheel.isLargeCog(stateTo) && ICogWheel.isSmallCog(stateFrom))
            if (isLargeToSmallCog(stateTo, stateFrom, definitionFrom, diff))
                return -.5f;

        // Gear <-> Gear
        if (connectedByGears) {
            if (diff.distManhattan(BlockPos.ZERO) != 1)
                return 0;
            if (ICogWheel.isLargeCog(stateTo))
                return 0;
            if (direction.getAxis() == definitionFrom.getRotationAxis(stateFrom))
                return 0;
            if (definitionFrom.getRotationAxis(stateFrom) == definitionTo.getRotationAxis(stateTo))
                return -1;
        }



        //Helical <-> Helical   getXYZ（）应该都是 坐标差，有负数， 的吧？
        if (is_2_Helical(from.getBlockState(),to.getBlockState(),diff)){
            Direction.Axis from_axis=stateFrom.getValue(AXIS);
            Direction.Axis to_axis=stateTo.getValue(AXIS);
            if((from_axis== Direction.Axis.Y && to_axis==Direction.Axis.X)
                    ||(from_axis== Direction.Axis.X && to_axis==Direction.Axis.Z)
                    ||(from_axis== Direction.Axis.Z && to_axis==Direction.Axis.Y)){
                if(diff.getX()+diff.getY()+diff.getZ()>0){
                    return -1;
                }else if(diff.getX()+diff.getY()+diff.getZ()<0){
                    return 1;
                }else {
                    return 0;
                }
            } else if((from_axis== Direction.Axis.Y && to_axis==Direction.Axis.Z)
                    ||(from_axis== Direction.Axis.Z && to_axis==Direction.Axis.X)
                    ||(from_axis== Direction.Axis.X && to_axis==Direction.Axis.Y)){
                if(diff.getX()+diff.getY()+diff.getZ()>0){
                    return 1;
                }else if(diff.getX()+diff.getY()+diff.getZ()<0){
                    return -1;
                }else {
                    return 0;
                }
            }else {
                return 0;
            }

        }


        return 0;
    }
    private static float getAxisModifier(KineticBlockEntity be, Direction direction) {
        if (!(be.hasSource() || be.isSource()) || !(be instanceof DirectionalShaftHalvesBlockEntity))
            return 1;
        Direction source = ((DirectionalShaftHalvesBlockEntity) be).getSourceFacing();

        if (be instanceof GearboxBlockEntity)
            return direction.getAxis() == source.getAxis() ? direction == source ? 1 : -1
                    : direction.getAxisDirection() == source.getAxisDirection() ? -1 : 1;

        if (be instanceof SplitShaftBlockEntity)
            return ((SplitShaftBlockEntity) be).getRotationSpeedModifier(direction);

        return 1;
    }
    private static boolean isLargeToSmallCog(BlockState from, BlockState to, IRotate defTo, BlockPos diff) {
        Direction.Axis axisFrom = from.getValue(AXIS);
        if (axisFrom != defTo.getRotationAxis(to))
            return false;
        if (axisFrom.choose(diff.getX(), diff.getY(), diff.getZ()) != 0)
            return false;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == axisFrom)
                continue;
            if (Math.abs(axis.choose(diff.getX(), diff.getY(), diff.getZ())) != 1)
                return false;
        }
        return true;
    }
    private static boolean isLargeToLargeGear(BlockState from, BlockState to, BlockPos diff) {
        if (!ICogWheel.isLargeCog(from) || !ICogWheel.isLargeCog(to))
            return false;
        Direction.Axis fromAxis = from.getValue(AXIS);
        Direction.Axis toAxis = to.getValue(AXIS);
        if (fromAxis == toAxis)
            return false;
        for (Direction.Axis axis : Direction.Axis.values()) {
            int axisDiff = axis.choose(diff.getX(), diff.getY(), diff.getZ());
            if (axis == fromAxis || axis == toAxis) {
                if (axisDiff == 0)
                    return false;

            } else if (axisDiff != 0)
                return false;
        }
        return true;
    }
    //斜齿轮传递判断：三轴互相垂直
        // diff 在齿轮A旋转轴上的分量等于0 → axisA ⊥ diff
        //boolean aPerpDiff = axisA.choose(diff.getX(), diff.getY(), diff.getZ()) == 0;
    private static boolean is_2_Helical(BlockState from,BlockState to,BlockPos diff){
        if(!I_Gears.is_Helical_Gear(from)||!I_Gears.is_Helical_Gear(to))
            return false;
        Direction.Axis fromAxis = from.getValue(AXIS);
        Direction.Axis toAxis = to.getValue(AXIS);
        if(fromAxis==toAxis)
            return false;
        if (fromAxis.choose(diff.getX(),diff.getY(),diff.getZ())!=0||toAxis.choose(diff.getX(),diff.getY(),diff.getZ())!=0)
            return false;
        //distManhattan(BlockPos.ZERO)=x，y，z三者的绝对值之和
        if (diff.distManhattan(BlockPos.ZERO)!=1)
            return false;
        return true;
    }
    //动力清扫预选
    public static void handleRemoved(Level worldIn, BlockPos pos, KineticBlockEntity removedBE) {
        if (worldIn.isClientSide)//客户端不管，以同步双端
            return;
        if (removedBE == null)//无BE
            return;
        if (removedBE.getTheoreticalSpeed() == 0)//被破坏的方块速度为0
            return;

        for (BlockPos neighbourPos : getPotentialNeighbourLocations(removedBE)) {
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            if (!(neighbourState.getBlock() instanceof IRotate))
                continue;
            BlockEntity blockEntity = worldIn.getBlockEntity(neighbourPos);
            if (!(blockEntity instanceof KineticBlockEntity neighbourBE))
                continue;
            //如果邻居没动力源或者动力源不是该方块：跳过
            if (!neighbourBE.hasSource() || !neighbourBE.source.equals(pos))
                continue;

            propagateMissingSource(neighbourBE);
        }

    }
    //动力清扫
    public static void propagateMissingSource(KineticBlockEntity updateTE) {
        final Level world = updateTE.getLevel();

        //新动力源列表
        List<KineticBlockEntity> potentialNewSources = new LinkedList<>();
        //（清扫）坐标列表
        List<BlockPos> frontier = new LinkedList<>();
        frontier.add(updateTE.getBlockPos());
        //被标记为不是动力源的坐标    有动力源：源，   没源：null
        BlockPos missingSource = updateTE.hasSource() ? updateTE.source : null;

        while (!frontier.isEmpty()) {
            final BlockPos pos = frontier.remove(0);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof KineticBlockEntity currentBE))
                continue;
            //清扫
            currentBE.removeSource();
            currentBE.sendData();

            for (KineticBlockEntity neighbourBE : getConnectedNeighbours(currentBE)) {
                //是本次被破坏的方块：跳过
                if (neighbourBE.getBlockPos()
                        .equals(missingSource))
                    continue;
                //无动力源：跳过
                if (!neighbourBE.hasSource())
                    continue;
                //动力源不是我：加入动力源列表并跳过清扫
                if (!neighbourBE.source.equals(pos)) {
                    potentialNewSources.add(neighbourBE);
                    continue;
                }
                //是动力源：加入动力源列表
                if (neighbourBE.isSource())
                    potentialNewSources.add(neighbourBE);
                //加入清扫队列
                frontier.add(neighbourBE.getBlockPos());
            }
        }
        //把这些发现的动力源的下属网络全部清掉之后，重新当成新的动力源来计算
        for (KineticBlockEntity newSource : potentialNewSources) {
            if (newSource.hasSource() || newSource.isSource()) {
                propagateNewSource(newSource);
                return;
            }
        }
    }
}
