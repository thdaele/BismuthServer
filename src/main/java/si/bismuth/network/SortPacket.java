package si.bismuth.network;

import java.io.IOException;

import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import si.bismuth.utils.InventoryHelper;

// Stolen from/based on code from https://github.com/kyrptonaught/Inventory-Sorter
public class SortPacket implements BisPacket {
	private boolean isPlayerInv;

	public SortPacket() {
		// noop
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.isPlayerInv = buffer.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		// noop
	}

	@Override
	public String getChannel() {
		return "Bis|sort";
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
