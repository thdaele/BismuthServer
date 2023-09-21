package si.bismuth.network;

import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.scoreboard.ServerScoreboard;
import net.minecraft.server.world.ServerWorld;

import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

import si.bismuth.MCServer;

import java.util.*;
import java.util.function.Supplier;

public class BismuthNetworking {
	private final Set<String> allChannels = new HashSet<>();

	public BismuthNetworking() {
		this.registerPacket(GetInventoryPacket::new);
		this.registerPacket(SearchForItemPacket::new);
		this.registerPacket(SortPacket::new);
		this.registerPacket(UpdateScorePacket::new);
		this.registerPacket(FakeCarpetClientSupport::new);

		this.init();
	}

	private <T extends BisPacket> void registerPacket(Supplier<T> initializer) {
		BisPacket p = initializer.get();
		String channel = p.getChannel();

		if (this.allChannels.contains(channel)) {
			MCServer.log.error("attempted to register packet '{}' on channel '{}' but it already exists!", p.getClass().getSimpleName(), channel);
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
			String scoreChannel = new UpdateScorePacket().getChannel();

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
