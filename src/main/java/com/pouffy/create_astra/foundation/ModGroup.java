package com.pouffy.create_astra.foundation;

import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.foundation.registry.ItemRegistry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
public class ModGroup {
	public static CreativeModeTab MAIN = FabricItemGroupBuilder.build(new ResourceLocation(CreateAstra.ID, "main"), () -> new ItemStack(ItemRegistry.CRUSHED_DESH.get()));
}
