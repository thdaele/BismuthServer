package si.bismuth.network.client;

import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import si.bismuth.BismuthServer;
import si.bismuth.network.BisPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ClientNetworking {
	private final Set<String> allChannels = new HashSet<>();

	public ClientNetworking() {

	}

	private <T extends ClientPacket> void registerPacket(Supplier<T> initializer) {
		BisPacket p = initializer.get();
		String channel = p.getChannel();

		if (this.allChannels.contains(channel)) {
			BismuthServer.log.error("attempted to register packet '{}' on channel '{}' but it already exists!", p.getClass().getSimpleName(), channel);
		} else {
			this.allChannels.add(channel);

			ClientPlayNetworking.registerListener(channel, initializer, (minecraft, handler, packet) -> {
				packet.handle(minecraft);
				return true;
			});
		}
	}

	public void sendPacket(BisPacket packet) {
		ClientPlayNetworking.send(packet.getChannel(), packet);
	}
}
