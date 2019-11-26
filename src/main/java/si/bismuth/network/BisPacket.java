package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public abstract class BisPacket {
	private final String channel;
	private PacketBuffer data = new PacketBuffer(Unpooled.buffer());

	public BisPacket(String channelIn) {
		this.channel = channelIn;
	}

	public abstract void writePacketData();

	public abstract void readPacketData(PacketBuffer buf);

	public abstract void processPacket(EntityPlayer player);

	public String getChannelName() {
		return "Bis|" + this.channel;
	}

	public PacketBuffer getPacketBuffer() {
		return this.data;
	}
}
