package si.bismuth.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

// Stolen from/based on code from https://github.com/kyrptonaught/Inventory-Sorter
public class SortPacket implements ClientPacket {
	private boolean isPlayerInv;

	public SortPacket() {
		// noop
	}

	public SortPacket(Boolean isPlayerInv) {
		this.isPlayerInv = isPlayerInv;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		// noop
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBoolean(this.isPlayerInv);
	}

	@Override
	public String getChannel() {
		return "Bis|sort";
	}

	@Override
	public void handle(Minecraft minecraft) {
		// noop
	}
}
