package com.pouffy.create_astra.content.contraptions.heavy_press;

import com.pouffy.create_astra.foundation.registry.TileEntityRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeavyPressBlock extends HorizontalKineticBlock implements ITE<HeavyPressBlockEntity> {
	public HeavyPressBlock(Properties properties) {
		super(properties);
	}
	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}
	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
	}
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}
	public IRotate.SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.FAST;
	}
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext
				&& ((EntityCollisionContext) context).getEntity() instanceof Player)
			return AllShapes.CASING_14PX.get(Direction.DOWN);

		return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
	}
	@Override
	public Class<HeavyPressBlockEntity> getTileEntityClass() {
		return HeavyPressBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends HeavyPressBlockEntity> getTileEntityType() {
		return TileEntityRegistry.MECHANICAL_PRESS.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}
}
