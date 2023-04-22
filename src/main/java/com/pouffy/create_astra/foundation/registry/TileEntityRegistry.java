package com.pouffy.create_astra.foundation.registry;

import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressBlockEntity;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressInstance;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressRenderer;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.pouffy.create_astra.CreateAstra.REGISTRATE;

public class TileEntityRegistry {
	public static final BlockEntityEntry<HeavyPressBlockEntity> MECHANICAL_PRESS = REGISTRATE
			.tileEntity("heavy_press", HeavyPressBlockEntity::new)
			.instance(() -> HeavyPressInstance::new)
			.validBlocks(AllBlocks.MECHANICAL_PRESS)
			.renderer(() -> HeavyPressRenderer::new)
			.register();

	public static void register() {}

}
