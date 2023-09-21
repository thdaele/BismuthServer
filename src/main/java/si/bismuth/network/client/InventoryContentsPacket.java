package si.bismuth.network.client;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DefaultedList;

public class InventoryContentsPacket implements ClientPacket {

	private DefaultedList<ItemStack> contents;

	public InventoryContentsPacket() {
	}

	public InventoryContentsPacket(DefaultedList<ItemStack> contents) {
		this.contents = contents;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		final int size = buffer.readVarInt();
		this.contents = DefaultedList.of(size, ItemStack.EMPTY);
		for (int i = 0; i < size; i++) {
			this.contents.set(i, buffer.readItemStack());
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(this.contents.size());
		for (ItemStack stack : this.contents) {
			buffer.writeItemStack(stack);
		}
	}

	@Override
	public String getChannel() {
		return "Bis|InvContents";
	}

	@Override
	public void handle() {
		// TODO use data for something ig
	}
}
