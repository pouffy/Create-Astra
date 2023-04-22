package com.pouffy.create_astra.foundation.registry;

import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressBlock;
import com.pouffy.create_astra.foundation.ModGroup;
import com.pouffy.create_astra.foundation.util.AstraSpriteShifts;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.base.CasingBlock;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.simibubi.create.content.contraptions.components.AssemblyOperatorBlockItem;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.pouffy.create_astra.CreateAstra.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class BlockRegistry {
	static {
		REGISTRATE.creativeModeTab(() -> ModGroup.MAIN);
	}
	public static final BlockEntry<HeavyPressBlock> HEAVY_PRESS =
			REGISTRATE.block("heavy_press", HeavyPressBlock::new)
					.initialProperties(SharedProperties::stone)
					.properties(p -> p.color(MaterialColor.PODZOL))
					.properties(BlockBehaviour.Properties::noOcclusion)
					.transform(axeOrPickaxe())
					.blockstate(BlockStateGen.horizontalBlockProvider(true))
					.transform(BlockStressDefaults.setImpact(8.0))
					.item(AssemblyOperatorBlockItem::new)
					.transform(customItemModel())
					.register();
	public static final BlockEntry<CasingBlock> DESH_CASING = REGISTRATE.block("desh_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.COLOR_ORANGE))
			.transform(BuilderTransformers.casing(() -> AstraSpriteShifts.DESH_CASING))
			.properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
			.register();

	public static void register() {}

}
