package com.pouffy.create_astra.foundation.registry;

import static com.pouffy.create_astra.CreateAstra.registrate;

import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.pouffy.create_astra.foundation.ModGroup;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;

public class ItemRegistry {
	private static final CreateRegistrate REGISTRATE = registrate()
			.creativeModeTab(() -> ModGroup.MAIN);
	public static final ItemEntry<Item> CRUSHED_DESH =
			REGISTRATE.item("crushed_desh_ore", Item::new)
					.register();
	public static final ItemEntry<Item> CRUSHED_CALORITE =
			REGISTRATE.item("crushed_calorite_ore", Item::new)
					.register();
	public static final ItemEntry<Item> CRUSHED_OSTRUM =
			REGISTRATE.item("crushed_ostrum_ore", Item::new)
					.register();

	public static void register() {
		Create.REGISTRATE.addToSection(CRUSHED_DESH, AllSections.MATERIALS);
	}
}
