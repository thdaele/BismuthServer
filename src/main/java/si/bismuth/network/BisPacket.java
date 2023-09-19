package si.bismuth.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.io.IOException;

public abstract class BisPacket {
	private final PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());

	public abstract void writePacketData();

	public abstract void readPacketData(PacketByteBuf buf) throws IOException;

	public abstract void processPacket(ServerPlayerEntity player);

	public PacketByteBuf getPacketBuffer() {
		return this.data;
	}
}
