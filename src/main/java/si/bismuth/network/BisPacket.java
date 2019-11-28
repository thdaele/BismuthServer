package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public abstract class BisPacket {
	private PacketBuffer data = new PacketBuffer(Unpooled.buffer());
	private static String channel;

	public abstract void writePacketData();

	public abstract void readPacketData(PacketBuffer buf);

	public abstract void processPacket(EntityPlayer player);

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
