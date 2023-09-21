package si.bismuth.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class GetInventoryPacket implements ClientPacket {
	private BlockPos pos;
	private DefaultedList<ItemStack> result;

	public GetInventoryPacket() {
		// noop
	}

	public GetInventoryPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		final int size = buffer.readVarInt();
		this.result = DefaultedList.of(size, ItemStack.EMPTY);
		for (int i = 0; i < size; i++) {
			this.result.set(i, buffer.readItemStack());
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
	}

	@Override
	public String getChannel() {
		return "Bis|getinventory";
	}

	@Override
	public void handle(Minecraft minecraft) {
		//TODO use data for something ig
	}
}
