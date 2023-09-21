package si.bismuth.network.server;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import si.bismuth.network.BisPacket;

public interface ServerPacket extends BisPacket {
	void handle(ServerPlayerEntity player);
}
