package si.bismuth.network.server;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import si.bismuth.BismuthServer;
import si.bismuth.network.BisPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ServerNetworking {
	private final Set<String> allChannels = new HashSet<>();

	public ServerNetworking() {
		this.registerListener(QueryInventoryPacket::new);
		this.registerListener(FindItemPacket::new);
		this.registerListener(SortPacket::new);
		this.registerListener(FakeCarpetClientSupport::new);
	}

	private <T extends ServerPacket> void registerListener(Supplier<T> initializer) {
		BisPacket p = initializer.get();
		String channel = p.getChannel();

		if (this.allChannels.contains(channel)) {
			BismuthServer.log.error("attempted to register packet '{}' on channel '{}' but it already exists!", p.getClass().getSimpleName(), channel);
		} else {
			this.allChannels.add(channel);

			ServerPlayNetworking.registerListener(channel, initializer, (server, handler, player, packet) -> {
				packet.handle(player);
				return true;
			});
		}
	}

	public void sendPacket(ServerPlayerEntity player, BisPacket packet) {
		ServerPlayNetworking.send(player, packet.getChannel(), packet);
	}

	public void sendPacket(Iterable<ServerPlayerEntity> players, BisPacket packet) {
		ServerPlayNetworking.send(players, packet.getChannel(), packet);
	}

	public void sendPacket(int dimension, BisPacket packet) {
		ServerPlayNetworking.send(dimension, packet.getChannel(), packet);
	}

	public void sendPacket(BisPacket packet) {
		ServerPlayNetworking.send(packet.getChannel(), packet);
	}
}
