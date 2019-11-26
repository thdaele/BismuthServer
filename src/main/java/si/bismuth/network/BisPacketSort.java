package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class BisPacketSort extends BisPacket {
	private boolean isPlayerInv;
	private int invIndex;

	public BisPacketSort(boolean playerInv, int index) {
		super("sort");
		this.isPlayerInv = playerInv;
		this.invIndex = index;
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeBoolean(this.isPlayerInv);
		buf.writeVarInt(this.invIndex);
	}

	@Override
	public void readPacketData(PacketBuffer buf) {
		this.isPlayerInv = buf.readBoolean();
		this.invIndex = buf.readVarInt();
	}

	@Override
	public void processPacket(EntityPlayer player) {
		// TODO
	}
}
