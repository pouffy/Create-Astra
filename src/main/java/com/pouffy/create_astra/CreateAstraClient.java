package com.pouffy.create_astra;

import com.pouffy.create_astra.foundation.util.AstraBlockPartials;

import net.fabricmc.api.ClientModInitializer;

public class CreateAstraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AstraBlockPartials.init();
	}
}
