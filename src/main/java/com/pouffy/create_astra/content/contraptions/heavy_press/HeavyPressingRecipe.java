package com.pouffy.create_astra.content.contraptions.heavy_press;

import java.util.List;
import java.util.Set;

import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import com.simibubi.create.content.contraptions.itemAssembly.IAssemblyRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.utility.Lang;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class HeavyPressingRecipe extends ProcessingRecipe<Container> implements IAssemblyRecipe {

	public HeavyPressingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(RecipeRegistry.HEAVY_PRESSING, params);
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
				.test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 2;
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@Environment(EnvType.CLIENT)
	public Component getDescriptionForAssembly() {
		return Lang.translateDirect("recipe.assembly.heavy_pressing");
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(BlockRegistry.HEAVY_PRESS.get());
	}

	@Override
	public SequencedAssemblySubCategoryType getJEISubCategory() {
		return SequencedAssemblySubCategoryType.PRESSING;
	}

}
