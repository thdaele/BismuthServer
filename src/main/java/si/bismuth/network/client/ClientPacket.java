package si.bismuth.network.client;

import net.minecraft.client.Minecraft;
import si.bismuth.network.BisPacket;

public interface ClientPacket extends BisPacket {
	void handle(Minecraft minecraft);
}
