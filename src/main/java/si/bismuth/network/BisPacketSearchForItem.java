package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.utils.InventoryHelper;

import java.io.IOException;

@PacketChannelName("searchforitem")
public class BisPacketSearchForItem extends BisPacket {
	private ItemStack stack;
	private NonNullList<BlockPos> result;

	public BisPacketSearchForItem() {
		// noop
	}

	public BisPacketSearchForItem(NonNullList<BlockPos> listIn) {
		this.result = listIn;
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeVarInt(this.result.size());
		for (BlockPos pos : this.result) {
			buf.writeBlockPos(pos);
		}
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.stack = buf.readItemStack();
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		InventoryHelper.processFindItem(player, this.stack);
	}
}
