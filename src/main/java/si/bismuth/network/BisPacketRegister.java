package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

@PacketChannelName("register")
public class BisPacketRegister extends BisPacket {
	private static final int CURRENT_BISMUTH_PROTOCOL_ID = 1;

	public BisPacketRegister() {
		// noop
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeVarInt(CURRENT_BISMUTH_PROTOCOL_ID);
	}

	@Override
	public void readPacketData(PacketBuffer buf) {
		// noop
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		// noop
	}
}
