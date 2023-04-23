package com.pouffy.create_astra.foundation.data;

import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

public class HeavyCompactingRecipeGen extends AstaRecipeGen {
	GeneratedRecipe
			IRON_PLATING = create("iron_plating", b -> b
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.require(ModItems.IRON_PLATE)
			.output(ModItems.IRON_PLATING, 32)),
			STEEL_PLATING = create("steel_plating", b -> b
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.COMPRESSED_STEEL)
					.output(ModItems.STEEL_PLATING, 8)),
			DESH_PLATING = create("desh_plating", b -> b
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.COMPRESSED_DESH)
					.output(ModItems.DESH_PLATING, 8)),
			OSTRUM_PLATING = create("ostrum_plating", b -> b
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.COMPRESSED_OSTRUM)
					.output(ModItems.OSTRUM_PLATING, 8)),
			CALORITE_PLATING = create("calorite_plating", b -> b
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.IRON_PLATING)
					.require(ModItems.COMPRESSED_CALORITE)
					.output(ModItems.CALORITE_PLATING, 8))
			;
	public HeavyCompactingRecipeGen(FabricDataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected RecipeRegistry getRecipeType() {
		return RecipeRegistry.HEAVY_COMPACTING;
	}

}
