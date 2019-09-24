package si.bismuth.patches;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;

class NetworkManagerFake extends NetworkManager {
	NetworkManagerFake() {
		super(EnumPacketDirection.CLIENTBOUND);
	}

	public void disableAutoRead() {
	}
}
