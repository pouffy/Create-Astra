package com.pouffy.create_astra.foundation.registry;

import com.google.common.collect.ImmutableSet;
import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyCompactingRecipe;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;

import io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public enum RecipeRegistry implements IRecipeTypeInfo {
	HEAVY_PRESSING(HeavyPressingRecipe::new),
	HEAVY_COMPACTING(HeavyCompactingRecipe::new)
	;

	private final ResourceLocation id;
	private final RecipeSerializer<?> serializerObject;
	@Nullable
	private final RecipeType<?> typeObject;
	private final Supplier<RecipeType<?>> type;

	RecipeRegistry(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
		String name = Lang.asId(name());
		id = CreateAstra.asResource(name);
		serializerObject = Registry.register(Registry.RECIPE_SERIALIZER, id, serializerSupplier.get());
		if (registerType) {
			typeObject = typeSupplier.get();
			Registry.register(Registry.RECIPE_TYPE, id, typeObject);
		} else {
			typeObject = null;
		}
		type = typeSupplier;
	}

	RecipeRegistry(Supplier<RecipeSerializer<?>> serializerSupplier) {
		String name = Lang.asId(name());
		id = CreateAstra.asResource(name);
		serializerObject = Registry.register(Registry.RECIPE_SERIALIZER, id, serializerSupplier.get());
		typeObject = simpleType(id);
		Registry.register(Registry.RECIPE_TYPE, id, typeObject);
		type = () -> typeObject;
	}

	RecipeRegistry(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
		this(() -> new ProcessingRecipeSerializer<>(processingFactory));
	}

	public static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
		String stringId = id.toString();
		return new RecipeType<T>() {
			@Override
			public String toString() {
				return stringId;
			}
		};
	}

	public static void register() {
		ShapedRecipeUtil.setCraftingSize(9, 9);
		// fabric: just load the class
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializerObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeType<?>> T getType() {
		return (T) type.get();
	}

	public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
		return world.getRecipeManager()
				.getRecipeFor(getType(), inv, world);
	}
}
