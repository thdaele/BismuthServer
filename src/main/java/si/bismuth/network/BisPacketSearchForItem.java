package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

@PacketChannelName("searchforitem")
public class BisPacketSearchForItem extends BisPacket {
	private ItemStack stack;

	public BisPacketSearchForItem() {
		// noop
	}

	public BisPacketSearchForItem(ItemStack stackIn) {
		this.stack = stackIn.copy();
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeItemStack(this.stack);
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.stack = buf.readItemStack();
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
	}
}
