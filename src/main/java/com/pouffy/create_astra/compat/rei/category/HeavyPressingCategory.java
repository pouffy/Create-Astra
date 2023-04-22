package com.pouffy.create_astra.compat.rei.category;

import com.pouffy.create_astra.compat.recipe_viewers.animations.REIAnimatedHeavyPress;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressingRecipe;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.category.WidgetUtil;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Lang;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.ChatFormatting;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.gui.widgets.Widget;

import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;


public class HeavyPressingCategory extends CreateRecipeCategory<HeavyPressingRecipe> {
	public HeavyPressingCategory(Info<HeavyPressingRecipe> info) {
		super(info);
	}
	@Override
	public List<Widget> setupDisplay(CreateDisplay<HeavyPressingRecipe> display, Rectangle bounds) {
		Point origin = new Point(bounds.getX(), bounds.getY() + 15);
		List<Widget> widgets = new ArrayList<>();
		List<ProcessingOutput> results = display.getRecipe().getRollableResults();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createSlot(new Point(origin.x + 27, origin.y + 51)).disableBackground().markInput().entries(display.getInputEntries().get(0)));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.x + 26, origin.y + 50));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SHADOW, origin.x + 61, origin.y + 41));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_LONG_ARROW, origin.x + 52, origin.y + 54));

		for (int outputIndex = 0; outputIndex < results.size(); outputIndex++) {
			List<Component> tooltip = new ArrayList<>();
			if (results.get(outputIndex).getChance() != 1)
				tooltip.add((Component) Lang.translateDirect("recipe.processing.chance", results.get(outputIndex).getChance() < 0.01 ? "<1" : (int) (results.get(outputIndex).getChance() * 100))
						.withStyle(ChatFormatting.GOLD));
			widgets.add(Widgets.createSlot(new Point((origin.x + 131 + 19 * outputIndex) + 1, (origin.y + 50) + 1))
					.disableBackground().markOutput()
					.entry(EntryStack.of(VanillaEntryTypes.ITEM, results.get(outputIndex).getStack()).tooltip((Component) tooltip)));
			widgets.add(WidgetUtil.textured(getRenderedSlot(display.getRecipe(), outputIndex), origin.x + 131 + 19 * outputIndex, origin.y + 50));
		}
		REIAnimatedHeavyPress press = new REIAnimatedHeavyPress(false);
		press.setPos(new Point(origin.getX() + (getDisplayWidth(display) / 2 - 17), origin.getY() + 22));
		widgets.add(press);
		return widgets;
	}
}
