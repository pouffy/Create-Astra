package com.pouffy.create_astra.compat.jei;

import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.compat.jei.category.HeavyPressingCategory;
import com.pouffy.create_astra.compat.rei.CreateAstraREI;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressingRecipe;
import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;
import com.simibubi.create.AllFluids;
import com.simibubi.create.compat.jei.BlueprintTransferHandler;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.GhostIngredientHandler;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.PotionFluidSubtypeInterpreter;
import com.simibubi.create.compat.jei.SlotMover;
import com.simibubi.create.compat.jei.ToolboxColoringRecipeMaker;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import com.simibubi.create.content.contraptions.fluids.potion.PotionFluid;
import com.simibubi.create.content.curiosities.tools.BlueprintScreen;
import com.simibubi.create.content.logistics.item.LinkedControllerScreen;
import com.simibubi.create.content.logistics.item.filter.AbstractFilterScreen;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleScreen;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CRecipes;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.gui.container.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.utility.Lang;

import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.RecipeManagerAccessor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class CreateAstraJEI implements IModPlugin {
	private static final ResourceLocation ID = CreateAstra.asResource("jei_plugin");

	private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
	private IIngredientManager ingredientManager;

	private void loadCategories() {
		allCategories.clear();

		CreateRecipeCategory<?>
				pressing = builder(HeavyPressingRecipe.class)
				.addTypedRecipes(RecipeRegistry.HEAVY_PRESSING)
				.catalyst(BlockRegistry.HEAVY_PRESS::get)
				.doubleItemIcon(BlockRegistry.HEAVY_PRESS.get(), ModItems.IRON_PLATE)
				.emptyBackground(177, 70)
				.build("heavy_pressing", HeavyPressingCategory::new);

	}
	private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new CategoryBuilder<>(recipeClass);
	}

	@Override
	@Nonnull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		loadCategories();
		registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
	}


	private class CategoryBuilder<T extends Recipe<?>> {
		private final Class<? extends T> recipeClass;
		private Predicate<CRecipes> predicate = cRecipes -> true;

		private IDrawable background;
		private IDrawable icon;

		private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
		private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

		public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		}

		public CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
			this.predicate = predicate;
			return this;
		}

		public CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
			predicate = c -> configValue.apply(c).get();
			return this;
		}

		public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
			recipeListConsumers.add(consumer);
			return this;
		}

		public CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
			return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
		}

		public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add((T) recipe);
				}
			}));
		}

		public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add(converter.apply(recipe));
				}
			}));
		}

		public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
			return addTypedRecipes(recipeTypeEntry::getType);
		}

		public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> CreateAstraJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
		}

		public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType, Function<Recipe<?>, T> converter) {
			return addRecipeListConsumer(recipes -> CreateAstraJEI.<T>consumeTypedRecipes(recipe -> recipes.add(converter.apply(recipe)), recipeType.get()));
		}

		public CategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<? extends T>> recipeType, Predicate<Recipe<?>> pred) {
			return addRecipeListConsumer(recipes -> CreateAstraJEI.<T>consumeTypedRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add(recipe);
				}
			}, recipeType.get()));
		}

		public CategoryBuilder<T> addTypedRecipesExcluding(Supplier<RecipeType<? extends T>> recipeType,
														   Supplier<RecipeType<? extends T>> excluded) {
			return addRecipeListConsumer(recipes -> {
				List<Recipe<?>> excludedRecipes = getTypedRecipes(excluded.get());
				CreateAstraJEI.<T>consumeTypedRecipes(recipe -> {
					for (Recipe<?> excludedRecipe : excludedRecipes) {
						if (doInputsMatch(recipe, excludedRecipe)) {
							return;
						}
					}
					recipes.add(recipe);
				}, recipeType.get());
			});
		}

		public CategoryBuilder<T> removeRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> {
				List<Recipe<?>> excludedRecipes = getTypedRecipes(recipeType.get());
				recipes.removeIf(recipe -> {
					for (Recipe<?> excludedRecipe : excludedRecipes) {
						if (doInputsMatch(recipe, excludedRecipe)) {
							return true;
						}
					}
					return false;
				});
			});
		}

		public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
			catalysts.add(supplier);
			return this;
		}

		public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
			return catalystStack(() -> new ItemStack(supplier.get()
					.asItem()));
		}

		public CategoryBuilder<T> icon(IDrawable icon) {
			this.icon = icon;
			return this;
		}

		public CategoryBuilder<T> itemIcon(ItemLike item) {
			icon(new ItemIcon(() -> new ItemStack(item)));
			return this;
		}

		public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
			icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
			return this;
		}

		public CategoryBuilder<T> background(IDrawable background) {
			this.background = background;
			return this;
		}

		public CategoryBuilder<T> emptyBackground(int width, int height) {
			background(new EmptyBackground(width, height));
			return this;
		}

		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			Supplier<List<T>> recipesSupplier;
			if (predicate.test(AllConfigs.SERVER.recipes)) {
				recipesSupplier = () -> {
					List<T> recipes = new ArrayList<>();
					for (Consumer<List<T>> consumer : recipeListConsumers)
						consumer.accept(recipes);
					return recipes;
				};
			} else {
				recipesSupplier = () -> Collections.emptyList();
			}

			CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
					new mezz.jei.api.recipe.RecipeType<>(CreateAstra.asResource(name), recipeClass),
					Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts);
			CreateRecipeCategory<T> category = factory.create(info);
			allCategories.add(category);
			return category;
		}
	}

	public static void consumeAllRecipes(Consumer<Recipe<?>> consumer) {
		Minecraft.getInstance()
				.getConnection()
				.getRecipeManager()
				.getRecipes()
				.forEach(consumer);
	}

	public static <T extends Recipe<?>> void consumeTypedRecipes(Consumer<T> consumer, RecipeType<?> type) {
		Map<ResourceLocation, Recipe<?>> map = ((RecipeManagerAccessor) Minecraft.getInstance()
				.getConnection()
				.getRecipeManager()).port_lib$getRecipes().get(type);
		if (map != null) {
			map.values().forEach(recipe -> consumer.accept((T) recipe));
		}
	}

	public static List<Recipe<?>> getTypedRecipes(RecipeType<?> type) {
		List<Recipe<?>> recipes = new ArrayList<>();
		consumeTypedRecipes(recipes::add, type);
		return recipes;
	}

	public static List<Recipe<?>> getTypedRecipesExcluding(RecipeType<?> type, Predicate<Recipe<?>> exclusionPred) {
		List<Recipe<?>> recipes = getTypedRecipes(type);
		recipes.removeIf(exclusionPred);
		return recipes;
	}

	public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
		if (recipe1.getIngredients()
				.isEmpty()
				|| recipe2.getIngredients()
				.isEmpty()) {
			return false;
		}
		ItemStack[] matchingStacks = recipe1.getIngredients()
				.get(0)
				.getItems();
		if (matchingStacks.length == 0) {
			return false;
		}
		return recipe2.getIngredients()
				.get(0)
				.test(matchingStacks[0]);
	}
}
