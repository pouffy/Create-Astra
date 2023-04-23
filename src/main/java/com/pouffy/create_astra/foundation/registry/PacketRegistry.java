package com.pouffy.create_astra.foundation.registry;

import com.pouffy.create_astra.CreateAstra;
import com.pouffy.create_astra.content.logistics.funnel.AstraFunnelFlapPacket;

import com.simibubi.create.foundation.networking.SimplePacketBase;

import com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;


import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection.PLAY_TO_CLIENT;
import static com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection.PLAY_TO_SERVER;

public enum PacketRegistry {
	ASTRA_FUNNEL_FLAP(AstraFunnelFlapPacket.class, AstraFunnelFlapPacket::new, PLAY_TO_CLIENT)
	;
	public static final ResourceLocation CHANNEL_NAME = CreateAstra.asResource("main");
	public static final int NETWORK_VERSION = 2;
	public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
	public static SimpleChannel channel;

	private LoadedPacket<?> packet;

	<T extends SimplePacketBase> PacketRegistry(Class<T> type, Function<FriendlyByteBuf, T> factory,
											NetworkDirection direction) {
		packet = new LoadedPacket<>(type, factory, direction);
	}

	public static void registerPackets() {
		channel = new SimpleChannel(CHANNEL_NAME);
		int id = 0;
		for (PacketRegistry packet : values()) {
			boolean registered = false;
			if (packet.packet.direction == PLAY_TO_SERVER) {
				channel.registerC2SPacket(packet.packet.type, id++);
				registered = true;
			}
			if (packet.packet.direction == PLAY_TO_CLIENT) {
				channel.registerS2CPacket(packet.packet.type, id++);
				registered = true;
			}
			if (!registered) {
				CreateAstra.LOGGER.error("Could not register packet with type " + packet.packet.type);
			}
		}
	}

	public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
		channel.sendToClientsAround((S2CPacket) message, (ServerLevel) world, pos, range);
	}

	private static class LoadedPacket<T extends SimplePacketBase> {
		private static int index = 0;

		private BiConsumer<T, FriendlyByteBuf> encoder;
		private Function<FriendlyByteBuf, T> decoder;
		private BiConsumer<T, Supplier<SimplePacketBase.Context>> handler;
		private Class<T> type;
		private NetworkDirection direction;

		private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
			encoder = T::write;
			decoder = factory;
			handler = T::handle;
			this.type = type;
			this.direction = direction;
		}

//		private void register() {
//			channel.registerC2SPacket();
//			channel.messageBuilder(type, index++, direction)
//				.encoder(encoder)
//				.decoder(decoder)
//				.consumer(handler)
//				.add();
//		}
	}

}
