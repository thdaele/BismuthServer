package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public abstract class BisPacket {
	private PacketBuffer data = new PacketBuffer(Unpooled.buffer());

	public abstract void writePacketData();

	public abstract void readPacketData(PacketBuffer buf) throws IOException;

	public abstract void processPacket(EntityPlayerMP player);

	public PacketBuffer getPacketBuffer() {
		return this.data;
	}
}
