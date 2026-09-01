package com.jgxc_bek.create_gear.content.kinetics.Simple_Relays;

import com.jgxc_bek.create_gear.ALL_BlockEntity_Types;
import com.jgxc_bek.create_gear.ALL_Shapes;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Helical_Gear_Block extends AbstractSimpleShaftBlock implements EncasableBlock ,ICogWheel,I_Gears{


    protected Helical_Gear_Block(Properties properties) {
        super(properties);
    }
    public static Helical_Gear_Block get_new(Properties properties){
        return new Helical_Gear_Block(properties);
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return ALL_Shapes.Helical_Gear.get(state.getValue(AXIS));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return isValidCogwheelPosition(worldIn, pos, state.getValue(AXIS));
    }
    public static boolean isValidCogwheelPosition(LevelReader worldIn, BlockPos pos, Direction.Axis cogAxis) {
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == cogAxis)
                continue;

            BlockPos offsetPos = pos.relative(facing);
            BlockState blockState = worldIn.getBlockState(offsetPos);
            if (blockState.hasProperty(AXIS) && facing.getAxis() == blockState.getValue(AXIS))
                continue;

            if (ICogWheel.isLargeCog(blockState))
                return false;
        }
        return true;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    //判断自己
    @Override
    public  boolean is_Helical_Gear(){
        return true;
    }
    //外壳逻辑
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemInteractionResult result = tryEncase(state, level, pos, stack, player, hand, hitResult);
        if (result.consumesAction())
            return result;

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    //放置逻辑
    protected Direction.Axis getAxisForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())
            return context.getClickedFace()
                    .getAxis();

        Level world = context.getLevel();

        BlockPos placedOnPos = context.getClickedPos()
                .relative(context.getClickedFace()
                        .getOpposite());
        BlockState placedAgainst = world.getBlockState(placedOnPos);

        Block block = placedAgainst.getBlock();
        if (ICogWheel.isSmallCog(placedAgainst))
            return ((IRotate) block).getRotationAxis(placedAgainst);

        Direction.Axis preferredAxis = getPreferredAxis(context);
        return preferredAxis != null ? preferredAxis
                : context.getClickedFace()
                .getAxis();
    }
    //含水方块
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean shouldWaterlog = context.getLevel()
                .getFluidState(context.getClickedPos())
                .getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(AXIS, getAxisForPlacement(context))
                .setValue(BlockStateProperties.WATERLOGGED, shouldWaterlog);
    }

    //单独齿轮
    @Override
    public boolean isDedicatedCogWheel() {
        return true;
    }

    //方块实体
    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return ALL_BlockEntity_Types.ENCASED_HELICAL_GEAR.get();
    }

    @Override
    public boolean isSmallCog() {
        return false;
    }

}
