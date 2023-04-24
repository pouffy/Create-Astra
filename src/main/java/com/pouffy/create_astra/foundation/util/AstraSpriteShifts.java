package com.pouffy.create_astra.foundation.util;

import com.pouffy.create_astra.CreateAstra;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

public class AstraSpriteShifts {
	public static final CTSpriteShiftEntry DESH_CASING = omni("desh_casing");
	public static final CTSpriteShiftEntry CALORITE_CASING = omni("calorite_casing");
	public static final CTSpriteShiftEntry OSTRUM_CASING = omni("ostrum_casing");

	private static CTSpriteShiftEntry omni(String name) {
		return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
	}

	private static CTSpriteShiftEntry horizontal(String name) {
		return getCT(AllCTTypes.HORIZONTAL, name);
	}

	private static CTSpriteShiftEntry vertical(String name) {
		return getCT(AllCTTypes.VERTICAL, name);
	}
	private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
		return SpriteShifter.get(Create.asResource(originalLocation), Create.asResource(targetLocation));
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
		return CTSpriteShifter.getCT(type, CreateAstra.asResource("block/" + blockTextureName), CreateAstra.asResource("block/" + connectedTextureName + "_connected"));
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
		return getCT(type, blockTextureName, blockTextureName);
	}
}
