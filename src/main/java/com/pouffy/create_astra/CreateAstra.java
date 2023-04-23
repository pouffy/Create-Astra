package com.pouffy.create_astra;

import com.github.alexnijjar.ad_astra.AdAstra;
import com.pouffy.create_astra.foundation.data.AstaRecipeGen;
import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.pouffy.create_astra.foundation.registry.PacketRegistry;
import com.pouffy.create_astra.foundation.registry.RecipeRegistry;
import com.pouffy.create_astra.foundation.registry.TileEntityRegistry;

import com.pouffy.create_astra.foundation.util.AstraLangPartials;
import com.simibubi.create.foundation.data.LangMerger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pouffy.create_astra.foundation.registry.ItemRegistry;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

@SuppressWarnings("removal")
public class CreateAstra implements ModInitializer {
	public static final String ID = "create_astra";
	public static final String NAME = "Rotae Ex Astris";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);
	@Override
	public void onInitialize() {
		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);

		ItemRegistry.register();
		TileEntityRegistry.register();
		BlockRegistry.register();
		RecipeRegistry.register();
		PacketRegistry.registerPackets();
		PacketRegistry.channel.initServerListener();
		REGISTRATE.register();
	}
	public static void gatherData(FabricDataGenerator gen, ExistingFileHelper helper) {
		AstaRecipeGen.registerAll(gen);
		gen.addProvider(new LangMerger(gen, ID, "Rotae Ex Astris", AstraLangPartials.values()));
	}
	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
	public static ResourceLocation asAdAstraResource(String path) {
		return new ResourceLocation(AdAstra.MOD_ID, path);
	}
	public static CreateRegistrate registrate() {
		return REGISTRATE;
	}
}
