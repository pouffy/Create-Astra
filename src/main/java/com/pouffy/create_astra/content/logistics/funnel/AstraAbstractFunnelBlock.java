package com.pouffy.create_astra.content.logistics.funnel;

import com.pouffy.create_astra.foundation.registry.TileEntityRegistry;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.inventory.InvManipulationBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

import javax.annotation.Nullable;

import java.util.Random;

public abstract class AstraAbstractFunnelBlock extends Block implements ITE<AstraFunnelTileEntity>, IWrenchable, ReducedDestroyEffects {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	protected AstraAbstractFunnelBlock(Properties p_i48377_1_) {
		super(p_i48377_1_);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

//	@Environment(EnvType.CLIENT)
//	public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
//		consumer.accept(new ReducedDestroyEffects());
//	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(POWERED, context.getLevel()
				.hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(POWERED));
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
								boolean isMoving) {
		if (worldIn.isClientSide)
			return;
		InvManipulationBehaviour behaviour = TileEntityBehaviour.get(worldIn, pos, InvManipulationBehaviour.TYPE);
		if (behaviour != null)
			behaviour.onNeighborChanged(fromPos);
		if (!worldIn.getBlockTicks()
				.willTickThisTick(pos, this))
			worldIn.scheduleTick(pos, this, 0);
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random r) {
		boolean previouslyPowered = state.getValue(POWERED);
		if (previouslyPowered != worldIn.hasNeighborSignal(pos))
			worldIn.setBlock(pos, state.cycle(POWERED), 2);
	}

	public static ItemStack tryInsert(Level worldIn, BlockPos pos, ItemStack toInsert, boolean simulate) {
		FilteringBehaviour filter = TileEntityBehaviour.get(worldIn, pos, FilteringBehaviour.TYPE);
		InvManipulationBehaviour inserter = TileEntityBehaviour.get(worldIn, pos, InvManipulationBehaviour.TYPE);
		if (inserter == null)
			return toInsert;
		if (filter != null && !filter.test(toInsert))
			return toInsert;
		if (simulate)
			inserter.simulate();
		ItemStack insert = inserter.insert(toInsert);

		if (!simulate && insert.getCount() != toInsert.getCount()) {
			BlockEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof AstraFunnelTileEntity) {
				AstraFunnelTileEntity funnelTileEntity = (AstraFunnelTileEntity) tileEntity;
				funnelTileEntity.onTransfer(toInsert);
				if (funnelTileEntity.hasFlap())
					funnelTileEntity.flap(true);
			}
		}
		return insert;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Block block = world.getBlockState(pos.relative(getFunnelFacing(state).getOpposite()))
				.getBlock();
		return !(block instanceof AstraAbstractFunnelBlock);
	}

	@Nullable
	public static boolean isFunnel(BlockState state) {
		return state.getBlock() instanceof AstraAbstractFunnelBlock;
	}

	@Nullable
	public static Direction getFunnelFacing(BlockState state) {
		if (!(state.getBlock() instanceof AstraAbstractFunnelBlock))
			return null;
		return ((AstraAbstractFunnelBlock) state.getBlock()).getFacing(state);
	}

	protected abstract Direction getFacing(BlockState state);

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		ITE.onRemove(state, world, pos, newState);
	}

	@Override
	public Class<AstraFunnelTileEntity> getTileEntityClass() {
		return AstraFunnelTileEntity.class;
	}

	public BlockEntityType<? extends AstraFunnelTileEntity> getTileEntityType() {
		return TileEntityRegistry.FUNNEL.get();
	};

}
