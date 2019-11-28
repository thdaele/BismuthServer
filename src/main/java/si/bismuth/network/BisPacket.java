package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public abstract class BisPacket {
	private PacketBuffer data = new PacketBuffer(Unpooled.buffer());
	private String channel;

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
