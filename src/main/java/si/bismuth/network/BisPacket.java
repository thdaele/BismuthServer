package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public abstract class BisPacket {
	private PacketBuffer data = new PacketBuffer(Unpooled.buffer());
	private static String channel;

	public abstract void writePacketData();

	public abstract void readPacketData(PacketBuffer buf) throws IOException;

	public abstract void processPacket(EntityPlayerMP player);

	public static String getChannelName() {
		return channel;
	}

	public static void setChannelName(String name) {
		channel = "Bis|" + name;
	}

	public PacketBuffer getPacketBuffer() {
		return this.data;
	}
}
