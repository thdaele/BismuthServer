package si.bismuth.network.client;

import si.bismuth.network.BisPacket;

public interface ClientPacket extends BisPacket {
	void handle();
}
