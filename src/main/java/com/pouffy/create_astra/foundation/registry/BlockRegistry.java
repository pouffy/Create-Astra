package com.pouffy.create_astra.foundation.registry;

import static com.pouffy.create_astra.CreateAstra.REGISTRATE;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.content.contraptions.heavy_press.HeavyPressBlock;
import com.pouffy.create_astra.content.logistics.funnel.AstraBeltFunnelBlock;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelItem;
import com.pouffy.create_astra.content.logistics.funnel.desh.DeshFunnelBlock;
import com.pouffy.create_astra.foundation.ModGroup;
import com.pouffy.create_astra.foundation.data.AstraBeltFunnelGenerator;
import com.pouffy.create_astra.foundation.data.AstraFunnelGenerator;
import com.pouffy.create_astra.foundation.util.AstraSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.base.CasingBlock;
import com.simibubi.create.content.contraptions.components.AssemblyOperatorBlockItem;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelGenerator;
import com.simibubi.create.content.logistics.block.funnel.FunnelMovementBehaviour;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

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
					.transform(BlockStressDefaults.setImpact(16.0))
					.item(AssemblyOperatorBlockItem::new)
					.transform(customItemModel())
					.register();
	public static final BlockEntry<CasingBlock> DESH_CASING = REGISTRATE.block("desh_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.COLOR_ORANGE))
			.transform(BuilderTransformers.casing(() -> AstraSpriteShifts.DESH_CASING))
			.properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
			.register();
	public static final BlockEntry<CasingBlock> CALORITE_CASING = REGISTRATE.block("calorite_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.TERRACOTTA_RED))
			.transform(BuilderTransformers.casing(() -> AstraSpriteShifts.CALORITE_CASING))
			.properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
			.register();
	public static final BlockEntry<CasingBlock> OSTRUM_CASING = REGISTRATE.block("ostrum_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.TERRACOTTA_BROWN))
			.transform(BuilderTransformers.casing(() -> AstraSpriteShifts.OSTRUM_CASING))
			.properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
			.register();
	public static final BlockEntry<DeshFunnelBlock> DESH_FUNNEL =
			REGISTRATE.block("desh_funnel", DeshFunnelBlock::new)
					.initialProperties(SharedProperties::softMetal)
					.properties(p -> p.color(MaterialColor.COLOR_ORANGE))
					.transform(pickaxeOnly())
					.tag(AllTags.AllBlockTags.SAFE_NBT.tag)
					.onRegister(movementBehaviour(FunnelMovementBehaviour.brass()))
					.blockstate(new AstraFunnelGenerator("desh", true)::generate)
					.item(AstraFunnelItem::new)
					.model(AstraFunnelGenerator.itemModel("desh"))
					.build()
					.register();
	public static final BlockEntry<AstraBeltFunnelBlock> DESH_BELT_FUNNEL =
			REGISTRATE.block("desh_belt_funnel", p -> new AstraBeltFunnelBlock(BlockRegistry.DESH_FUNNEL, p))
					.initialProperties(SharedProperties::softMetal)
					.properties(p -> p.color(MaterialColor.TERRACOTTA_YELLOW))
					.transform(pickaxeOnly())
					.tag(AllTags.AllBlockTags.SAFE_NBT.tag)
					.blockstate(new AstraBeltFunnelGenerator("desh", CreateAstra.asResource("block/desh_casing"))::generate)
					.loot((p, b) -> p.dropOther(b, DESH_FUNNEL.get()))
					.register();

	public static void register() {}

}
