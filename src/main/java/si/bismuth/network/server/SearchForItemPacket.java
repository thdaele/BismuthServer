package si.bismuth.network.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.utils.InventoryHelper;

import java.io.IOException;

public class SearchForItemPacket implements ServerPacket {
	private ItemStack stack;
	private DefaultedList<BlockPos> result;

	public SearchForItemPacket() {
		// noop
	}

	public SearchForItemPacket(DefaultedList<BlockPos> result) {
		this.result = result;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.stack = buffer.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(this.result.size());
		for (BlockPos pos : this.result) {
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public String getChannel() {
		return "Bis|searchforitem";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		InventoryHelper.processFindItem(player, this.stack);
	}
}
