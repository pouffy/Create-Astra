package com.pouffy.create_astra.compat.recipe_viewers.animations;

import com.pouffy.create_astra.foundation.registry.BlockRegistry;
import com.pouffy.create_astra.foundation.util.AstraBlockPartials;
import com.simibubi.create.compat.rei.category.animations.AnimatedKinetics;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.core.Direction.Axis;
public class REIAnimatedHeavyPress extends AnimatedKinetics {

	private boolean basin;

	public REIAnimatedHeavyPress(boolean basin) {
		this.basin = basin;
	}

	@Override
	public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
		int scale = basin ? 23 : 24;

		blockElement(shaft(Axis.Z))
				.rotateBlock(0, 0, getCurrentAngle())
				.scale(scale)
				.render(matrixStack);

		blockElement(BlockRegistry.HEAVY_PRESS.getDefaultState())
				.scale(scale)
				.render(matrixStack);

		blockElement(AstraBlockPartials.MECHANICAL_PRESS_HEAD)
				.atLocal(0, -getAnimatedHeadOffset(), 0)
				.scale(scale)
				.render(matrixStack);

		if (basin)
			blockElement(AllBlocks.BASIN.getDefaultState())
					.atLocal(0, 1.65, 0)
					.scale(scale)
					.render(matrixStack);

		matrixStack.popPose();
	}

	private float getAnimatedHeadOffset() {
		float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
		if (cycle < 10) {
			float progress = cycle / 10;
			return -(progress * progress * progress);
		}
		if (cycle < 15)
			return -1;
		if (cycle < 20)
			return -1 + (1 - ((20 - cycle) / 5));
		return 0;
	}

}
