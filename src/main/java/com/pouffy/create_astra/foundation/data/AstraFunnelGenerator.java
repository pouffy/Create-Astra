package com.pouffy.create_astra.foundation.data;

import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelBlock;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelItem;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class AstraFunnelGenerator extends SpecialBlockStateGen {

	private String type;
	private ResourceLocation particleTexture;
	private boolean hasFilter;

	public AstraFunnelGenerator(String type, boolean hasFilter) {
		this.type = type;
		this.hasFilter = hasFilter;
		this.particleTexture = CreateAstra.asResource("block/" + type + "_casing");
	}

	@Override
	protected int getXRotation(BlockState state) {
		return state.getValue(AstraFunnelBlock.FACING) == Direction.DOWN ? 180 : 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		return horizontalAngle(state.getValue(AstraFunnelBlock.FACING)) + 180;
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> c, RegistrateBlockstateProvider p,
												BlockState s) {
		String powered = s.getValue(AstraFunnelBlock.POWERED) ? "_powered" : "";
		String closed = s.getValue(AstraFunnelBlock.POWERED) ? "_closed" : "_open";
		String extracting = s.getValue(AstraFunnelBlock.EXTRACTING) ? "_push" : "_pull";
		Direction facing = s.getValue(AstraFunnelBlock.FACING);
		boolean horizontal = facing.getAxis()
				.isHorizontal();
		String parent = horizontal ? "horizontal" : hasFilter ? "vertical" : "vertical_filterless";

		BlockModelBuilder model = p.models()
				.withExistingParent("block/" + type + "_funnel_" + parent + extracting + powered,
						p.modLoc("block/funnel/block_" + parent))
				.texture("particle", particleTexture)
				.texture("7", p.modLoc("block/" + type + "_funnel_plating"))
				.texture("5", p.modLoc("block/" + type + "_funnel_tall" + powered))
				.texture("2_2", p.modLoc("block/" + type + "_funnel" + extracting))
				.texture("3", p.modLoc("block/" + type + "_funnel_back"));

		if (horizontal)
			return model.texture("6", p.modLoc("block/" + type + "_funnel" + powered));

		return model.texture("8", particleTexture)
				.texture("9", p.modLoc("block/" + type + "_funnel_slope"))
				.texture("10", p.modLoc("block/funnel" + closed));
	}

	public static NonNullBiConsumer<DataGenContext<Item, AstraFunnelItem>, RegistrateItemModelProvider> itemModel(
			String type) {
		ResourceLocation particleTexture = CreateAstra.asResource("block/" + type + "_casing");
		return (c, p) -> {
			p.withExistingParent("item/" + type + "_funnel", p.modLoc("block/funnel/item"))
					.texture("particle", particleTexture)
					.texture("7", p.modLoc("block/" + type + "_funnel_plating"))
					.texture("2", p.modLoc("block/" + type + "_funnel_neutral"))
					.texture("6", p.modLoc("block/" + type + "_funnel"))
					.texture("5", p.modLoc("block/" + type + "_funnel_tall"))
					.texture("3", p.modLoc("block/" + type + "_funnel_back"));
		};
	}
}
