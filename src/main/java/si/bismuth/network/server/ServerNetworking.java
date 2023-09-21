package si.bismuth.network.server;

import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.scoreboard.ServerScoreboard;
import net.minecraft.server.world.ServerWorld;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import si.bismuth.BismuthServer;
import si.bismuth.network.BisPacket;
import si.bismuth.network.client.ScorePacket;

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

		this.init();
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

	private void init() {
		// TODO: perhaps move this to an event that is dispatched
		// to the server whenever compatible clients connect
		ServerConnectionEvents.PLAY_READY.register((server, player) -> {
			String scoreChannel = new ScorePacket().getChannel();

			if (ServerPlayNetworking.canSend(player, scoreChannel)) {
				Set<ScoreboardObjective> objectives = new HashSet<>();
				ServerWorld world = player.getServerWorld();
				ServerScoreboard scoreboard = (ServerScoreboard)world.getScoreboard();

				for (int i = 0; i < 19; ++i) {
					ScoreboardObjective objective = scoreboard.getDisplayObjective(i);

					if (objective != null && !objectives.contains(objective)) {
						for (Packet<?> packet : scoreboard.createStartDisplayingObjectivePackets(objective)) {
							player.networkHandler.sendPacket(packet);
						}

						objectives.add(objective);
					}
				}
			}
		});
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
