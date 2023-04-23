package com.pouffy.create_astra.foundation.registry;

import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressBlockEntity;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressInstance;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressRenderer;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelInstance;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelRenderer;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelTileEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.pouffy.create_astra.CreateAstra.REGISTRATE;

public class TileEntityRegistry {
	public static final BlockEntityEntry<HeavyPressBlockEntity> MECHANICAL_PRESS = REGISTRATE
			.tileEntity("heavy_press", HeavyPressBlockEntity::new)
			.instance(() -> HeavyPressInstance::new)
			.validBlocks(BlockRegistry.HEAVY_PRESS)
			.renderer(() -> HeavyPressRenderer::new)
			.register();
	public static final BlockEntityEntry<AstraFunnelTileEntity> FUNNEL = REGISTRATE
			.tileEntity("astra_funnel", AstraFunnelTileEntity::new)
			.instance(() -> AstraFunnelInstance::new)
			.validBlocks(BlockRegistry.DESH_BELT_FUNNEL, BlockRegistry.DESH_FUNNEL)
			.renderer(() -> AstraFunnelRenderer::new)
			.register();

	public static void register() {}

}
