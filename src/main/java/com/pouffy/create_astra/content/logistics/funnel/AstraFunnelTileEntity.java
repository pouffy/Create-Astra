package com.pouffy.create_astra.content.logistics.funnel;

import java.lang.ref.WeakReference;
import java.util.List;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelFlapPacket;
import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.pouffy.create_astra.foundation.registry.PacketRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.contraptions.relays.belt.BeltHelper;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.block.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.tterrag.registrate.fabric.EnvExecutor;

import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AstraFunnelTileEntity extends SmartTileEntity implements IHaveHoveringInformation {

	private FilteringBehaviour filtering;
	private InvManipulationBehaviour invManipulation;
	private int extractionCooldown;

	private WeakReference<ItemEntity> lastObserved; // In-world Extractors only

	LerpedFloat flap;

	static enum Mode {
		INVALID, PAUSED, COLLECT, PUSHING_TO_BELT, TAKING_FROM_BELT, EXTRACT
	}

	public AstraFunnelTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		extractionCooldown = 0;
		flap = createChasingFlap();
	}

	public Mode determineCurrentMode() {
		BlockState state = getBlockState();
		if (!AstraFunnelBlock.isFunnel(state))
			return Mode.INVALID;
		if (state.getOptionalValue(BlockStateProperties.POWERED)
				.orElse(false))
			return Mode.PAUSED;
		if (state.getBlock() instanceof AstraBeltFunnelBlock) {
			AstraBeltFunnelBlock.Shape shape = state.getValue(AstraBeltFunnelBlock.SHAPE);
			if (shape == AstraBeltFunnelBlock.Shape.PULLING)
				return Mode.TAKING_FROM_BELT;
			if (shape == AstraBeltFunnelBlock.Shape.PUSHING)
				return Mode.PUSHING_TO_BELT;

			BeltTileEntity belt = BeltHelper.getSegmentTE(level, worldPosition.below());
			if (belt != null)
				return belt.getMovementFacing() == state.getValue(AstraBeltFunnelBlock.HORIZONTAL_FACING) ? Mode.PUSHING_TO_BELT
						: Mode.TAKING_FROM_BELT;
			return Mode.INVALID;
		}
		if (state.getBlock() instanceof AstraFunnelBlock)
			return state.getValue(AstraFunnelBlock.EXTRACTING) ? Mode.EXTRACT : Mode.COLLECT;

		return Mode.INVALID;
	}

	@Override
	public void tick() {
		super.tick();
		flap.tickChaser();
		Mode mode = determineCurrentMode();
		if (level.isClientSide)
			return;

		// Redstone resets the extraction cooldown
		if (mode == Mode.PAUSED)
			extractionCooldown = 0;
		if (mode == Mode.TAKING_FROM_BELT)
			return;

		if (extractionCooldown > 0) {
			extractionCooldown--;
			return;
		}

		if (mode == Mode.PUSHING_TO_BELT)
			activateExtractingBeltFunnel();
		if (mode == Mode.EXTRACT)
			activateExtractor();
	}

	private void activateExtractor() {
		BlockState blockState = getBlockState();
		Direction facing = AstraAbstractFunnelBlock.getFunnelFacing(blockState);

		if (facing == null)
			return;

		boolean trackingEntityPresent = true;
		AABB area = getEntityOverflowScanningArea();

		// Check if last item is still blocking the extractor
		if (lastObserved == null) {
			trackingEntityPresent = false;
		} else {
			ItemEntity lastEntity = lastObserved.get();
			if (lastEntity == null || !lastEntity.isAlive() || !lastEntity.getBoundingBox()
					.intersects(area)) {
				trackingEntityPresent = false;
				lastObserved = null;
			}
		}

		if (trackingEntityPresent)
			return;

		// Find other entities blocking the extract (only if necessary)
		int amountToExtract = getAmountToExtract();
		ItemStack stack = invManipulation.simulate()
				.extract(amountToExtract);
		if (stack.isEmpty())
			return;
		for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, area)) {
			lastObserved = new WeakReference<>(itemEntity);
			return;
		}

		// Extract
		stack = invManipulation.extract(amountToExtract);
		if (stack.isEmpty())
			return;

		flap(false);
		onTransfer(stack);

		Vec3 outputPos = VecHelper.getCenterOf(worldPosition);
		boolean vertical = facing.getAxis()
				.isVertical();
		boolean up = facing == Direction.UP;

		outputPos = outputPos.add(Vec3.atLowerCornerOf(facing.getNormal())
				.scale(vertical ? up ? .15f : .5f : .25f));
		if (!vertical)
			outputPos = outputPos.subtract(0, .45f, 0);

		Vec3 motion = Vec3.ZERO;
		if (up)
			motion = new Vec3(0, 4 / 16f, 0);

		ItemEntity item = new ItemEntity(level, outputPos.x, outputPos.y, outputPos.z, stack.copy());
		item.setDefaultPickUpDelay();
		item.setDeltaMovement(motion);
		level.addFreshEntity(item);
		lastObserved = new WeakReference<>(item);

		startCooldown();
	}

	static final AABB coreBB =
			new AABB(VecHelper.CENTER_OF_ORIGIN, VecHelper.CENTER_OF_ORIGIN).inflate(.75f);

	private AABB getEntityOverflowScanningArea() {
		Direction facing = AstraAbstractFunnelBlock.getFunnelFacing(getBlockState());
		AABB bb = coreBB.move(worldPosition);
		if (facing == null || facing == Direction.UP)
			return bb;
		return bb.expandTowards(0, -1, 0);
	}

	private void activateExtractingBeltFunnel() {
		BlockState blockState = getBlockState();
		Direction facing = blockState.getValue(AstraBeltFunnelBlock.HORIZONTAL_FACING);
		DirectBeltInputBehaviour inputBehaviour =
				TileEntityBehaviour.get(level, worldPosition.below(), DirectBeltInputBehaviour.TYPE);

		if (inputBehaviour == null)
			return;
		if (!inputBehaviour.canInsertFromSide(facing))
			return;

		int amountToExtract = getAmountToExtract();
		ItemStack stack = invManipulation.extract(amountToExtract, s -> inputBehaviour.handleInsertion(s, facing, true)
				.isEmpty());
		if (stack.isEmpty())
			return;
		flap(false);
		onTransfer(stack);
		inputBehaviour.handleInsertion(stack, facing, false);
		startCooldown();
	}

	public int getAmountToExtract() {
		if (!supportsAmountOnFilter())
			return -1;
		int amountToExtract = invManipulation.getAmountFromFilter();
		if (!filtering.isActive())
			amountToExtract = 1;
		return amountToExtract;
	}

	private int startCooldown() {
		return extractionCooldown = AllConfigs.SERVER.logistics.defaultExtractionTimer.get();
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		invManipulation =
				new InvManipulationBehaviour(this, (w, p, s) -> new BlockFace(p, AstraAbstractFunnelBlock.getFunnelFacing(s)
						.getOpposite()));
		behaviours.add(invManipulation);

		filtering = new FilteringBehaviour(this, new FunnelFilterSlotPositioning());
		filtering.showCountWhen(this::supportsAmountOnFilter);
		filtering.onlyActiveWhen(this::supportsFiltering);
		behaviours.add(filtering);

		behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput)
				.setInsertionHandler(this::handleDirectBeltInput));
		registerAwardables(behaviours, AllAdvancements.FUNNEL);
	}

	private boolean supportsAmountOnFilter() {
		BlockState blockState = getBlockState();
		boolean beltFunnelsupportsAmount = false;
		if (blockState.getBlock() instanceof AstraBeltFunnelBlock) {
			AstraBeltFunnelBlock.Shape shape = blockState.getValue(AstraBeltFunnelBlock.SHAPE);
			if (shape == AstraBeltFunnelBlock.Shape.PUSHING)
				beltFunnelsupportsAmount = true;
			else
				beltFunnelsupportsAmount = BeltHelper.getSegmentTE(level, worldPosition.below()) != null;
		}
		boolean extractor = blockState.getBlock() instanceof AstraFunnelBlock && blockState.getValue(AstraFunnelBlock.EXTRACTING);
		return beltFunnelsupportsAmount || extractor;
	}

	private boolean supportsDirectBeltInput(Direction side) {
		BlockState blockState = getBlockState();
		if (blockState == null)
			return false;
		if (!(blockState.getBlock() instanceof AstraFunnelBlock))
			return false;
		if (blockState.getValue(AstraFunnelBlock.EXTRACTING))
			return false;
		return AstraFunnelBlock.getFunnelFacing(blockState) == Direction.UP;
	}

	private boolean supportsFiltering() {
		BlockState blockState = getBlockState();
		return BlockRegistry.DESH_BELT_FUNNEL.has(blockState) || BlockRegistry.DESH_FUNNEL.has(blockState);
	}

	private ItemStack handleDirectBeltInput(TransportedItemStack stack, Direction side, boolean simulate) {
		ItemStack inserted = stack.stack;
		if (!filtering.test(inserted))
			return inserted;
		if (determineCurrentMode() == Mode.PAUSED)
			return inserted;
		if (simulate)
			invManipulation.simulate();
		if (!simulate)
			onTransfer(inserted);
		return invManipulation.insert(inserted);
	}

	public void flap(boolean inward) {
		if (!level.isClientSide) {
			PacketRegistry.channel.sendToClientsTracking(new AstraFunnelFlapPacket(this, inward), this);
		} else {
			flap.setValue(inward ? 1 : -1);
			AllSoundEvents.FUNNEL_FLAP.playAt(level, worldPosition, 1, 1, true);
		}
	}

	public boolean hasFlap() {
		BlockState blockState = getBlockState();
		if (!AstraAbstractFunnelBlock.getFunnelFacing(blockState)
				.getAxis()
				.isHorizontal())
			return false;
		return true;
	}

	public float getFlapOffset() {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof AstraBeltFunnelBlock))
			return -1 / 16f;
		switch (blockState.getValue(AstraBeltFunnelBlock.SHAPE)) {
			default:
			case RETRACTED:
				return 0;
			case EXTENDED:
				return 8 / 16f;
			case PULLING:
			case PUSHING:
				return -2 / 16f;
		}
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putInt("TransferCooldown", extractionCooldown);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		extractionCooldown = compound.getInt("TransferCooldown");

		if (clientPacket)
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> InstancedRenderDispatcher.enqueueUpdate(this));
	}

	public void onTransfer(ItemStack stack) {
		AllBlocks.CONTENT_OBSERVER.get()
				.onFunnelTransfer(level, worldPosition, stack);
		award(AllAdvancements.FUNNEL);
	}

	private LerpedFloat createChasingFlap() {
		return LerpedFloat.linear()
				.startWithValue(.25f)
				.chase(0, .05f, LerpedFloat.Chaser.EXP);
	}
}