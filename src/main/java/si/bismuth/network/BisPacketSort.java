package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import si.bismuth.utils.InventoryHelper;

// Stolen from/based on code from https://github.com/kyrptonaught/Inventory-Sorter
@PacketChannelName("sort")
public class BisPacketSort extends BisPacket {
	private boolean isPlayerInv;

	public BisPacketSort() {
		// noop
	}

	@Override
	public void writePacketData() {
		// noop
	}

	@Override
	public void readPacketData(PacketBuffer buf) {
		this.isPlayerInv = buf.readBoolean();
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		if (player.isSpectator()) {
			return;
		}

		if (this.isPlayerInv) {
			InventoryHelper.sortInv(player.inventory, 9, 27);
		} else {
			final IInventory inv = player.openContainer.getSlot(0).inventory;
			InventoryHelper.sortInv(inv, 0, inv.getSizeInventory());
		}
	}
}
