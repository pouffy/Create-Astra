package com.pouffy.create_astra.foundation.util;

import com.pouffy.create_astra.CreateAstra;
import com.simibubi.create.foundation.data.LangPartial;
import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.simibubi.create.foundation.utility.Lang;

public enum AstraLangPartials implements LangPartial {

	//ADVANCEMENTS("Advancements", AllAdvancements::provideLangEntries),
	INTERFACE("UI & Messages"),
	//SUBTITLES("Subtitles", AllSoundEvents::provideLangEntries),
	TOOLTIPS("Item Descriptions")
	//PONDER("Ponder Content", PonderLocalization::provideLangEntries),

			;

	private final String displayName;
	private final Supplier<JsonElement> provider;

	private AstraLangPartials(String displayName) {
		this.displayName = displayName;
		String fileName = Lang.asId(name());
		this.provider = () -> LangPartial.fromResource(CreateAstra.ID, fileName);
	}

	private AstraLangPartials(String displayName, Supplier<JsonElement> provider) {
		this.displayName = displayName;
		this.provider = provider;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public JsonElement provide() {
		return provider.get();
	}
}
