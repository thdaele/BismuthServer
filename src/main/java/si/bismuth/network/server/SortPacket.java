package si.bismuth.network.server;

import java.io.IOException;

import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import si.bismuth.utils.InventoryHelper;

public class SortPacket implements ServerPacket {

	private boolean isPlayerInv;

	public SortPacket() {
	}

	public SortPacket(boolean isPlayerInv) {
		this.isPlayerInv = isPlayerInv;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.isPlayerInv = buffer.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBoolean(this.isPlayerInv);
	}

	@Override
	public String getChannel() {
		return "Bis|Sort";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		if (player.isSpectator()) {
			return;
		}

		if (this.isPlayerInv) {
			InventoryHelper.sortInv(player.inventory, 9, 27);
		} else {
			final Inventory inv = player.menu.getSlot(0).inventory;
			InventoryHelper.sortInv(inv, 0, inv.getSize());
		}
	}
}
