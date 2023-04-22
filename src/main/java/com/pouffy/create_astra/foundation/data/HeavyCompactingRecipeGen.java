package com.pouffy.create_astra.foundation.data;

import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.item.Items;

public class HeavyCompactingRecipeGen extends AstaRecipeGen {
	GeneratedRecipe
			IRON_PLATE = create(() -> Items.IRON_INGOT, b -> b.output(ModItems.IRON_PLATE))
			;
	public HeavyCompactingRecipeGen(FabricDataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected RecipeRegistry getRecipeType() {
		return RecipeRegistry.HEAVY_COMPACTING;
	}

}
