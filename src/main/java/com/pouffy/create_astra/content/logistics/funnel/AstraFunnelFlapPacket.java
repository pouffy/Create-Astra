package com.pouffy.create_astra.content.logistics.funnel;

import com.simibubi.create.foundation.networking.TileEntityDataPacket;

import net.minecraft.network.FriendlyByteBuf;

public class AstraFunnelFlapPacket extends TileEntityDataPacket<AstraFunnelTileEntity> {

	private final boolean inwards;

	public AstraFunnelFlapPacket(FriendlyByteBuf buffer) {
		super(buffer);

		inwards = buffer.readBoolean();
	}

	public AstraFunnelFlapPacket(AstraFunnelTileEntity tile, boolean inwards) {
		super(tile.getBlockPos());
		this.inwards = inwards;
	}

	@Override
	protected void writeData(FriendlyByteBuf buffer) {
		buffer.writeBoolean(inwards);
	}

	@Override
	protected void handlePacket(AstraFunnelTileEntity tile) {
		tile.flap(inwards);
	}
}
