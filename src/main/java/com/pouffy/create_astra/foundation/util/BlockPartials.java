package com.pouffy.create_astra.foundation.util;

import com.jozufozu.flywheel.core.PartialModel;
import com.pouffy.create_astra.CreateAstra;

public class BlockPartials {
	public static final PartialModel
			MECHANICAL_PRESS_HEAD = block("heavy_press/head")
			;

	private static PartialModel block(String path) {
		return new PartialModel(CreateAstra.asResource("block/" + path));
	}

	private static PartialModel entity(String path) {
		return new PartialModel(CreateAstra.asResource("entity/" + path));
	}

	public static void init() {
		// init static fields
	}
}
