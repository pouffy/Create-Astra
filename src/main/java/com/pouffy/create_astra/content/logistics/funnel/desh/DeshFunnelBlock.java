package com.pouffy.create_astra.content.logistics.funnel.desh;

import com.pouffy.create_astra.content.logistics.funnel.AstraBeltFunnelBlock;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelBlock;
import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class DeshFunnelBlock extends AstraFunnelBlock {

	public DeshFunnelBlock(Properties p_i48415_1_) {
		super(p_i48415_1_);
	}

	@Override
	public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
		Direction facing = getFacing(state);
		return BlockRegistry.DESH_BELT_FUNNEL.getDefaultState()
				.setValue(AstraBeltFunnelBlock.HORIZONTAL_FACING, facing)
				.setValue(POWERED, state.getValue(POWERED));
	}
}
