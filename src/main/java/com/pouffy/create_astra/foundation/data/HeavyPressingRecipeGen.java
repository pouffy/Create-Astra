package com.pouffy.create_astra.foundation.data;

import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.item.Items;

public class HeavyPressingRecipeGen extends AstaRecipeGen {
	GeneratedRecipe
			IRON_PLATE = create(() -> Items.IRON_INGOT, b -> b.output(ModItems.IRON_PLATE)),
	        STEEL_PLATE = create(() -> ModItems.STEEL_INGOT, b -> b.output(ModItems.COMPRESSED_STEEL)),
			OSTRUM_PLATE = create(() -> ModItems.OSTRUM_INGOT, b -> b.output(ModItems.COMPRESSED_OSTRUM)),
	        CALORITE_PLATE = create(() -> ModItems.CALORITE_INGOT, b -> b.output(ModItems.COMPRESSED_CALORITE)),
	        DESH_PLATE = create(() -> ModItems.DESH_INGOT, b -> b.output(ModItems.COMPRESSED_DESH))
			;
	public HeavyPressingRecipeGen(FabricDataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected RecipeRegistry getRecipeType() {
		return RecipeRegistry.HEAVY_PRESSING;
	}

}
