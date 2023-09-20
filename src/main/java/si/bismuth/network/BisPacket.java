package si.bismuth.network;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import net.ornithemc.osl.networking.api.CustomPayload;

public interface BisPacket extends CustomPayload {
	String getChannel();

	void handle(ServerPlayerEntity player);
}
